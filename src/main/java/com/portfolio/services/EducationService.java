package com.portfolio.services;

import com.portfolio.dtos.EducationRequest;
import com.portfolio.dtos.EducationResponse;
import com.portfolio.dtos.ProjectResponse;
import com.portfolio.entities.Education;
import com.portfolio.entities.Profile;
import com.portfolio.entities.Project;
import com.portfolio.enums.DegreeEnum;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.EducationRepository;
import com.portfolio.repositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EducationService {

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private ProfileRepository profileRepository;

    // ---------------- CREATE EDUCATION ----------------
    public EducationResponse createEducation(EducationRequest request) throws GenericException {
        Profile profile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found"));

        // Optional: enforce unique degree per profile
        if (request.getDegree() != null && educationRepository.findByDegreeAndProfile(request.getDegree(), profile) != null) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_DEGREE,
                    "Education with degree " + request.getDegree() + " already exists for this profile");
        }

        Education education = Education.builder()
                .institution(request.getInstitution())
                .degree(request.getDegree())
                .location(request.getLocation())
                .fieldOfStudy(request.getFieldOfStudy())
                .startYear(request.getStartYear())
                .endYear(request.getEndYear())
                .description(request.getDescription())
                .grade(request.getGrade())
                .profile(profile)
                .build();

        return toDto(educationRepository.save(education));
    }

    // ---------------- UPDATE EDUCATION (by id) ----------------
    public EducationResponse updateEducation(Integer id, EducationRequest request) throws GenericException {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EDUCATION_NOT_FOUND, "Education not found"));

        // Validate profile match (prevent updating another profile's education)
        if (education.getProfile().getId()!=request.getProfileId()) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile mismatch");
        }

        // If degree is changed in request and you want to prevent that, you can enforce:
        if (request.getDegree() != null && education.getDegree() != null
                && !education.getDegree().equals(request.getDegree())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Degree cannot be changed");
        }

        // Update fields
        education.setInstitution(request.getInstitution());
        education.setLocation(request.getLocation());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartYear(request.getStartYear());
        education.setEndYear(request.getEndYear());
        education.setDescription(request.getDescription());
        education.setGrade(request.getGrade());

        return toDto(educationRepository.save(education));
    }

    // ---------------- GET EDUCATION BY ID ----------------
    public EducationResponse findById(Integer id, Integer profileId) throws GenericException {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EDUCATION_NOT_FOUND, "Education not found"));

        if (education.getProfile().getId()!=profileId) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile mismatch");
        }

        return toDto(education);
    }

    // ---------------- DELETE EDUCATION BY ID ----------------
    @Transactional
    public String delete(Integer id, Integer profileId) throws GenericException {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EDUCATION_NOT_FOUND, "Education not found"));

        if (education.getProfile().getId()!=profileId) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile mismatch");
        }

        educationRepository.delete(education);
        return "Education deleted successfully";
    }

    // ---------------- GET EDUCATION BY PROFILE (PAGINATED + SEARCH) ----------------
    public Page<EducationResponse> getEducationByProfileId(Integer profileId, Pageable pageable, String search) {
        return educationRepository.findByProfileIdWithSearch(profileId, search, pageable)
                .map(this::toDto);
    }

    // ---------------- GET EDUCATION BY PROFILE -----------------
    public List<EducationResponse> getEducationByProfileId(Integer profileId) {
        List<Education> educations = educationRepository.findByProfileId(profileId);
        return educations.stream().map(this::toDto).collect(Collectors.toList());
    }

    // ---------------- DTO MAPPING ----------------
    private EducationResponse toDto(Education education) {
        return EducationResponse.builder()
                .id(education.getId())
                .institution(education.getInstitution())
                .degree(education.getDegree())
                .location(education.getLocation())
                .fieldOfStudy(education.getFieldOfStudy())
                .startYear(education.getStartYear())
                .endYear(education.getEndYear())
                .description(education.getDescription())
                .grade(education.getGrade())
                .build();
    }
}
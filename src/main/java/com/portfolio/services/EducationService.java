package com.portfolio.services;

import com.portfolio.dtos.EducationRequest;
import com.portfolio.dtos.EducationResponse;
import com.portfolio.entities.Education;
import com.portfolio.entities.Profile;
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

        if (educationRepository.findByDegreeAndProfile(request.getDegree(), profile) != null) {
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

    // ---------------- UPDATE EDUCATION (Degree cannot change) ----------------
    public EducationResponse updateEducation(DegreeEnum degree, EducationRequest request) throws GenericException {
        Profile profile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found"));

        Education education = educationRepository.findByDegreeAndProfile(degree, profile);
        if (education == null) {
            throw new GenericException(ExceptionCodeEnum.EDUCATION_NOT_FOUND,
                    "Education with degree " + degree + " not found for this profile");
        }

        // Update all fields except degree
        education.setInstitution(request.getInstitution());
        education.setLocation(request.getLocation());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartYear(request.getStartYear());
        education.setEndYear(request.getEndYear());
        education.setDescription(request.getDescription());
        education.setGrade(request.getGrade());

        return toDto(educationRepository.save(education));
    }

    // ---------------- GET EDUCATION BY DEGREE ----------------
    public EducationResponse findByDegree(DegreeEnum degree, Integer profileId) throws GenericException {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found"));

        Education education = educationRepository.findByDegreeAndProfile(degree, profile);
        if (education == null) {
            throw new GenericException(ExceptionCodeEnum.EDUCATION_NOT_FOUND,
                    "Education with degree " + degree + " not found for this profile");
        }

        return toDto(education);
    }

    // ---------------- DELETE EDUCATION ----------------
    @Transactional
    public String delete(DegreeEnum degree, Integer profileId) throws GenericException {
        if (!educationRepository.existsByDegreeAndProfileId(degree, profileId)) {
            throw new GenericException(ExceptionCodeEnum.EDUCATION_NOT_FOUND,
                    "Education not found with degree " + degree + " and profile " + profileId);
        }
        educationRepository.deleteByDegreeAndProfileId(degree, profileId);
        return "Education deleted successfully";
    }

    // ---------------- GET EDUCATION BY PROFILE (PAGINATED + SEARCH) ----------------
    public Page<EducationResponse> getEducationByProfileId(Integer profileId, Pageable pageable, String search) {
        return educationRepository.findByProfileIdWithSearch(profileId, search, pageable)
                .map(this::toDto);
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

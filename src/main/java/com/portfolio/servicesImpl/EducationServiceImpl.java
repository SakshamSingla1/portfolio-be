package com.portfolio.servicesImpl;

import com.portfolio.dtos.EducationRequest;
import com.portfolio.dtos.EducationResponse;
import com.portfolio.entities.Education;
import com.portfolio.entities.NotificationTemplate;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.EducationRepository;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.services.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;

    @Override
    public EducationResponse createEducation(EducationRequest request) throws GenericException {
        if (educationRepository.existsByDegreeAndProfileId(request.getDegree(), request.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_DEGREE, "Education already exists for this degree");
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
                .profileId(request.getProfileId())
                .build();

        return mapToResponse(educationRepository.save(education));
    }

    @Override
    public EducationResponse updateEducation(String id, EducationRequest request) throws GenericException {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EDUCATION_NOT_FOUND, "Education not found"));
        if (!education.getProfileId().equals(request.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile mismatch");
        }
        education.setInstitution(request.getInstitution());
        education.setLocation(request.getLocation());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartYear(request.getStartYear());
        education.setEndYear(request.getEndYear());
        education.setDescription(request.getDescription());
        education.setGrade(request.getGrade());
        return mapToResponse(educationRepository.save(education));
    }

    @Override
    public EducationResponse findById(String id, String profileId) throws GenericException {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EDUCATION_NOT_FOUND, "Education not found"));
        if (!education.getProfileId().equals(profileId)) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile mismatch");
        }
        return mapToResponse(education);
    }

    @Transactional
    @Override
    public String delete(String id, String profileId) throws GenericException {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EDUCATION_NOT_FOUND, "Education not found"));
        if (!education.getProfileId().equals(profileId)) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile mismatch");
        }
        educationRepository.delete(education);
        return "Education deleted successfully";
    }

    @Override
    public Page<EducationResponse> getByProfile(String profileId, String search, String sortDir, String sortBy ,Pageable pageable) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        boolean hasProfileId = profileId != null;
        boolean hasSearch = search != null && !search.isBlank();

        Page<Education> educations;
        if( hasSearch && hasProfileId){
            educations = educationRepository.findByProfileIdWithSearch(profileId,search,sortedPageable);
        }else if(hasSearch){
            educations = educationRepository.findBySearch(search,sortedPageable);
        }else if(hasProfileId) {
            educations = educationRepository.findByProfileId(profileId, sortedPageable);
        }else{
            educations = educationRepository.findAll(sortedPageable);
        }
        return educations.map(this::mapToResponse);
    }

    @Override
    public List<EducationResponse> getByProfile(String profileId) {
        return educationRepository.findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ---------------- DTO MAPPING ----------------
    private EducationResponse mapToResponse(Education education) {
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
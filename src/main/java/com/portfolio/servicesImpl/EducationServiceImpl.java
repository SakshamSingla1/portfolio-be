package com.portfolio.servicesImpl;

import com.portfolio.dao.education.EducationDao;
import com.portfolio.dtos.Education.EducationRequest;
import com.portfolio.dtos.Education.EducationResponse;
import com.portfolio.entities.Education;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationDao educationDao;

    @Override
    public EducationResponse createEducation(EducationRequest request) throws GenericException {
        if (educationDao.existsByDegreeAndProfileId(request.getDegree(), request.getProfileId())) {
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

        return mapToResponse(educationDao.save(education));
    }

    @Override
    public EducationResponse updateEducation(Long id, EducationRequest request) throws GenericException {
        Education education = educationDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EDUCATION_NOT_FOUND, "Education not found"));
        if (!education.getProfileId().equals(request.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile mismatch");
        }
        education.setInstitution(request.getInstitution());
        education.setDegree(request.getDegree());
        education.setLocation(request.getLocation());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartYear(request.getStartYear());
        education.setEndYear(request.getEndYear());
        education.setDescription(request.getDescription());
        education.setGrade(request.getGrade());
        education.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(educationDao.save(education));
    }

    @Override
    public EducationResponse findById(Long id, Long profileId) throws GenericException {
        return educationDao.findDTOByIdAndProfileId(id, profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EDUCATION_NOT_FOUND, "Education not found"));
    }

    @Transactional
    @Override
    public String delete(Long id, Long profileId) throws GenericException {
        Education education = educationDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EDUCATION_NOT_FOUND, "Education not found"));
        if (!education.getProfileId().equals(profileId)) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile mismatch");
        }
        educationDao.delete(education);
        return "Education deleted successfully";
    }

    @Override
    public Page<EducationResponse> getByProfile(Long profileId, String search, Pageable pageable) {
        return educationDao.findByCriteria(profileId,search, pageable);
    }

    @Override
    public List<EducationResponse> getByProfile(Long profileId) {
        return educationDao.findByProfileId(profileId)
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
                .createdAt(education.getCreatedAt())
                .updatedAt(education.getUpdatedAt())
                .build();
    }
}

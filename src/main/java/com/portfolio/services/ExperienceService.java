// src/main/java/com/portfolio/services/ExperienceService.java
package com.portfolio.services;

import com.portfolio.dtos.ExperienceRequest;
import com.portfolio.dtos.ExperienceResponse;
import com.portfolio.entities.Experience;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.ExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExperienceService {

    @Autowired
    ExperienceRepository experienceRepository;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ResponseEntity<ResponseModel<ExperienceResponse>> create(ExperienceRequest req) throws Exception {
        Date parsedStartDate = sdf.parse(req.getStartDate());

        // 🔒 Duplicacy Check
        if (experienceRepository.existsByCompanyNameAndJobTitleAndStartDate(
                req.getCompanyName(), req.getJobTitle(), parsedStartDate)) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_EXPERIENCE,
                    "Experience already exists with same company, title and start date");
        }

        Experience exp = Experience.builder()
                .companyName(req.getCompanyName())
                .jobTitle(req.getJobTitle())
                .location(req.getLocation())
                .startDate(parsedStartDate)
                .endDate(req.getEndDate() != null ? sdf.parse(req.getEndDate()) : null)
                .currentlyWorking(req.isCurrentlyWorking())
                .description(req.getDescription())
                .technologiesUsed(req.getTechnologiesUsed())
                .build();

        Experience saved = experienceRepository.save(exp);
        return ApiResponse.successResponse(mapToResponse(saved), "Experience created successfully");
    }


    public ResponseEntity<ResponseModel<List<ExperienceResponse>>> getAll() {
        List<Experience> list = experienceRepository.findAll();
        List<ExperienceResponse> result = list.stream().map(this::mapToResponse).collect(Collectors.toList());
        return ApiResponse.successResponse(result, "Fetched all experiences");
    }

    public ResponseEntity<ResponseModel<ExperienceResponse>> getById(Integer id) throws GenericException {
        Optional<Experience> optional = experienceRepository.findById(id);
        if (optional.isEmpty()) {
            throw new GenericException(ExceptionCodeEnum.EXPERIENCE_NOT_FOUND, "Experience not found");
        }
        return ApiResponse.successResponse(mapToResponse(optional.get()), "Experience fetched");
    }

    public ResponseEntity<ResponseModel<ExperienceResponse>> update(Integer id, ExperienceRequest req) throws Exception {
        Experience exp = experienceRepository.findById(id).orElseThrow(() ->
                new GenericException(ExceptionCodeEnum.EXPERIENCE_NOT_FOUND, "Experience not found"));

        exp.setCompanyName(req.getCompanyName());
        exp.setJobTitle(req.getJobTitle());
        exp.setLocation(req.getLocation());
        exp.setStartDate(sdf.parse(req.getStartDate()));
        exp.setEndDate(req.getEndDate() != null ? sdf.parse(req.getEndDate()) : null);
        exp.setCurrentlyWorking(req.isCurrentlyWorking());
        exp.setDescription(req.getDescription());
        exp.setTechnologiesUsed(req.getTechnologiesUsed());

        Experience updated = experienceRepository.save(exp);
        return ApiResponse.successResponse(mapToResponse(updated), "Experience updated");
    }

    public ResponseEntity<ResponseModel<String>> delete(Integer id) {
        experienceRepository.deleteById(id);
        return ApiResponse.successResponse("Experience deleted successfully", "Deleted");
    }

    private ExperienceResponse mapToResponse(Experience exp) {
        return ExperienceResponse.builder()
                .id(exp.getId())
                .companyName(exp.getCompanyName())
                .jobTitle(exp.getJobTitle())
                .location(exp.getLocation())
                .startDate(sdf.format(exp.getStartDate()))
                .endDate(exp.getEndDate() != null ? sdf.format(exp.getEndDate()) : null)
                .currentlyWorking(exp.isCurrentlyWorking())
                .description(exp.getDescription())
                .technologiesUsed(exp.getTechnologiesUsed())
                .build();
    }
}

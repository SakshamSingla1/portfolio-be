package com.portfolio.services;

import com.portfolio.dtos.EducationRequest;
import com.portfolio.dtos.EducationResponse;
import com.portfolio.entities.Education;
import com.portfolio.enums.DegreeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.EducationRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EducationService {

    @Autowired
    private EducationRespository educationRespository;

    public ResponseEntity<ResponseModel<EducationResponse>> createEducation(EducationRequest request) {
        Education existingEducation = educationRespository.findByDegree(request.getDegree());
        if (existingEducation != null) {
            return ApiResponse.failureResponse(null, "Education with " + request.getDegree() + " already exists");
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
                .build();
        Education savedEducation = educationRespository.save(education);
        return ApiResponse.successResponse(toDto(savedEducation), "Education created successfully");
    }

    public ResponseEntity<ResponseModel<EducationResponse>> updateEducation(DegreeEnum degree, EducationRequest request) {
        Education education = educationRespository.findByDegree(degree);
        if (education == null) {
            return ApiResponse.failureResponse(null, "Education with " + degree + " doesn't exist");
        }
        education.setInstitution(request.getInstitution());
        education.setDegree(request.getDegree());
        education.setLocation(request.getLocation());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartYear(request.getStartYear());
        education.setEndYear(request.getEndYear());
        education.setDescription(request.getDescription());
        education.setGrade(request.getGrade());
        Education savedEducation = educationRespository.save(education);
        return ApiResponse.successResponse(toDto(savedEducation), "Education updated successfully");
    }

    public ResponseEntity<ResponseModel<EducationResponse>> findByDegree(DegreeEnum degree) {
        Education education = educationRespository.findByDegree(degree);
        if (education == null) {
            return ApiResponse.failureResponse(null, "Education with " + degree + " doesn't exist");
        }
        return ApiResponse.successResponse(toDto(education), "Education found successfully");
    }

    public ResponseEntity<ResponseModel<List<EducationResponse>>> findAllEducations() {
        List<Education> educations = educationRespository.findAll();
        if (educations.isEmpty()) {
            return ApiResponse.failureResponse(null, "No Educations found");
        }
        List<EducationResponse> response = educations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ApiResponse.successResponse(response, "Educations found successfully");
    }

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

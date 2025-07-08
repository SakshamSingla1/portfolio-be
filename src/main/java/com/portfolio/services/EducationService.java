package com.portfolio.services;

import com.portfolio.dtos.EducationRequest;
import com.portfolio.dtos.EducationResponse;
import com.portfolio.entities.Education;
import com.portfolio.enums.DegreeEnum;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.EducationRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EducationService {

    @Autowired
    EducationRespository educationRespository;

    public ResponseEntity<ResponseModel<EducationResponse>> createEducation(EducationRequest request) throws GenericException {
        Education existingEducation = educationRespository.findByDegree(request.getDegree());
        if (existingEducation != null) {
            return ApiResponse.failureResponse(null,"Education with "+ request.getDegree()+" already exists");
        }
        Education education = Education.builder()
                .institution(request.getInstitution())
                .degree(request.getDegree())
                .location(request.getLocation())
                .fieldOfStudy(request.getFieldOfStudy())
                .startYear(request.getStartYear())
                .endYear(request.getEndYear())
                .description(request.getDescription())
                .build();
        Education savedEducation = educationRespository.save(education);
        EducationResponse response = EducationResponse.builder()
                .id(savedEducation.getId())
                .institution(savedEducation.getInstitution())
                .degree(savedEducation.getDegree())
                .location(savedEducation.getLocation())
                .fieldOfStudy(savedEducation.getFieldOfStudy())
                .startYear(savedEducation.getStartYear())
                .endYear(savedEducation.getEndYear())
                .description(savedEducation.getDescription())
                .build();
        return ApiResponse.successResponse(response,"Education created successfully");
    }

    public ResponseEntity<ResponseModel<EducationResponse>> updateEducation(DegreeEnum degree, EducationRequest request) throws GenericException {
        Education education = educationRespository.findByDegree(request.getDegree());
        if (education == null) {
            return ApiResponse.failureResponse(null,"Education with "+ request.getDegree()+" doesn't exist");
        }
        education.setInstitution(request.getInstitution());
        education.setDegree(request.getDegree());
        education.setLocation(request.getLocation());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartYear(request.getStartYear());
        education.setEndYear(request.getEndYear());
        education.setDescription(request.getDescription());
        Education savedEducation = educationRespository.save(education);

        EducationResponse response = EducationResponse.builder()
                .id(savedEducation.getId())
                .institution(savedEducation.getInstitution())
                .degree(savedEducation.getDegree())
                .location(savedEducation.getLocation())
                .fieldOfStudy(savedEducation.getFieldOfStudy())
                .startYear(savedEducation.getStartYear())
                .endYear(savedEducation.getEndYear())
                .description(savedEducation.getDescription())
                .build();
        return ApiResponse.successResponse(response,"Education updated successfully");
    }

    public ResponseEntity<ResponseModel<EducationResponse>> findByDegree(DegreeEnum degree) throws GenericException {
        Education education = educationRespository.findByDegree(degree);
        if (education == null) {
            return ApiResponse.failureResponse(null,"Education with " + degree + "doesn't exist");
        }
        EducationResponse response = EducationResponse.builder()
                .id(education.getId())
                .degree(education.getDegree())
                .institution(education.getInstitution())
                .description(education.getDescription())
                .location(education.getLocation())
                .fieldOfStudy(education.getFieldOfStudy())
                .startYear(education.getStartYear())
                .endYear(education.getEndYear())
                .build();
        return ApiResponse.successResponse(response,"Education found successfully");
    }

    public ResponseEntity<ResponseModel<List<EducationResponse>>> findAllEducations() throws GenericException {
        List<Education> educations = educationRespository.findAll();
        if (educations == null) {
            return ApiResponse.failureResponse(null,"No Educations found");
        }
        List<EducationResponse> response = educations.stream()
                .map(education -> EducationResponse.builder()
                        .id(education.getId())
                        .degree(education.getDegree())
                        .institution(education.getInstitution())
                        .description(education.getDescription())
                        .location(education.getLocation())
                        .fieldOfStudy(education.getFieldOfStudy())
                        .startYear(education.getStartYear())
                        .endYear(education.getEndYear())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.successResponse(response,"Educations found successfully");
    }
}

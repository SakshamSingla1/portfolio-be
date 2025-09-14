package com.portfolio.controllers;

import com.portfolio.dtos.EducationRequest;
import com.portfolio.dtos.EducationResponse;
import com.portfolio.enums.DegreeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.EducationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Education records.
 * Supports create, update, delete, fetch by degree, and paginated search.
 */
@RestController
@RequestMapping("/api/v1/education")
public class EducationController {

    @Autowired
    private EducationService educationService;

    // 🔹 CREATE EDUCATION
    @PostMapping
    public ResponseEntity<ResponseModel<EducationResponse>> create(@RequestBody EducationRequest request) throws GenericException {
        return ApiResponse.respond(
                educationService.createEducation(request),
                "Education created successfully",
                "Failed to create education"
        );
    }

    // 🔹 UPDATE EDUCATION BY DEGREE
    @PutMapping("/{degree}")
    public ResponseEntity<ResponseModel<EducationResponse>> update(
            @PathVariable DegreeEnum degree,
            @RequestBody EducationRequest request
    ) throws GenericException {
        return ApiResponse.respond(
                educationService.updateEducation(degree, request),
                "Education updated successfully",
                "Failed to update education"
        );
    }

    // 🔹 GET EDUCATION BY DEGREE AND PROFILE
    @GetMapping("/{degree}/{profileId}")
    public ResponseEntity<ResponseModel<EducationResponse>> getByDegree(
            @PathVariable DegreeEnum degree,
            @PathVariable Integer profileId
    ) throws GenericException {
        return ApiResponse.respond(
                educationService.findByDegree(degree, profileId),
                "Education fetched successfully",
                "Failed to fetch education"
        );
    }

    // 🔹 GET EDUCATIONS BY PROFILE (PAGINATED & FILTERABLE)
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ResponseModel<Page<EducationResponse>>> getByProfile(
            @PathVariable Integer profileId,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        return ApiResponse.respond(
                educationService.getEducationByProfileId(profileId, pageable, search),
                "Educations fetched successfully",
                "Failed to fetch educations"
        );
    }

    // 🔹 DELETE EDUCATION
    @DeleteMapping("/{degree}/{profileId}")
    public ResponseEntity<ResponseModel<String>> delete(
            @PathVariable DegreeEnum degree,
            @PathVariable Integer profileId
    ) throws GenericException {
        return ApiResponse.respond(
                educationService.delete(degree, profileId),
                "Education deleted successfully",
                "Failed to delete education"
        );
    }
}

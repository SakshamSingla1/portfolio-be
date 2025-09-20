// File: EducationController.java
package com.portfolio.controllers;

import com.portfolio.dtos.EducationRequest;
import com.portfolio.dtos.EducationResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.EducationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/education")
@Tag(name = "Education", description = "Endpoints for managing education records")
public class EducationController {

    @Autowired
    private EducationService educationService;

    @Operation(summary = "Create education", description = "Creates a new education record.")
    @PostMapping
    public ResponseEntity<ResponseModel<EducationResponse>> create(@RequestBody EducationRequest request) throws GenericException {
        return ApiResponse.respond(
                educationService.createEducation(request),
                "Education created successfully",
                "Failed to create education"
        );
    }

    @Operation(summary = "Update education by ID", description = "Updates education details by ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<EducationResponse>> update(
            @PathVariable Integer id,
            @RequestBody EducationRequest request
    ) throws GenericException {
        return ApiResponse.respond(
                educationService.updateEducation(id, request),
                "Education updated successfully",
                "Failed to update education"
        );
    }

    @Operation(summary = "Get education by ID", description = "Fetches education record for a given ID and profile ID.")
    @GetMapping("/{id}/{profileId}")
    public ResponseEntity<ResponseModel<EducationResponse>> getById(
            @PathVariable Integer id,
            @PathVariable Integer profileId
    ) throws GenericException {
        return ApiResponse.respond(
                educationService.findById(id, profileId),
                "Education fetched successfully",
                "Failed to fetch education"
        );
    }

    @Operation(summary = "Get education by profile", description = "Fetches paginated education records for a profile with optional search.")
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

    @Operation(summary = "Get education by profile", description = "Fetches education records for a profile")
    @GetMapping("/user/{profileId}")
    public ResponseEntity<ResponseModel<List<EducationResponse>>> getByProfile(
            @PathVariable Integer profileId
    ) {
        return ApiResponse.respond(
                educationService.getEducationByProfileId(profileId),
                "Educations fetched successfully",
                "Failed to fetch educations"
        );
    }

    @Operation(summary = "Delete education", description = "Delete education record by ID and profile ID.")
    @DeleteMapping("/{id}/{profileId}")
    public ResponseEntity<ResponseModel<String>> delete(
            @PathVariable Integer id,
            @PathVariable Integer profileId
    ) throws GenericException {
        return ApiResponse.respond(
                educationService.delete(id, profileId),
                "Education deleted successfully",
                "Failed to delete education"
        );
    }
}
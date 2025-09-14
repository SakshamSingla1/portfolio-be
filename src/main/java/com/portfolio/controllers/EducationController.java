package com.portfolio.controllers;

import com.portfolio.dtos.EducationRequest;
import com.portfolio.dtos.EducationResponse;
import com.portfolio.enums.DegreeEnum;
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

    @Operation(summary = "Update education by degree", description = "Updates education details by degree type.")
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

    @Operation(summary = "Get education by degree", description = "Fetches education record for a given degree and profile ID.")
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

    @Operation(summary = "Delete education", description = "Deletes education record by degree and profile ID.")
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

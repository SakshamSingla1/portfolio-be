// File: EducationController.java
package com.portfolio.controllers;

import com.portfolio.dtos.Education.EducationRequest;
import com.portfolio.dtos.Education.EducationResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.EducationService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/education")
@Tag(name = "Education", description = "Endpoints for managing education records")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    private final Helper helper;

    @Operation(summary = "Create education", description = "Creates a new education record.")
    @PostMapping
    public ResponseEntity<ResponseModel<EducationResponse>> create(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @Valid @RequestBody EducationRequest request) throws GenericException {
        request.setProfileId(helper.getProfileIdFromHeader(auth));
        return ApiResponse.respond(
                educationService.createEducation(request),
                "Education created successfully",
                "Failed to create education"
        );
    }

    @Operation(summary = "Update education by ID", description = "Updates education details by ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<EducationResponse>> update(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long id,
            @Valid @RequestBody EducationRequest request
    ) throws GenericException {
        request.setProfileId(helper.getProfileIdFromHeader(auth));
        return ApiResponse.respond(
                educationService.updateEducation(id, request),
                "Education updated successfully",
                "Failed to update education"
        );
    }

    @Operation(summary = "Get education by ID", description = "Fetches education record for the logged-in user.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<EducationResponse>> getById(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long id
    ) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(
                educationService.findById(id, profileId),
                "Education fetched successfully",
                "Failed to fetch education"
        );
    }

    @Operation(summary = "Get education", description = "Fetches paginated education records for the logged-in profile.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<EducationResponse>>> getAll(
            @RequestHeader(value = "Authorization", required = false) String auth,
            Pageable pageable,
            @RequestParam(required = false) String search
    ) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(
                educationService.getByProfile(profileId, search, pageable),
                "Educations fetched successfully",
                "Failed to fetch educations"
        );
    }

    @Operation(summary = "Delete education", description = "Delete education record by ID for the logged-in user.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long id
    ) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(
                educationService.delete(id, profileId),
                "Education deleted successfully",
                "Failed to delete education"
        );
    }
}
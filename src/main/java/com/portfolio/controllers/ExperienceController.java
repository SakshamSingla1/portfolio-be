package com.portfolio.controllers;

import com.portfolio.dtos.ExperienceRequest;
import com.portfolio.dtos.ExperienceResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/experience")
@Tag(name = "Experience", description = "Endpoints for managing experience records")
public class ExperienceController {

    @Autowired
    private ExperienceService experienceService;

    @Operation(summary = "Create experience", description = "Creates a new work experience record.")
    @PostMapping
    public ResponseEntity<ResponseModel<ExperienceResponse>> create(@RequestBody ExperienceRequest req) throws GenericException {
        ExperienceResponse response = experienceService.create(req);
        return ApiResponse.respond(response, "Experience created successfully", "Failed to create experience");
    }

    @Operation(summary = "Update experience", description = "Updates an existing work experience record by ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ExperienceResponse>> update(
            @PathVariable Integer id,
            @RequestBody ExperienceRequest req) throws GenericException {
        ExperienceResponse response = experienceService.update(id, req);
        return ApiResponse.respond(response, "Experience updated successfully", "Failed to update experience");
    }

    @Operation(summary = "Get experience by ID", description = "Fetches a specific experience record by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ExperienceResponse>> getById(@PathVariable Integer id) throws GenericException {
        ExperienceResponse response = experienceService.getById(id);
        return ApiResponse.respond(response, "Experience fetched successfully", "Failed to fetch experience");
    }

    @Operation(summary = "Delete experience", description = "Deletes a work experience record by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@PathVariable Integer id) throws GenericException {
        String response = experienceService.delete(id);
        return ApiResponse.respond(response, "Experience deleted successfully", "Failed to delete experience");
    }

    @Operation(summary = "Get experiences by profile", description = "Fetches paginated list of experiences for a profile with optional search.")
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ResponseModel<Page<ExperienceResponse>>> getByProfile(
            @PathVariable Integer profileId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ExperienceResponse> response = experienceService.getExperienceByProfileId(profileId, pageable, search);
        return ApiResponse.respond(response, "Experiences fetched successfully", "Failed to fetch experiences");
    }
}

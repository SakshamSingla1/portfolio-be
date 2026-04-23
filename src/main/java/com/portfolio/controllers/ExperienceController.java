package com.portfolio.controllers;

import com.portfolio.dtos.Experience.ExperienceRequest;
import com.portfolio.dtos.Experience.ExperienceResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.servicesImpl.ExperienceServiceImpl;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/experience")
@Tag(name = "Experience", description = "Endpoints for managing experience records")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceServiceImpl experienceService;
    private final Helper helper;

    @Operation(summary = "Create experience", description = "Creates a new work experience record.")
    @PostMapping
    public ResponseEntity<ResponseModel<ExperienceResponse>> create(
            @RequestHeader("Authorization") String auth,
            @RequestBody ExperienceRequest req) throws GenericException {
        req.setProfileId(helper.getProfileIdFromHeader(auth));
        ExperienceResponse response = experienceService.create(req);
        return ApiResponse.respond(response, "Experience created successfully", "Failed to create experience");
    }

    @Operation(summary = "Update experience", description = "Updates an existing work experience record by ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ExperienceResponse>> update(
            @RequestHeader("Authorization") String auth,
            @PathVariable String id,
            @RequestBody ExperienceRequest req) throws GenericException {
        req.setProfileId(helper.getProfileIdFromHeader(auth));
        ExperienceResponse response = experienceService.update(id, req);
        return ApiResponse.respond(response, "Experience updated successfully", "Failed to update experience");
    }

    @Operation(summary = "Get experience by ID", description = "Fetches a specific experience record by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ExperienceResponse>> getById(@PathVariable String id) throws GenericException {
        ExperienceResponse response = experienceService.getById(id);
        return ApiResponse.respond(response, "Experience fetched successfully", "Failed to fetch experience");
    }

    @Operation(summary = "Delete experience", description = "Deletes a work experience record by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@PathVariable String id) throws GenericException {
        String response = experienceService.delete(id);
        return ApiResponse.respond(response, "Experience deleted successfully", "Failed to delete experience");
    }

    @Operation(summary = "Get My experiences", description = "Fetches paginated list of experiences for the logged-in profile.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<ExperienceResponse>>> getMyExperiences(
            @RequestHeader("Authorization") String auth,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) throws GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        Page<ExperienceResponse> response = experienceService.getByProfile(profileId, search, sortDir, sortBy, pageable);
        return ApiResponse.respond(response, "Experiences fetched successfully", "Failed to fetch experiences");
    }
}

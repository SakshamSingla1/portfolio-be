package com.portfolio.controllers;

import com.portfolio.dtos.Experience.ExperienceRequest;
import com.portfolio.dtos.Experience.ExperienceResponse;
import com.portfolio.entities.Experience;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.ExperienceRepository;
import com.portfolio.services.ExperienceService;
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
@RequestMapping("/api/v1/experience")
@Tag(name = "Experience", description = "Endpoints for managing experience records")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;
    private final ExperienceRepository experienceRepository;
    private final Helper helper;

    @Operation(summary = "Create experience", description = "Creates a new work experience record.")
    @PostMapping
    public ResponseEntity<ResponseModel<ExperienceResponse>> create(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @Valid @RequestBody ExperienceRequest req) throws GenericException {
        req.setProfileId(helper.getProfileIdFromHeader(auth));
        ExperienceResponse response = experienceService.create(req);
        return ApiResponse.respond(response, "Experience created successfully", "Failed to create experience");
    }

    @Operation(summary = "Update experience", description = "Updates an existing work experience record by ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ExperienceResponse>> update(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long id,
            @Valid @RequestBody ExperienceRequest req) throws GenericException {
        req.setProfileId(helper.getProfileIdFromHeader(auth));
        ExperienceResponse response = experienceService.update(id, req);
        return ApiResponse.respond(response, "Experience updated successfully", "Failed to update experience");
    }

    @Operation(summary = "Get experience by ID", description = "Fetches a specific experience record by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ExperienceResponse>> getById(@PathVariable Long id) throws GenericException {
        ExperienceResponse response = experienceService.getById(id);
        return ApiResponse.respond(response, "Experience fetched successfully", "Failed to fetch experience");
    }

    @Operation(summary = "Delete experience", description = "Deletes a work experience record by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long id) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EXPERIENCE_NOT_FOUND, "Experience not found"));
        if (!experience.getProfileId().equals(profileId)) {
            throw new GenericException(ExceptionCodeEnum.FORBIDDEN, "You do not have permission to delete this experience");
        }
        String response = experienceService.delete(id);
        return ApiResponse.respond(response, "Experience deleted successfully", "Failed to delete experience");
    }

    @Operation(summary = "Get experiences", description = "Fetches paginated list of experiences for the logged-in profile.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<ExperienceResponse>>> getAll(
            @RequestHeader(value = "Authorization", required = false) String auth,
            Pageable pageable,
            @RequestParam(required = false) String search) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        Page<ExperienceResponse> response = experienceService.getByProfile(profileId, search, pageable);
        return ApiResponse.respond(response, "Experiences fetched successfully", "Failed to fetch experiences");
    }
}

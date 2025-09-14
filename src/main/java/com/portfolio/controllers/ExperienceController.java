package com.portfolio.controllers;

import com.portfolio.dtos.ExperienceRequest;
import com.portfolio.dtos.ExperienceResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing Experience entities.
 * Provides endpoints to create, update, fetch, delete, and list experiences.
 */
@RestController
@RequestMapping("/api/v1/experience")
public class ExperienceController {

    @Autowired
    private ExperienceService experienceService;

    // ---------------- CREATE EXPERIENCE ----------------
    @PostMapping
    public ResponseEntity<ResponseModel<ExperienceResponse>> create(@RequestBody ExperienceRequest req) {
        try {
            ExperienceResponse response = experienceService.create(req);
            return ApiResponse.respond(response, "Experience created successfully", "Failed to create experience");
        } catch (GenericException ex) {
            return ApiResponse.respond(null, "Failed to create experience", ex.getMessage());
        }
    }

    // ---------------- UPDATE EXPERIENCE ----------------
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ExperienceResponse>> update(
            @PathVariable Integer id,
            @RequestBody ExperienceRequest req) {
        try {
            ExperienceResponse response = experienceService.update(id, req);
            return ApiResponse.respond(response, "Experience updated successfully", "Failed to update experience");
        } catch (GenericException ex) {
            return ApiResponse.respond(null, "Failed to update experience", ex.getMessage());
        }
    }

    // ---------------- GET EXPERIENCE BY ID ----------------
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ExperienceResponse>> getById(@PathVariable Integer id) {
        try {
            ExperienceResponse response = experienceService.getById(id);
            return ApiResponse.respond(response, "Experience fetched successfully", "Failed to fetch experience");
        } catch (GenericException ex) {
            return ApiResponse.respond(null, "Failed to fetch experience", ex.getMessage());
        }
    }

    // ---------------- DELETE EXPERIENCE ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@PathVariable Integer id) {
        try {
            String response = experienceService.delete(id);
            return ApiResponse.respond(response, "Experience deleted successfully", "Failed to delete experience");
        } catch (GenericException ex) {
            return ApiResponse.respond(null, "Failed to delete experience", ex.getMessage());
        }
    }

    // ---------------- LIST EXPERIENCES BY PROFILE (SEARCH + PAGINATION) ----------------
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

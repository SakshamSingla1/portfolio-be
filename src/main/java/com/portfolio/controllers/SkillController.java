package com.portfolio.controllers;

import com.cloudinary.Api;
import com.portfolio.dtos.Skill.SkillRequest;
import com.portfolio.dtos.Skill.SkillResponse;
import com.portfolio.dtos.Skill.SkillStat;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.SkillService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/skill")
@Tag(name = "Skills", description = "APIs for managing Skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final Helper helper;

    @Operation(summary = "Create Skill", description = "Add a new skill to a profile")
    @PostMapping
    public ResponseEntity<ResponseModel<SkillResponse>> create(
            @RequestHeader("Authorization") String auth,
            @RequestBody SkillRequest req) {
        try {
            req.setProfileId(helper.getProfileIdFromHeader(auth));
            SkillResponse response = skillService.create(req);
            return ApiResponse.successResponse(response, "Skill created successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Update Skill", description = "Update an existing skill by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<SkillResponse>> update(
            @RequestHeader("Authorization") String auth,
            @Parameter(description = "Skill ID") @PathVariable String id, 
            @RequestBody SkillRequest req) {
        try {
            req.setProfileId(helper.getProfileIdFromHeader(auth));
            SkillResponse response = skillService.update(id, req);
            return ApiResponse.successResponse(response, "Skill updated successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Get Skill by ID", description = "Retrieve details of a skill by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<SkillResponse>> findById(@Parameter(description = "Skill ID") @PathVariable String id) {
        try {
            SkillResponse response = skillService.getById(id);
            return ApiResponse.successResponse(response, "Skill fetched successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Get Skills", description = "Fetch all skills of the logged-in profile")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<SkillResponse>>> getAll(
            @RequestHeader("Authorization") String auth,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir
    ) throws GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        Page<SkillResponse> response = skillService.getByProfile(profileId, pageable, search, sortDir, sortBy);
        return ApiResponse.successResponse(response, "Skills fetched successfully");
    }

    @Operation(summary = "Delete Skill", description = "Delete a skill by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@Parameter(description = "Skill ID") @PathVariable String id) {
        try {
            skillService.delete(id);
            return ApiResponse.successResponse("Skill deleted successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ResponseModel<SkillStat>> getStats() {
        SkillStat statsResponse = skillService.getStats();
        return ApiResponse.successResponse(statsResponse, "Stats Fetched Successfully");
    }
}

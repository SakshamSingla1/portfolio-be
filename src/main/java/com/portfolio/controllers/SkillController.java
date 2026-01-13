package com.portfolio.controllers;

import com.portfolio.dtos.SkillRequest;
import com.portfolio.dtos.SkillResponse;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/skill")
@Tag(name = "Skills", description = "APIs for managing Skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @Operation(summary = "Create Skill", description = "Add a new skill to a profile")
    @PostMapping
    public ResponseEntity<ResponseModel<SkillResponse>> create(@RequestBody SkillRequest req) {
        try {
            SkillResponse response = skillService.create(req);
            return ApiResponse.successResponse(response, "Skill created successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Update Skill", description = "Update an existing skill by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<SkillResponse>> update(@Parameter(description = "Skill ID") @PathVariable String id, @RequestBody SkillRequest req) {
        try {
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

    @Operation(summary = "Get Skills by Profile", description = "Fetch all skills of a profile with pagination and optional search")
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ResponseModel<Page<SkillResponse>>> findByProfileId(
            @Parameter(description = "Profile ID") @PathVariable String profileId,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir
    ) {
        Page<SkillResponse> response = skillService.getByProfile(profileId, pageable, search,sortDir,sortBy);
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
}

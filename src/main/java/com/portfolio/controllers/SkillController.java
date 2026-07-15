package com.portfolio.controllers;

import com.portfolio.dtos.Skill.SkillRequest;
import com.portfolio.dtos.Skill.SkillResponse;
import com.portfolio.dtos.Skill.SkillStat;
import com.portfolio.entities.Skill;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.SkillRepository;
import com.portfolio.services.SkillService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/skill")
@Tag(name = "Skills", description = "APIs for managing Skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final SkillRepository skillRepository;
    private final Helper helper;

    @Operation(summary = "Create Skill", description = "Add a new skill to a profile")
    @PostMapping
    public ResponseEntity<ResponseModel<SkillResponse>> create(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @Valid @RequestBody SkillRequest req) throws GenericException {
        req.setProfileId(helper.getProfileIdFromHeader(auth));
        SkillResponse response = skillService.create(req);
        return ApiResponse.successResponse(response, "Skill created successfully");
    }

    @Operation(summary = "Update Skill", description = "Update an existing skill by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<SkillResponse>> update(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @Parameter(description = "Skill ID") @PathVariable Long id,
            @Valid @RequestBody SkillRequest req) throws GenericException {
        req.setProfileId(helper.getProfileIdFromHeader(auth));
        SkillResponse response = skillService.update(id, req);
        return ApiResponse.successResponse(response, "Skill updated successfully");
    }

    @Operation(summary = "Get Skill by ID", description = "Retrieve details of a skill by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<SkillResponse>> findById(@Parameter(description = "Skill ID") @PathVariable Long id) throws GenericException {
        SkillResponse response = skillService.getById(id);
        return ApiResponse.successResponse(response, "Skill fetched successfully");
    }

    @Operation(summary = "Get Skills", description = "Fetch all skills of the logged-in profile")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<SkillResponse>>> getAll(
            @RequestHeader(value = "Authorization", required = false) String auth,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir
    ) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        Page<SkillResponse> response = skillService.getByProfile(profileId, pageable, search, sortDir, sortBy);
        return ApiResponse.successResponse(response, "Skills fetched successfully");
    }

    @Operation(summary = "Delete Skill", description = "Delete a skill by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @Parameter(description = "Skill ID") @PathVariable Long id) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SKILL_NOT_FOUND, "Skill not found"));
        if (!skill.getProfileId().equals(profileId)) {
            throw new GenericException(ExceptionCodeEnum.FORBIDDEN, "You do not have permission to delete this skill");
        }
        skillService.delete(id);
        return ApiResponse.successResponse("Skill deleted successfully");
    }

    @Operation(summary = "Get skill statistics", description = "Returns aggregated skill statistics such as counts by category and level for the authenticated profile.")
    @GetMapping("/stats")
    public ResponseEntity<ResponseModel<SkillStat>> getStats(
            @RequestHeader(value = "Authorization", required = false) String auth) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        SkillStat statsResponse = skillService.getStats(profileId);
        return ApiResponse.successResponse(statsResponse, "Stats Fetched Successfully");
    }
}

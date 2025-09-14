package com.portfolio.controllers;

import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.dtos.SkillRequest;
import com.portfolio.dtos.SkillResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.SkillService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/skill")
public class SkillController {

    private final SkillService skillService;

    // Use constructor injection instead of field injection
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    // Create a new skill
    @PostMapping
    public ResponseEntity<ResponseModel<SkillResponse>> create(@RequestBody SkillRequest req) {
        try {
            SkillResponse response = skillService.create(req);
            return ApiResponse.successResponse(response, "Skill created successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null,e.getMessage() + "Failed to create skill");
        }
    }

    // Update existing skill
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<SkillResponse>> update(@PathVariable Integer id, @RequestBody SkillRequest req) {
        try {
            SkillResponse response = skillService.update(id, req);
            return ApiResponse.successResponse(response, "Skill updated successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null,e.getMessage() + "Failed to update skill");
        }
    }

    // Get skill by id
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<SkillResponse>> findById(@PathVariable Integer id) {
        try {
            SkillResponse response = skillService.getById(id);
            return ApiResponse.successResponse(response, "Skill fetched successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage() + "Failed to fetch skill");
        }
    }

    // Get all skills for a profile with pagination and search
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ResponseModel<Page<SkillDropdown>>> findSkillByProfileId(
            @PathVariable Integer profileId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<SkillDropdown> response = skillService.getSkillByProfileId(profileId, pageable, search);
        return ApiResponse.successResponse(response, "Skills fetched successfully");
    }

    // Delete skill
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@PathVariable Integer id) {
        try {
            skillService.delete(id);
            return ApiResponse.successResponse("Skill deleted successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(e.getMessage(), "Failed to delete skill");
        }
    }
}

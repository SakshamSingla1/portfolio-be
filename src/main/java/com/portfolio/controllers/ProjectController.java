package com.portfolio.controllers;

import com.portfolio.dtos.ProjectRequest;
import com.portfolio.dtos.ProjectResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // ---------------- CREATE PROJECT ----------------
    @PostMapping
    public ResponseEntity<ResponseModel<ProjectResponse>> createProject(@RequestBody ProjectRequest request) {
        try {
            ProjectResponse response = projectService.createProject(request);
            return ApiResponse.respond(response, "Project created successfully", "Failed to create project");
        } catch (GenericException e) {
            return ApiResponse.respond(null, "Failed to create project", e.getMessage());
        }
    }

    // ---------------- GET PROJECT BY ID ----------------
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ProjectResponse>> getProjectById(@PathVariable Integer id) {
        try {
            ProjectResponse response = projectService.getProjectById(id);
            return ApiResponse.respond(response, "Project fetched successfully", "Failed to fetch project");
        } catch (GenericException e) {
            return ApiResponse.respond(null, "Failed to fetch project", e.getMessage());
        }
    }

    // ---------------- UPDATE PROJECT ----------------
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ProjectResponse>> updateProject(@PathVariable Integer id,
                                                                        @RequestBody ProjectRequest request) {
        try {
            ProjectResponse response = projectService.updateProjectById(id, request);
            return ApiResponse.respond(response, "Project updated successfully", "Failed to update project");
        } catch (GenericException e) {
            return ApiResponse.respond(null, "Failed to update project", e.getMessage());
        }
    }

    // ---------------- DELETE PROJECT ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteProject(@PathVariable Integer id) {
        try {
            String message = projectService.deleteProjectById(id);
            return ApiResponse.successResponse(message, "Project deleted successfully");
        } catch (GenericException e) {
            return ApiResponse.respond(null, "Failed to delete project", e.getMessage());
        }
    }

    // ---------------- GET PROJECTS BY PROFILE (PAGINATED + SEARCH) ----------------
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ResponseModel<Page<ProjectResponse>>> getProjectsByProfile(
            @PathVariable Integer profileId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProjectResponse> projects = projectService.getProjectByProfileId(profileId, pageable, search);

        return ApiResponse.respond(projects, "Projects fetched successfully", "Failed to fetch projects");
    }
}

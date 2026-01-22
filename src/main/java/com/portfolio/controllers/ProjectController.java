package com.portfolio.controllers;

import com.portfolio.dtos.ImageUploadResponse;
import com.portfolio.dtos.ProjectRequest;
import com.portfolio.dtos.ProjectResponse;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/project")
@Tag(name = "Projects", description = "APIs for managing Projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Create a Project", description = "Create a new project for a profile")
    @PostMapping
    public ResponseEntity<ResponseModel<ProjectResponse>> createProject(@RequestBody ProjectRequest request) {
        try {
            ProjectResponse response = projectService.create(request);
            return ApiResponse.successResponse(response, "Project created successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Get Project by ID", description = "Retrieve project details by project ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ProjectResponse>> getProjectById(@PathVariable String id) {
        try {
            ProjectResponse response = projectService.getById(id);
            return ApiResponse.successResponse(response, "Project fetched successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Update Project", description = "Update project details by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ProjectResponse>> updateProject(@PathVariable String id, @RequestBody ProjectRequest request) {
        try {
            ProjectResponse response = projectService.update(id, request);
            return ApiResponse.successResponse(response, "Project updated successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Delete Project", description = "Delete project by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteProject(@PathVariable String id) {
        try {
            String message = projectService.delete(id);
            return ApiResponse.successResponse(message, "Project deleted successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Get Projects by Profile", description = "Fetch all projects of a profile with pagination and optional search")
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ResponseModel<Page<ProjectResponse>>> getProjectsByProfile(
            @PathVariable String profileId,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        Page<ProjectResponse> projects = projectService.getByProfile(profileId, pageable, search,sortDir,sortBy);
        return ApiResponse.successResponse(projects, "Projects fetched successfully");
    }

    @Operation(summary = "Upload a project image")
    @PostMapping("/{profileId}/images")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadProjectImage(
            @PathVariable String profileId,
            @RequestParam("file") MultipartFile file
    ) throws IOException, GenericException {
        return ApiResponse.respond(
                projectService.uploadProjectImage(profileId, file),
                "Project image uploaded successfully",
                "Failed to upload project image"
        );
    }
}

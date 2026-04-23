package com.portfolio.controllers;

import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.dtos.Project.ProjectRequest;
import com.portfolio.dtos.Project.ProjectResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProjectService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "APIs for managing Projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final Helper helper;

    @Operation(summary = "Create a Project", description = "Create a new project for a profile")
    @PostMapping
    public ResponseEntity<ResponseModel<ProjectResponse>> createProject(
            @RequestHeader("Authorization") String auth,
            @RequestBody ProjectRequest request) {
        try {
            request.setProfileId(helper.getProfileIdFromHeader(auth));
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
    public ResponseEntity<ResponseModel<ProjectResponse>> updateProject(
            @RequestHeader("Authorization") String auth,
            @PathVariable String id, 
            @RequestBody ProjectRequest request) {
        try {
            request.setProfileId(helper.getProfileIdFromHeader(auth));
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

    @Operation(summary = "Get My Projects", description = "Fetch all projects of the logged-in profile")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<ProjectResponse>>> getMyProjects(
            @RequestHeader("Authorization") String auth,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) throws GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        Page<ProjectResponse> projects = projectService.getByProfile(profileId, pageable, search, sortDir, sortBy);
        return ApiResponse.successResponse(projects, "Projects fetched successfully");
    }

    @Operation(summary = "Upload a project image")
    @PostMapping("/images/upload")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadProjectImage(
            @RequestHeader("Authorization") String auth,
            @RequestParam("file") MultipartFile file
    ) throws IOException, GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(
                projectService.uploadProjectImage(profileId, file),
                "Project image uploaded successfully",
                "Failed to upload project image"
        );
    }
}

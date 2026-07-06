package com.portfolio.controllers;

import com.portfolio.dtos.Resume.ResumeUploadResponseDTO;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ResumeService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/resume")
@PreAuthorize("isAuthenticated()")
public class ResumeController {

    private final ResumeService resumeService;
    private final Helper helper;

    @Operation(summary = "Upload resume", description = "Uploads a PDF resume file for the authenticated user's profile and stores it in cloud storage.")
    @PostMapping("/upload")
    public ResponseEntity<ResponseModel<ResumeUploadResponseDTO>> upload(
            @RequestHeader("Authorization") String auth,
            @RequestParam MultipartFile file) throws IOException, GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        ResumeUploadResponseDTO resumeUploadResponseDTO = resumeService.uploadResume(profileId, file);
        return ApiResponse.respond(resumeUploadResponseDTO, "Resume uploaded successfully", "Failed to upload resume");
    }

    @Operation(summary = "Get resumes", description = "Returns a paginated list of resume records for the authenticated user with optional status filter and search.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<ResumeUploadResponseDTO>>> getAll(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) StatusEnum status,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        Page<ResumeUploadResponseDTO> resumeDtos = resumeService.getByProfile(profileId, status, pageable, search, sortDir, sortBy);
        return ApiResponse.respond(resumeDtos, "Resumes fetched successfully", "failed to fetch resumes");
    }

    @Operation(summary = "Activate resume", description = "Sets a specific resume as the active one for the authenticated user's profile. Deactivates all others.")
    @PutMapping("/activate")
    public ResponseEntity<ResponseModel<String>> activate(
            @RequestHeader("Authorization") String auth,
            @RequestParam Long resumeId) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        resumeService.activateResume(profileId, resumeId);
        return ApiResponse.successResponse(null, "Resume activated successfully");
    }

    @Operation(summary = "Delete resume", description = "Permanently deletes a resume record and its associated file by resume ID.")
    @DeleteMapping("/{resumeId}")
    public ResponseEntity<ResponseModel<String>> deleteResume(
            @PathVariable Long resumeId
    ) throws GenericException {
        resumeService.deleteResume(resumeId);
        return ApiResponse.successResponse(null, "Resume deleted successfully");
    }

}

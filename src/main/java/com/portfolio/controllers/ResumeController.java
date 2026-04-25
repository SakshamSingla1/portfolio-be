package com.portfolio.controllers;

import com.portfolio.dtos.Resume.ResumeUploadResponseDTO;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ResumeService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/resume")
public class ResumeController {

    private final ResumeService resumeService;
    private final Helper helper;

    @PostMapping("/upload")
    public ResponseEntity<ResponseModel<ResumeUploadResponseDTO>> upload(
            @RequestHeader("Authorization") String auth,
            @RequestParam MultipartFile file) throws IOException, GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        ResumeUploadResponseDTO resumeUploadResponseDTO = resumeService.uploadResume(profileId, file);
        return ApiResponse.respond(resumeUploadResponseDTO, "Resume uploaded successfully", "Failed to upload resume");
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<ResumeUploadResponseDTO>>> getAll(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) StatusEnum status,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) throws GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        Page<ResumeUploadResponseDTO> resumeDtos = resumeService.getByProfile(profileId, status, pageable, search, sortDir, sortBy);
        return ApiResponse.respond(resumeDtos, "Resumes fetched successfully", "failed to fetch resumes");
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseModel<String>> activate(
            @RequestHeader("Authorization") String auth,
            @RequestParam String resumeId) throws GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        resumeService.activateResume(profileId, resumeId);
        return ApiResponse.successResponse(null, "Resume activated successfully");
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<ResponseModel<String>> deleteResume(
            @PathVariable String resumeId
    ) throws GenericException {
        resumeService.deleteResume(resumeId);
        return ApiResponse.successResponse(null, "Resume deleted successfully");
    }

}

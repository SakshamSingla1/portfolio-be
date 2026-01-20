package com.portfolio.controllers;

import com.portfolio.dtos.Resume.ResumeUploadResponseDTO;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ResumeService;
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

    @PostMapping("/upload/{profileId}")
    public ResponseEntity<ResponseModel<ResumeUploadResponseDTO>> uploadResume(
            @PathVariable String profileId,
            @RequestParam MultipartFile file) throws IOException {
        ResumeUploadResponseDTO resumeUploadResponseDTO = resumeService.uploadResume(profileId, file);
        return ApiResponse.respond(resumeUploadResponseDTO, "Resume uploaded successfully","Failed to upload resume");
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ResponseModel<Page<ResumeUploadResponseDTO>>>  getResumes(
            @PathVariable String profileId,
            @RequestParam(required = false) StatusEnum status,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        Page<ResumeUploadResponseDTO> resumeDtos = resumeService.getByProfile(profileId,status,pageable, search,sortDir,sortBy);
        return ApiResponse.respond(resumeDtos, "Resumes fetched successfully","failed to fetch resumes");
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseModel<String>> activateResume(
            @RequestParam String profileId,
            @RequestParam String resumeId) throws GenericException {
        resumeService.activateResume(profileId,resumeId);
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

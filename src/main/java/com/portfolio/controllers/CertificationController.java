package com.portfolio.controllers;

import com.portfolio.dtos.Certifications.CertificationRequestDTO;
import com.portfolio.dtos.Certifications.CertificationResponseDTO;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.CertificationService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;
    private final Helper helper;

    @PostMapping
    public ResponseEntity<ResponseModel<CertificationResponseDTO>> createCertification(
            @RequestHeader("Authorization") String auth,
            @RequestBody CertificationRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        CertificationResponseDTO response = certificationService.createCertification(dto);
        return ApiResponse.respond(response, "Certification created successfully", "Failed to create certification");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<CertificationResponseDTO>> updateCertification(
            @RequestHeader("Authorization") String auth,
            @PathVariable String id, 
            @RequestBody CertificationRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        CertificationResponseDTO response = certificationService.updateCertification(id, dto);
        return ApiResponse.respond(response, "Certification updated successfully", "Failed to update certification");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<CertificationResponseDTO>> getCertificationById(@PathVariable String id) throws GenericException {
        CertificationResponseDTO response = certificationService.getCertificationById(id);
        return ApiResponse.respond(response, "Certification fetched successfully", "Failed to fetch certification");
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<CertificationResponseDTO>>> getAll(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false, defaultValue = "order") String sortBy,
            Pageable pageable
    ) throws GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        Page<CertificationResponseDTO> page = certificationService.getByProfile(profileId, search, sortDir, sortBy, pageable);
        return ApiResponse.respond(page, "Certifications fetched successfully", "Failed to fetch certifications");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteCertification(@PathVariable String id) throws GenericException {
        certificationService.deleteById(id);
        return ApiResponse.respond(null, "Certification deleted successfully", "Failed to delete certification");
    }

    @Operation(summary = "Upload Credential Image")
    @PostMapping("/upload")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadCredentialImage(
            @RequestHeader("Authorization") String auth,
            @RequestParam("file") MultipartFile file
    ) throws IOException, GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(certificationService.uploadCredentialImage(profileId, file), "Profile image uploaded successfully", "Failed to upload profile image");
    }
}

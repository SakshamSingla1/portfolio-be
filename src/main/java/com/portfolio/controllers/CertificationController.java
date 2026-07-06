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

import jakarta.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("api/v1/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;
    private final Helper helper;

    @Operation(summary = "Create certification", description = "Creates a new certification record for the authenticated user's profile.")
    @PostMapping
    public ResponseEntity<ResponseModel<CertificationResponseDTO>> createCertification(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody CertificationRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        CertificationResponseDTO response = certificationService.createCertification(dto);
        return ApiResponse.respond(response, "Certification created successfully", "Failed to create certification");
    }

    @Operation(summary = "Update certification", description = "Updates an existing certification record identified by its ID for the authenticated user's profile.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<CertificationResponseDTO>> updateCertification(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id, 
            @Valid @RequestBody CertificationRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        CertificationResponseDTO response = certificationService.updateCertification(id, dto);
        return ApiResponse.respond(response, "Certification updated successfully", "Failed to update certification");
    }

    @Operation(summary = "Get certification by ID", description = "Retrieves a single certification record by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<CertificationResponseDTO>> getCertificationById(@PathVariable Long id) throws GenericException {
        CertificationResponseDTO response = certificationService.getCertificationById(id);
        return ApiResponse.respond(response, "Certification fetched successfully", "Failed to fetch certification");
    }

    @Operation(summary = "Get all certifications", description = "Returns a paginated list of certification records for the authenticated user's profile, with optional keyword search.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<CertificationResponseDTO>>> getAll(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        System.out.println(profileId);
        Page<CertificationResponseDTO> page = certificationService.getByProfile(profileId, search, pageable);
        return ApiResponse.respond(page, "Certifications fetched successfully", "Failed to fetch certifications");
    }

    @Operation(summary = "Delete certification", description = "Permanently deletes the certification record identified by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteCertification(@PathVariable Long id) throws GenericException {
        certificationService.deleteById(id);
        return ApiResponse.respond(null, "Certification deleted successfully", "Failed to delete certification");
    }

    @Operation(summary = "Upload certification credential image", description = "Uploads a credential image file for a certification and returns the stored image URL for the authenticated user's profile.")
    @PostMapping("/upload")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadCredentialImage(
            @RequestHeader("Authorization") String auth,
            @RequestParam("file") MultipartFile file
    ) throws IOException, GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(certificationService.uploadCredentialImage(profileId, file), "Profile image uploaded successfully", "Failed to upload profile image");
    }
}

package com.portfolio.controllers;

import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.dtos.Services.ServiceRequest;
import com.portfolio.dtos.Services.ServiceResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ServiceOfferingService;
import com.portfolio.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("api/v1/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceOfferingService serviceOfferingService;
    private final Helper helper;

    @PostMapping
    public ResponseEntity<ResponseModel<ServiceResponse>> create(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody ServiceRequest req) throws GenericException {
        req.setBannerPublicId(req.getBannerPublicId());
        ServiceResponse response = serviceOfferingService.create(setProfileId(req, auth));
        return ApiResponse.respond(response, "Service created successfully", "Failed to create service");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ServiceResponse>> update(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequest req) throws GenericException {
        ServiceResponse response = serviceOfferingService.update(id, setProfileId(req, auth));
        return ApiResponse.respond(response, "Service updated successfully", "Failed to update service");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ServiceResponse>> getById(@PathVariable Long id) throws GenericException {
        return ApiResponse.respond(serviceOfferingService.getById(id), "Service fetched successfully", "Failed to fetch service");
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<ServiceResponse>>> getAll(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) String search,
            Pageable pageable) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(serviceOfferingService.getAll(profileId, search, pageable), "Services fetched successfully", "Failed to fetch services");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> delete(@PathVariable Long id) throws GenericException {
        serviceOfferingService.delete(id);
        return ApiResponse.successResponse(null, "Service deleted successfully");
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadBanner(
            @RequestHeader("Authorization") String auth,
            @RequestParam("file") MultipartFile file) throws GenericException, IOException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(serviceOfferingService.uploadBanner(profileId, file), "Banner uploaded successfully", "Failed to upload banner");
    }

    private ServiceRequest setProfileId(ServiceRequest req, String auth) throws GenericException {
        return req;
    }
}

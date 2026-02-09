package com.portfolio.controllers;

import com.portfolio.dtos.ImageUploadResponse;
import com.portfolio.dtos.ProfileRequest;
import com.portfolio.dtos.ProfileResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile", description = "Endpoints for managing user profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;


    @Operation(summary = "Get profile by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ProfileResponse>> get(
            @PathVariable String id
    ) throws GenericException {
        return ApiResponse.respond(profileService.get(id), "Profile fetched successfully", "Failed to fetch profile");
    }

    @Operation(summary = "Update profile")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ProfileResponse>> update(
            @PathVariable String id,
            @RequestBody ProfileRequest req
    ) throws GenericException, IOException {
        return ApiResponse.respond(profileService.update(id, req), "Profile updated successfully", "Failed to update profile");
    }

    @Operation(summary = "Upload profile image")
    @PutMapping("/{id}/upload/profile-image")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadProfileImage(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file
    ) throws IOException, GenericException {
        return ApiResponse.respond(profileService.uploadProfileImage(id, file), "Profile image uploaded successfully", "Failed to upload profile image");
    }

    @Operation(summary = "Upload About Me image")
    @PutMapping("/{id}/upload/about-me-image")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadAboutMeImage(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file
    ) throws IOException, GenericException {
        return ApiResponse.respond(profileService.uploadAboutMeImage(id, file), "About Me image uploaded successfully", "Failed to upload about me image");
    }

    @Operation(summary = "Upload logo")
    @PutMapping("/{id}/upload/logo")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadLogo(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file
    ) throws IOException, GenericException {
        return ApiResponse.respond(profileService.uploadLogoImage(id, file), "Logo uploaded successfully", "Failed to upload logo");
    }
}

package com.portfolio.controllers;

import com.portfolio.dtos.Admin.RoleUpdateRequest;
import com.portfolio.dtos.Admin.StatusUpdateRequest;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.dtos.Profile.ProfileRequest;
import com.portfolio.dtos.Profile.ProfileResponse;
import com.portfolio.dtos.User.UserResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.enums.StatusEnum;
import com.portfolio.services.ProfileService;
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

    @Operation(summary = "Get all profiles with pagination")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<UserResponse>>> getAllProfiles(
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) StatusEnum status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir
    ) {
        Page<UserResponse> profiles = profileService.getAllProfiles(
                pageable, search, status, role, sortBy, sortDir
        );
        return ApiResponse.respond(
                profiles,
                "Profiles fetched successfully",
                "Failed to fetch profiles"
        );
    }

    // Admin User Management APIs
    @Operation(summary = "Get all users (Admin)")
    @GetMapping("/users")
    public ResponseEntity<ResponseModel<Page<UserResponse>>> getAllUsers(
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) StatusEnum status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir
    ) {
        Page<UserResponse> users = profileService.getAllProfiles(
                pageable, search, status, role, sortBy, sortDir
        );
        return ApiResponse.respond(
                users,
                "Users fetched successfully",
                "Failed to fetch users"
        );
    }

    @Operation(summary = "Get user by ID (Admin)")
    @GetMapping("/users/{id}")
    public ResponseEntity<ResponseModel<UserResponse>> getUserById(@PathVariable String id) throws GenericException {
        UserResponse user = profileService.getUserById(id);
        return ApiResponse.respond(
                user,
                "User fetched successfully",
                "Failed to fetch user"
        );
    }

    @Operation(summary = "Update user status (Admin)")
    @PutMapping("/users/{id}/status")
    public ResponseEntity<ResponseModel<UserResponse>> updateUserStatus(
            @PathVariable String id,
            @RequestBody StatusUpdateRequest request
    ) throws GenericException {
        UserResponse user = profileService.updateUserStatus(id, request);
        return ApiResponse.respond(
                user,
                "User status updated successfully",
                "Failed to update user status"
        );
    }

    @Operation(summary = "Update user role (Admin)")
    @PutMapping("/users/{id}/role")
    public ResponseEntity<ResponseModel<UserResponse>> updateUserRole(
            @PathVariable String id,
            @RequestBody RoleUpdateRequest request
    ) throws GenericException {
        UserResponse user = profileService.updateUserRole(id, request);
        return ApiResponse.respond(
                user,
                "User role updated successfully",
                "Failed to update user role"
        );
    }

    @Operation(summary = "Toggle user verification (Admin)")
    @PutMapping("/users/{id}/verify")
    public ResponseEntity<ResponseModel<UserResponse>> toggleUserVerification(@PathVariable String id) throws GenericException {
        UserResponse user = profileService.toggleUserVerification(id);
        return ApiResponse.respond(
                user,
                "User verification toggled successfully",
                "Failed to toggle user verification"
        );
    }
}

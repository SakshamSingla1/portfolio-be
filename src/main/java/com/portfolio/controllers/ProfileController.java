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
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
        private final Helper helper;

        @Operation(summary = "Get profile", description = "Returns the full profile of the currently authenticated user.")
        @GetMapping
        public ResponseEntity<ResponseModel<ProfileResponse>> getProfile(
                        @RequestHeader("Authorization") String auth) throws GenericException {
                Long profileId = helper.getProfileIdFromHeader(auth);
                return ApiResponse.respond(profileService.get(profileId), "Profile fetched successfully",
                                "Failed to fetch profile");
        }

        @Operation(summary = "Get profile by ID", description = "Returns a user profile by its ID.")
        @GetMapping("/{id}")
        public ResponseEntity<ResponseModel<ProfileResponse>> get(
                        @PathVariable Long id) throws GenericException {
                return ApiResponse.respond(profileService.get(id), "Profile fetched successfully",
                                "Failed to fetch profile");
        }

        @Operation(summary = "Update profile", description = "Updates the profile of the currently authenticated user including name, title, location, and bio.")
        @PutMapping
        public ResponseEntity<ResponseModel<ProfileResponse>> updateProfile(
                        @RequestHeader("Authorization") String auth,
                        @RequestBody ProfileRequest req) throws GenericException, IOException {
                Long profileId = helper.getProfileIdFromHeader(auth);
                return ApiResponse.respond(profileService.update(profileId, req), "Profile updated successfully",
                                "Failed to update profile");
        }

        @Operation(summary = "Upload profile image", description = "Uploads and sets a new profile picture for the authenticated user. Accepts image files up to 10MB.")
        @PutMapping("/upload/profile-image")
        public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadProfileImage(
                        @RequestHeader("Authorization") String auth,
                        @RequestParam("file") MultipartFile file) throws IOException, GenericException {
                Long profileId = helper.getProfileIdFromHeader(auth);
                return ApiResponse.respond(profileService.uploadProfileImage(profileId, file),
                                "Profile image uploaded successfully", "Failed to upload profile image");
        }

        @Operation(summary = "Upload About image", description = "Uploads and sets the about-me section image for the authenticated user. Accepts image files up to 10MB.")
        @PutMapping("/upload/about-image")
        public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadAboutImage(
                        @RequestHeader("Authorization") String auth,
                        @RequestParam("file") MultipartFile file) throws IOException, GenericException {
                Long profileId = helper.getProfileIdFromHeader(auth);
                return ApiResponse.respond(profileService.uploadAboutMeImage(profileId, file),
                                "About Me image uploaded successfully", "Failed to upload about me image");
        }

        @Operation(summary = "Upload logo", description = "Uploads and sets the logo image for the authenticated user's profile. Accepts image files up to 10MB.")
        @PutMapping("/upload/logo")
        public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadLogo(
                        @RequestHeader("Authorization") String auth,
                        @RequestParam("file") MultipartFile file) throws IOException, GenericException {
                Long profileId = helper.getProfileIdFromHeader(auth);
                return ApiResponse.respond(profileService.uploadLogoImage(profileId, file),
                                "Logo uploaded successfully", "Failed to upload logo");
        }

        @Operation(summary = "Get all profiles with pagination", description = "Returns a paginated list of all user profiles with optional search, status, and role filters.")
        @GetMapping("/all")
        public ResponseEntity<ResponseModel<Page<UserResponse>>> getAllProfiles(
                        Pageable pageable,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String role) {
                Page<UserResponse> profiles = profileService.getAllProfiles(
                                pageable, search, status, role);
                return ApiResponse.respond(
                                profiles,
                                "Profiles fetched successfully",
                                "Failed to fetch profiles");
        }

        @Operation(summary = "Get all users (Admin)", description = "Returns a paginated list of all user accounts. Requires SUPER_ADMIN role.")
        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @GetMapping("/users")
        public ResponseEntity<ResponseModel<Page<UserResponse>>> getAllUsers(
                        Pageable pageable,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String role) {
                Page<UserResponse> users = profileService.getAllProfiles(
                                pageable, search, status, role);
                return ApiResponse.respond(
                                users,
                                "Users fetched successfully",
                                "Failed to fetch users");
        }

        @Operation(summary = "Get user by ID (Admin)", description = "Returns a user account details by ID. Requires SUPER_ADMIN role.")
        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @GetMapping("/users/{id}")
        public ResponseEntity<ResponseModel<UserResponse>> getUserById(@PathVariable Long id)
                        throws GenericException {
                UserResponse user = profileService.getUserById(id);
                return ApiResponse.respond(
                                user,
                                "User fetched successfully",
                                "Failed to fetch user");
        }

        @Operation(summary = "Update user status (Admin)", description = "Updates the account status (ACTIVE/INACTIVE/SUSPENDED) for a user by ID. Requires SUPER_ADMIN role.")
        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @PutMapping("/users/{id}/status")
        public ResponseEntity<ResponseModel<UserResponse>> updateUserStatus(
                        @PathVariable Long id,
                        @RequestBody StatusUpdateRequest request) throws GenericException {
                UserResponse user = profileService.updateUserStatus(id, request);
                return ApiResponse.respond(
                                user,
                                "User status updated successfully",
                                "Failed to update user status");
        }

        @Operation(summary = "Update user role (Admin)", description = "Assigns a new role to a user by ID. Requires SUPER_ADMIN role.")
        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @PutMapping("/users/{id}/role")
        public ResponseEntity<ResponseModel<UserResponse>> updateUserRole(
                        @PathVariable Long id,
                        @RequestBody RoleUpdateRequest request) throws GenericException {
                UserResponse user = profileService.updateUserRole(id, request);
                return ApiResponse.respond(
                                user,
                                "User role updated successfully",
                                "Failed to update user role");
        }

        @Operation(summary = "Toggle user verification (Admin)", description = "Toggles the email-verification status of a user account by ID. Requires SUPER_ADMIN role.")
        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @PutMapping("/users/{id}/verify")
        public ResponseEntity<ResponseModel<UserResponse>> toggleUserVerification(@PathVariable Long id)
                        throws GenericException {
                UserResponse user = profileService.toggleUserVerification(id);
                return ApiResponse.respond(
                                user,
                                "User verification toggled successfully",
                                "Failed to toggle user verification");
        }
}

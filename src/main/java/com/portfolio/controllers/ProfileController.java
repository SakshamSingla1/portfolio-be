package com.portfolio.controllers;

import com.portfolio.dtos.ProfileRequest;
import com.portfolio.dtos.ProfileResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.servicesImpl.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile", description = "Endpoints for managing user profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Operation(summary = "Get profile by ID", description = "Fetches user profile details by profile ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ProfileResponse>> get(@PathVariable String id) throws GenericException {
        return ApiResponse.respond(
                profileService.get(id),
                "Profile fetched successfully",
                "Failed to fetch profile"
        );
    }

    @Operation(summary = "Update profile", description = "Updates profile details for a given profile ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ProfileResponse>> update(
            @PathVariable String id,
            @RequestBody ProfileRequest req) throws GenericException {
        return ApiResponse.respond(
                profileService.update(id, req),
                "Profile updated successfully",
                "Failed to update profile"
        );
    }
}

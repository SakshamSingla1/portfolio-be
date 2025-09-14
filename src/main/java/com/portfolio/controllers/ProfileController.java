package com.portfolio.controllers;

import com.portfolio.dtos.ProfileRequest;
import com.portfolio.dtos.ProfileResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // 🔹 GET PROFILE BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ProfileResponse>> get(@PathVariable Integer id) throws GenericException {
        return ApiResponse.respond(
                profileService.get(id),
                "Profile fetched successfully",
                "Failed to fetch profile"
        );
    }

    // 🔹 UPDATE PROFILE
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ProfileResponse>> update(
            @PathVariable Integer id,
            @RequestBody ProfileRequest req
    ) throws GenericException {
        return ApiResponse.respond(
                profileService.update(id, req),
                "Profile updated successfully",
                "Failed to update profile"
        );
    }
}

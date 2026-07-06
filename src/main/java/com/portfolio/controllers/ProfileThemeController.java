package com.portfolio.controllers;

import com.portfolio.dtos.ProfileTheme.ProfileThemeRequest;
import com.portfolio.dtos.ProfileTheme.ProfileThemeResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProfileThemeService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/profile-themes")
@RequiredArgsConstructor
public class ProfileThemeController {

    private final ProfileThemeService profileThemeService;
    private final Helper helper;

    @Operation(summary = "Get active theme", description = "Returns the currently active color theme for the authenticated user's profile.")
    @GetMapping
    public ResponseEntity<ResponseModel<ProfileThemeResponse>> getTheme(@RequestHeader("Authorization") String auth)
            throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        ProfileThemeResponse response = profileThemeService.getThemeByProfileId(profileId);
        return ApiResponse.respond(response, "Active theme fetched successfully", "Failed to fetch active theme");
    }

    @Operation(summary = "Set profile theme", description = "Sets or updates the active color theme for the authenticated user's profile.")
    @PostMapping
    public ResponseEntity<ResponseModel<ProfileThemeResponse>> setTheme(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody ProfileThemeRequest request) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        ProfileThemeResponse response = profileThemeService.setThemeForProfile(profileId, request);
        return ApiResponse.respond(response, "Theme updated successfully", "Failed to update theme");
    }

    @Operation(summary = "Get profiles by theme", description = "Returns a list of all profiles currently using the specified theme ID.")
    @GetMapping("/theme/{themeId}")
    public ResponseEntity<ResponseModel<List<ProfileThemeResponse>>> getProfilesByThemeId(@PathVariable Long themeId)
            throws GenericException {
        List<ProfileThemeResponse> response = profileThemeService.getProfilesByThemeId(themeId);
        return ApiResponse.respond(response, "Profiles fetched by theme successfully",
                "Failed to fetch profiles by theme");
    }

    @Operation(summary = "Count profiles by theme", description = "Returns the total number of profiles using the specified theme ID.")
    @GetMapping("/theme/{themeId}/count")
    public ResponseEntity<ResponseModel<Long>> countProfilesByThemeId(@PathVariable Long themeId) {
        Long response = profileThemeService.countProfilesByThemeId(themeId);
        return ApiResponse.respond(response, "Theme usage count fetched successfully",
                "Failed to fetch theme usage count");
    }
}

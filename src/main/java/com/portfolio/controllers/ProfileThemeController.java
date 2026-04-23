package com.portfolio.controllers;

import com.portfolio.dtos.ProfileTheme.ProfileThemeRequest;
import com.portfolio.dtos.ProfileTheme.ProfileThemeResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProfileThemeService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile-themes")
@RequiredArgsConstructor
public class ProfileThemeController {

    private final ProfileThemeService profileThemeService;
    private final Helper helper;

    @GetMapping
    public ResponseEntity<ResponseModel<ProfileThemeResponse>> getMyTheme(@RequestHeader("Authorization") String auth)
            throws GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        ProfileThemeResponse response = profileThemeService.getThemeByProfileId(profileId);
        return ApiResponse.respond(response, "Active theme fetched successfully", "Failed to fetch active theme");
    }

    @PostMapping
    public ResponseEntity<ResponseModel<ProfileThemeResponse>> setThemeForMe(
            @RequestHeader("Authorization") String auth,
            @RequestBody ProfileThemeRequest request) throws GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        ProfileThemeResponse response = profileThemeService.setThemeForProfile(profileId, request);
        return ApiResponse.respond(response, "Theme updated successfully", "Failed to update theme");
    }

    @GetMapping("/theme/{themeId}")
    public ResponseEntity<ResponseModel<List<ProfileThemeResponse>>> getProfilesByThemeId(@PathVariable String themeId)
            throws GenericException {
        List<ProfileThemeResponse> response = profileThemeService.getProfilesByThemeId(themeId);
        return ApiResponse.respond(response, "Profiles fetched by theme successfully",
                "Failed to fetch profiles by theme");
    }

    @GetMapping("/theme/{themeId}/count")
    public ResponseEntity<ResponseModel<Long>> countProfilesByThemeId(@PathVariable String themeId) {
        Long response = profileThemeService.countProfilesByThemeId(themeId);
        return ApiResponse.respond(response, "Theme usage count fetched successfully",
                "Failed to fetch theme usage count");
    }
}

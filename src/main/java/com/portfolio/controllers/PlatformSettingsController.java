package com.portfolio.controllers;

import com.portfolio.dtos.Platform.PlatformSettingsDTO;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.PlatformSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlatformSettingsController {

    private final PlatformSettingsService platformSettingsService;

    @Operation(summary = "Get platform settings (public)", description = "Returns platform-level configuration such as registration settings, feature flags, and branding info. No authentication required.")
    @GetMapping("api/v1/public/platform-settings")
    public ResponseEntity<ResponseModel<PlatformSettingsDTO>> getSettings() {
        return ApiResponse.respond(
                platformSettingsService.getSettings(),
                "Settings fetched successfully",
                "Failed to fetch settings"
        );
    }
}

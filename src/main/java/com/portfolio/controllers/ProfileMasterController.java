package com.portfolio.controllers;

import com.portfolio.dtos.ProfileMasterResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProfileMasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/profile-master")
@Tag(name = "Profile Master", description = "APIs for managing Profile Master details")
@RequiredArgsConstructor
public class ProfileMasterController {
    private final ProfileMasterService profileMasterService;

    @Operation(
            summary = "Get Profile Master Data by Domain",
            description = "Fetch profile master data based on request domain"
    )
    @GetMapping
    public ResponseEntity<ResponseModel<ProfileMasterResponse>> getProfileMasterByDomain(HttpServletRequest request) throws GenericException {
        String domain = request.getHeader("Referer");
        ProfileMasterResponse response = profileMasterService.getProfileMasterData(domain);
        return ApiResponse.respond( response, "Profile details fetched successfully", "Failed to fetch profile details"
        );
    }
}

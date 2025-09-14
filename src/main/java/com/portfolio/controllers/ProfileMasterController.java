package com.portfolio.controllers;

import com.portfolio.dtos.ProfileMasterResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProfileMasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile-master")
@Tag(name = "Profile Master", description = "APIs for managing Profile Master details")
public class ProfileMasterController {

    private final ProfileMasterService profileMasterService;

    public ProfileMasterController(ProfileMasterService profileMasterService) {
        this.profileMasterService = profileMasterService;
    }

    @Operation(
            summary = "Get Profile Master Data",
            description = "Fetch profile master data with pagination and optional search"
    )
    @GetMapping("/{profileId}")
    public ResponseEntity<ResponseModel<ProfileMasterResponse>> getProfileMaster(
            @Parameter(description = "Profile ID", example = "1") @PathVariable Integer profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) throws GenericException {
        ProfileMasterResponse response = profileMasterService.getProfileMasterData(profileId, page, size, search);
        return ApiResponse.successResponse(response, ApiResponse.SUCCESS);
    }
}

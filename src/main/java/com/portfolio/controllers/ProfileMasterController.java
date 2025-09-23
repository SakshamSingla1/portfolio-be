package com.portfolio.controllers;

import com.portfolio.dtos.ProfileMasterResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProfileMasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
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
            summary = "Get Profile Master Data by Domain",
            description = "Fetch profile master data based on the request Host header"
    )
    @GetMapping
    public ResponseEntity<ResponseModel<ProfileMasterResponse>> getProfileMasterByDomain(
            HttpServletRequest request
    ) throws GenericException {
        String host = request.getHeader("Referer");
        System.out.println("Host: " + host);
        ProfileMasterResponse response = profileMasterService.getProfileMasterData(host);
        return ApiResponse.successResponse(response, ApiResponse.SUCCESS);
    }

//    // ✅ Keep this only if you also want to fetch by profileId (e.g., for admin panel)
//    @Operation(
//            summary = "Get Profile Master Data by Profile ID",
//            description = "Fetch profile master data using profileId (admin use only)"
//    )
//    @GetMapping("/{profileId}")
//    public ResponseEntity<ResponseModel<ProfileMasterResponse>> getProfileMasterById(
//            @PathVariable Integer profileId
//    ) throws GenericException {
//        ProfileMasterResponse response = profileMasterService.getProfileMasterData(profileId);
//        return ApiResponse.successResponse(response, ApiResponse.SUCCESS);
//    }
}

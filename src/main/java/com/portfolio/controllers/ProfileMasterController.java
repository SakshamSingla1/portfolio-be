package com.portfolio.controllers;

import com.portfolio.dtos.ProfileMasterResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProfileMasterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile-master")
public class ProfileMasterController {

    private final ProfileMasterService profileMasterService;

    public ProfileMasterController(ProfileMasterService profileMasterService) {
        this.profileMasterService = profileMasterService;
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ResponseModel<ProfileMasterResponse>> getProfileMaster(
            @PathVariable Integer profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) throws GenericException {
        ProfileMasterResponse response = profileMasterService.getProfileMasterData(profileId, page, size, search);
        return ApiResponse.successResponse(response, ApiResponse.SUCCESS);
    }
}

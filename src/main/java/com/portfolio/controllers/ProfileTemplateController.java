package com.portfolio.controllers;

import com.portfolio.dtos.ProfileTemplate.ProfileTemplateRequest;
import com.portfolio.dtos.ProfileTemplate.ProfileTemplateResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProfileTemplateService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile-templates")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ProfileTemplateController {

    private final ProfileTemplateService profileTemplateService;
    private final Helper helper;

    @Operation(summary = "Get active template", description = "Returns the currently active portfolio template for the authenticated user's profile.")
    @GetMapping
    public ResponseEntity<ResponseModel<ProfileTemplateResponse>> getTemplate(@RequestHeader(value = "Authorization", required = false) String auth)
            throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        ProfileTemplateResponse response = profileTemplateService.getTemplateByProfileId(profileId);
        return ApiResponse.respond(response, "Active template fetched successfully", "Failed to fetch active template");
    }

    @Operation(summary = "Set profile template", description = "Sets the active portfolio template for the authenticated user's profile.")
    @PostMapping
    public ResponseEntity<ResponseModel<ProfileTemplateResponse>> setTemplate(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @Valid @RequestBody ProfileTemplateRequest request) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        ProfileTemplateResponse response = profileTemplateService.setTemplateForProfile(profileId, request);
        return ApiResponse.respond(response, "Template updated successfully", "Failed to update template");
    }

    @Operation(summary = "Reset profile template", description = "Resets the authenticated user's profile back to the default (Classic) template.")
    @DeleteMapping
    public ResponseEntity<ResponseModel<Void>> resetTemplate(@RequestHeader(value = "Authorization", required = false) String auth)
            throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        profileTemplateService.resetTemplateForProfile(profileId);
        return ApiResponse.successResponse(null, "Template reset to default successfully");
    }

    @Operation(summary = "Count profiles by template", description = "Returns how many profiles currently use the given template key.")
    @GetMapping("/template/{templateKey}/count")
    public ResponseEntity<ResponseModel<Long>> countProfilesByTemplateKey(@PathVariable String templateKey) {
        Long response = profileTemplateService.countProfilesByTemplateKey(templateKey);
        return ApiResponse.respond(response, "Template usage count fetched successfully",
                "Failed to fetch template usage count");
    }
}

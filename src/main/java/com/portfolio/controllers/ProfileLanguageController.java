package com.portfolio.controllers;

import com.portfolio.dtos.Language.ProfileLanguageRequest;
import com.portfolio.dtos.Language.ProfileLanguageResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProfileLanguageService;
import com.portfolio.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("api/v1/languages")
@RequiredArgsConstructor
public class ProfileLanguageController {

    private final ProfileLanguageService profileLanguageService;
    private final Helper helper;

    @PostMapping
    public ResponseEntity<ResponseModel<ProfileLanguageResponse>> create(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody ProfileLanguageRequest req) throws GenericException {
        req.setProfileId(helper.getProfileIdFromHeader(auth));
        return ApiResponse.respond(profileLanguageService.create(req), "Language created successfully", "Failed to create language");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ProfileLanguageResponse>> update(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @Valid @RequestBody ProfileLanguageRequest req) throws GenericException {
        req.setProfileId(helper.getProfileIdFromHeader(auth));
        return ApiResponse.respond(profileLanguageService.update(id, req), "Language updated successfully", "Failed to update language");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ProfileLanguageResponse>> getById(
            @PathVariable Long id) throws GenericException {
        return ApiResponse.respond(profileLanguageService.getById(id), "Language fetched", "Language not found");
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<ProfileLanguageResponse>>> getAll(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) String search,
            Pageable pageable) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(profileLanguageService.getAll(profileId, search, pageable), "Languages fetched", "Failed to fetch languages");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> delete(@PathVariable Long id) throws GenericException {
        return ApiResponse.respond(profileLanguageService.delete(id), "Language deleted", "Failed to delete language");
    }
}

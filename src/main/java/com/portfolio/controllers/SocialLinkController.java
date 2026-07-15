package com.portfolio.controllers;

import com.portfolio.dtos.SocialLinks.SocialLinkRequestDTO;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.SocialLinkService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/social-links")
@PreAuthorize("isAuthenticated()")
public class SocialLinkController {

    private final SocialLinkService socialLinkService;
    private final Helper helper;

    @Operation(summary = "Create social link", description = "Creates a new social media link for the authenticated user's profile.")
    @PostMapping
    public ResponseEntity<ResponseModel<SocialLinkResponseDTO>> createLink(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @Valid @RequestBody SocialLinkRequestDTO requestDTO) throws GenericException {
        requestDTO.setProfileId(helper.getProfileIdFromHeader(auth));
        SocialLinkResponseDTO responseDTO = socialLinkService.createLink(requestDTO);
        return ApiResponse.respond(responseDTO,"Social Link created successfully","Failed to create social link");
    }

    @Operation(summary = "Update social link", description = "Updates an existing social link by ID for the authenticated user's profile.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<SocialLinkResponseDTO>> updateLink(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long id,
            @Valid @RequestBody SocialLinkRequestDTO requestDTO ) throws GenericException {
        requestDTO.setProfileId(helper.getProfileIdFromHeader(auth));
        SocialLinkResponseDTO responseDTO = socialLinkService.updateLink(id,requestDTO);
        return ApiResponse.respond(responseDTO,"Social Link updated successfully","Failed to update social link");
    }

    @Operation(summary = "Get social link by ID", description = "Fetches a single social link record by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<SocialLinkResponseDTO>> get(@PathVariable Long id) throws GenericException {
        SocialLinkResponseDTO responseDTO = socialLinkService.get(id);
        return ApiResponse.respond(responseDTO,"Social Link fetched successfully","Failed to fetch social link");
    }

    @Operation(summary = "Get all social links", description = "Returns a paginated list of social links for the authenticated user with optional status filter and search.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<SocialLinkResponseDTO>>> getAll(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam(required = false) StatusEnum status,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        Page<SocialLinkResponseDTO> responseDTOS = socialLinkService.getByProfile(profileId,status,pageable, search,sortDir,sortBy);
        return ApiResponse.respond(responseDTOS, "Social Links fetched successfully","failed to fetch social links");
    }

    @Operation(summary = "Delete social link", description = "Deletes a social link record by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteSocialLink(
            @PathVariable Long id
    ) throws GenericException {
        socialLinkService.delete(id);
        return ApiResponse.successResponse(null, "Social Link deleted successfully");
    }


}

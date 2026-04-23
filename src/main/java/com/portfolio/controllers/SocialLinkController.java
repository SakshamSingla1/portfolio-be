package com.portfolio.controllers;

import com.portfolio.dtos.SocialLinks.SocialLinkRequestDTO;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.SocialLinkService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/social-links")
public class SocialLinkController {

    private final SocialLinkService socialLinkService;
    private final Helper helper;

    @PostMapping
    public ResponseEntity<ResponseModel<SocialLinkResponseDTO>> createLink(
            @RequestHeader("Authorization") String auth,
            @RequestBody SocialLinkRequestDTO requestDTO) throws GenericException {
        requestDTO.setProfileId(helper.getProfileIdFromHeader(auth));
        SocialLinkResponseDTO responseDTO = socialLinkService.createLink(requestDTO);
        return ApiResponse.respond(responseDTO, "Social Link created successfully", "Failed to create social link");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<SocialLinkResponseDTO>> updateLink(
            @RequestHeader("Authorization") String auth,
            @PathVariable String id,
            @RequestBody SocialLinkRequestDTO requestDTO) throws GenericException {
        requestDTO.setProfileId(helper.getProfileIdFromHeader(auth));
        SocialLinkResponseDTO responseDTO = socialLinkService.updateLink(id, requestDTO);
        return ApiResponse.respond(responseDTO, "Social Link updated successfully", "Failed to update social link");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<SocialLinkResponseDTO>> get(@PathVariable String id) throws GenericException {
        SocialLinkResponseDTO responseDTO = socialLinkService.get(id);
        return ApiResponse.respond(responseDTO, "Social Link fetched successfully", "Failed to fetch social link");
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<SocialLinkResponseDTO>>> getMySocialLinks(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) StatusEnum status,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) throws GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        Page<SocialLinkResponseDTO> responseDTOS = socialLinkService.getByProfile(profileId, status, pageable, search,
                sortDir, sortBy);
        return ApiResponse.respond(responseDTOS, "Social Links fetched successfully", "failed to fetch social links");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteSocialLink(
            @PathVariable String id) throws GenericException {
        socialLinkService.delete(id);
        return ApiResponse.successResponse(null, "Social Link deleted successfully");
    }

}

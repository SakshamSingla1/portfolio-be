package com.portfolio.controllers;

import com.portfolio.dtos.Resume.ResumeUploadResponseDTO;
import com.portfolio.dtos.SocialLinks.SocialLinkRequestDTO;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.SocialLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/social-links")
public class SocialLinkController {

    private final SocialLinkService socialLinkService;

    @PostMapping
    public ResponseEntity<ResponseModel<SocialLinkResponseDTO>> createLink(@RequestBody SocialLinkRequestDTO requestDTO) throws GenericException {
        SocialLinkResponseDTO responseDTO = socialLinkService.createLink(requestDTO);
        return ApiResponse.respond(responseDTO,"Social Link created successfully","Failed to create social link");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<SocialLinkResponseDTO>> updateLink(
            @PathVariable String id,
            @RequestBody SocialLinkRequestDTO requestDTO ) throws GenericException {
        SocialLinkResponseDTO responseDTO = socialLinkService.updateLink(id,requestDTO);
        return ApiResponse.respond(responseDTO,"Social Link updated successfully","Failed to update social link");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<SocialLinkResponseDTO>> get(@PathVariable String id) throws GenericException {
        SocialLinkResponseDTO responseDTO = socialLinkService.get(id);
        return ApiResponse.respond(responseDTO,"Social Link fetched successfully","Failed to fetch social link");
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<SocialLinkResponseDTO>>>  getAllSocialLinks(
            @RequestParam(required = false) String profileId,
            @RequestParam(required = false) StatusEnum status,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        Page<SocialLinkResponseDTO> responseDTOS = socialLinkService.getByProfile(profileId,status,pageable, search,sortDir,sortBy);
        return ApiResponse.respond(responseDTOS, "Social Links fetched successfully","failed to fetch social links");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteSocialLink(
            @PathVariable String id
    ) throws GenericException {
        socialLinkService.delete(id);
        return ApiResponse.successResponse(null, "Social Link deleted successfully");
    }


}

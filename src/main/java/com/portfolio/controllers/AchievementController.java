package com.portfolio.controllers;

import com.portfolio.dtos.Achievements.AchievementResponseDTO;
import com.portfolio.dtos.Achievements.AchievementRequestDTO;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.AchievementService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("api/v1/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final Helper helper;

    @Operation(summary = "Create achievement", description = "Creates a new achievement record for the authenticated user's profile.")
    @PostMapping
    public ResponseEntity<ResponseModel<AchievementResponseDTO>> createAchievement(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody AchievementRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        AchievementResponseDTO response = achievementService.createAchievement(dto);
        return ApiResponse.respond(response, "Achievement created successfully", "Failed to create Achievement");
    }

    @Operation(summary = "Update achievement", description = "Updates an existing achievement record identified by its ID for the authenticated user's profile.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<AchievementResponseDTO>> updateAchievement(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id, 
            @Valid @RequestBody AchievementRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        AchievementResponseDTO response = achievementService.updateAchievement(id, dto);
        return ApiResponse.respond(response, "Achievement updated successfully", "Failed to update Achievement");
    }

    @Operation(summary = "Get achievement by ID", description = "Retrieves a single achievement record by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<AchievementResponseDTO>> getAchievementById(@PathVariable Long id) throws GenericException {
        AchievementResponseDTO response = achievementService.getAchievementById(id);
        return ApiResponse.respond(response, "Achievement fetched successfully", "Failed to fetch Achievement");
    }

    @Operation(summary = "Get all achievements", description = "Returns a paginated list of achievement records for the authenticated user's profile, with optional keyword search.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<AchievementResponseDTO>>> getAll(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        Page<AchievementResponseDTO> page = achievementService.getByProfile(profileId, search, pageable);
        return ApiResponse.respond(page, "Achievements fetched successfully", "Failed to fetch Achievements");
    }

    @Operation(summary = "Delete achievement", description = "Permanently deletes the achievement record identified by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteAchievement(@PathVariable Long id) throws GenericException {
        achievementService.deleteById(id);
        return ApiResponse.successResponse(null, "Achievement deleted successfully");
    }

    @Operation(summary = "Upload achievement image", description = "Uploads an image file for an achievement and returns the stored image URL for the authenticated user's profile.")
    @PostMapping("/upload")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadImage(
            @RequestHeader("Authorization") String auth,
            @RequestParam("file") MultipartFile file
    ) throws IOException, GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(achievementService.uploadImage(profileId, file), "Image uploaded successfully", "Failed to upload Image");
    }
}

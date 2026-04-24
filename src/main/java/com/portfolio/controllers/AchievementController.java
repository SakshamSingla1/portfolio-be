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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final Helper helper;

    @PostMapping
    public ResponseEntity<ResponseModel<AchievementResponseDTO>> createAchievement(
            @RequestHeader("Authorization") String auth,
            @RequestBody AchievementRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        AchievementResponseDTO response = achievementService.createAchievement(dto);
        return ApiResponse.respond(response, "Achievement created successfully", "Failed to create Achievement");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<AchievementResponseDTO>> updateAchievement(
            @RequestHeader("Authorization") String auth,
            @PathVariable String id, 
            @RequestBody AchievementRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        AchievementResponseDTO response = achievementService.updateAchievement(id, dto);
        return ApiResponse.respond(response, "Achievement updated successfully", "Failed to update Achievement");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<AchievementResponseDTO>> getAchievementById(@PathVariable String id) throws GenericException {
        AchievementResponseDTO response = achievementService.getAchievementById(id);
        return ApiResponse.respond(response, "Achievement fetched successfully", "Failed to fetch Achievement");
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<AchievementResponseDTO>>> getAll(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false, defaultValue = "order") String sortBy,
            Pageable pageable
    ) throws GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        Page<AchievementResponseDTO> page = achievementService.getByProfile(profileId, search, sortDir, sortBy, pageable);
        return ApiResponse.respond(page, "Achievements fetched successfully", "Failed to fetch Achievements");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteAchievement(@PathVariable String id) throws GenericException {
        achievementService.deleteById(id);
        return ApiResponse.respond(null, "Achievement deleted successfully", "Failed to delete Achievement");
    }

    @Operation(summary = "Upload Image")
    @PostMapping("/upload")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadImage(
            @RequestHeader("Authorization") String auth,
            @RequestParam("file") MultipartFile file
    ) throws IOException, GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(achievementService.uploadImage(profileId, file), "Image uploaded successfully", "Failed to upload Image");
    }
}

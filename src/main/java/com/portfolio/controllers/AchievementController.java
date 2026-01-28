package com.portfolio.controllers;

import com.portfolio.dtos.Achievements.AchievementResponseDTO;
import com.portfolio.dtos.Achievements.AchievementRequestDTO;
import com.portfolio.dtos.ImageUploadResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @PostMapping
    public ResponseEntity<ResponseModel<AchievementResponseDTO>> createAchievement(@RequestBody AchievementRequestDTO dto) throws GenericException {
        AchievementResponseDTO response = achievementService.createAchievement(dto);
        return ApiResponse.respond(response, "Achievement created successfully", "Failed to create Achievement");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<AchievementResponseDTO>> updateAchievement(@PathVariable String id, @RequestBody AchievementRequestDTO dto) throws GenericException {
        AchievementResponseDTO response = achievementService.updateAchievement(id, dto);
        return ApiResponse.respond(response, "Achievement updated successfully", "Failed to update Achievement");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<AchievementResponseDTO>> getAchievementById(@PathVariable String id) throws GenericException {
        AchievementResponseDTO response = achievementService.getAchievementById(id);
        return ApiResponse.respond(response, "Achievement fetched successfully", "Failed to fetch Achievement");
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<AchievementResponseDTO>>> getAchievement(
            @RequestParam(required = false) String profileId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false, defaultValue = "order") String sortBy,
            Pageable pageable
    ) {
        Page<AchievementResponseDTO> page = achievementService.getByProfile(profileId, search, sortDir, sortBy, pageable);
        return ApiResponse.respond(page, "Achievements fetched successfully", "Failed to fetch Achievements");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteAchievement(@PathVariable String id) throws GenericException {
        achievementService.deleteById(id);
        return ApiResponse.respond(null, "Achievement deleted successfully", "Failed to delete Achievement");
    }

    @Operation(summary = "Upload Image")
    @PostMapping("/{profileId}/upload")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadImage(
            @PathVariable String profileId,
            @RequestParam("file") MultipartFile file
    ) throws IOException, GenericException {
        return ApiResponse.respond(achievementService.uploadImage(profileId, file), "Image uploaded successfully", "Failed to upload Image");
    }
}

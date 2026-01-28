package com.portfolio.services;

import com.portfolio.dtos.Achievements.AchievementRequestDTO;
import com.portfolio.dtos.Achievements.AchievementResponseDTO;
import com.portfolio.dtos.ImageUploadResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AchievementService {
    AchievementResponseDTO createAchievement(AchievementRequestDTO AchievementRequestDTO) throws GenericException;
    AchievementResponseDTO updateAchievement(String id, AchievementRequestDTO AchievementDTO) throws GenericException;
    AchievementResponseDTO getAchievementById(String id) throws GenericException;
    Page<AchievementResponseDTO> getByProfile(String profileId, String search, String sortDir, String sortBy, Pageable pageable);
    Void deleteById(String id) throws GenericException;
    ImageUploadResponse uploadImage(String id,MultipartFile file) throws GenericException, IOException;
}

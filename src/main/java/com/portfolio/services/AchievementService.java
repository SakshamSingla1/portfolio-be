package com.portfolio.services;

import com.portfolio.dtos.Achievements.AchievementRequestDTO;
import com.portfolio.dtos.Achievements.AchievementResponseDTO;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AchievementService {
    AchievementResponseDTO createAchievement(AchievementRequestDTO AchievementRequestDTO) throws GenericException;
    AchievementResponseDTO updateAchievement(Long id, AchievementRequestDTO AchievementDTO) throws GenericException;
    AchievementResponseDTO getAchievementById(Long id) throws GenericException;
    Page<AchievementResponseDTO> getByProfile(Long profileId, String search, Pageable pageable);
    Void deleteById(Long id) throws GenericException;
    ImageUploadResponse uploadImage(Long id,MultipartFile file) throws GenericException, IOException;
    List<AchievementResponseDTO> getByProfile(Long profileId);
}

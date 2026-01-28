package com.portfolio.servicesImpl;

import com.portfolio.dtos.Achievements.AchievementRequestDTO;
import com.portfolio.dtos.Achievements.AchievementResponseDTO;
import com.portfolio.dtos.ImageUploadResponse;
import com.portfolio.entities.Achievements;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.AchievementRepository;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.services.AchievementService;
import com.portfolio.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final ProfileRepository profileRepository;
    private final CloudinaryService cloudinaryService;
    private final AchievementRepository achievementRepository;

    @Override
    public AchievementResponseDTO createAchievement(AchievementRequestDTO dto) throws GenericException {
        if (achievementRepository.existsByProfileIdAndOrder(dto.getProfileId(), dto.getOrder())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_ACHIEVEMENT, "Achievement already exists with same order for the profile");
        }
        Achievements achievement = Achievements.builder()
                .profileId(dto.getProfileId())
                .title(dto.getTitle())
                .issuer(dto.getIssuer())
                .description(dto.getDescription())
                .achievedAt(dto.getAchievedAt())
                .proofUrl(dto.getProofUrl())
                .proofPublicId(dto.getProofPublicId())
                .status(dto.getStatus())
                .order(dto.getOrder())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return mapToResponse(achievementRepository.save(achievement));
    }

    @Override
    public AchievementResponseDTO updateAchievement(String id, AchievementRequestDTO dto) throws GenericException {
        Achievements existing = achievementRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ACHIEVEMENT_NOT_FOUND, "Achievement not found"));
        if (dto.getOrder() != null && achievementRepository.existsByProfileIdAndOrderAndIdNot(existing.getProfileId(), dto.getOrder(), existing.getId())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_ACHIEVEMENT, "Achievement already exists with same order for the profile");
        }
        existing.setTitle(dto.getTitle());
        existing.setIssuer(dto.getIssuer());
        existing.setDescription(dto.getDescription());
        existing.setAchievedAt(dto.getAchievedAt());
        existing.setProofUrl(dto.getProofUrl());
        existing.setProofPublicId(dto.getProofPublicId());
        existing.setStatus(dto.getStatus());
        existing.setOrder(dto.getOrder());
        existing.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(achievementRepository.save(existing));
    }

    @Override
    public AchievementResponseDTO getAchievementById(String id) throws GenericException {
        Achievements achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ACHIEVEMENT_NOT_FOUND, "Achievement not found"));
        return mapToResponse(achievement);
    }

    @Override
    public Page<AchievementResponseDTO> getByProfile(String profileId, String search, String sortDir, String sortBy, Pageable pageable) {
        String finalSortBy = (sortBy != null && !sortBy.isBlank()) ? sortBy : "order";
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                finalSortBy
        );
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        Page<Achievements> page;
        if (profileId != null && search != null && !search.isBlank()) {
            page = achievementRepository.findByProfileIdWithSearch(profileId, search, sortedPageable);
        } else if (profileId != null) {
            page = achievementRepository.findByProfileId(profileId, sortedPageable);
        } else if (search != null && !search.isBlank()) {
            page = achievementRepository.findBySearch(search, sortedPageable);
        } else {
            page = achievementRepository.findAll(sortedPageable);
        }
        return page.map(this::mapToResponse);
    }

    @Override
    public Void deleteById(String id) throws GenericException {
        if (!achievementRepository.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.ACHIEVEMENT_NOT_FOUND, "Achievement not found");
        }
        achievementRepository.deleteById(id);
        return null;
    }

    @Override
    public ImageUploadResponse uploadImage(
            String profileId,
            MultipartFile file
    ) throws GenericException, IOException {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        Map uploadResult = cloudinaryService.uploadFile(file);
        return new ImageUploadResponse(uploadResult.get("secure_url").toString(), uploadResult.get("public_id").toString());
    }

    private AchievementResponseDTO mapToResponse(Achievements c) {
        return AchievementResponseDTO.builder()
                .id(c.getId())
                .title(c.getTitle())
                .issuer(c.getIssuer())
                .description(c.getDescription())
                .achievedAt(c.getAchievedAt())
                .proofUrl(c.getProofUrl())
                .proofPublicId(c.getProofPublicId())
                .order(c.getOrder())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}

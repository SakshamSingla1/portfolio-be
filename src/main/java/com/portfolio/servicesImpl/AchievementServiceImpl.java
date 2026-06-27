package com.portfolio.servicesImpl;

import com.portfolio.dtos.Achievements.AchievementRequestDTO;
import com.portfolio.dtos.Achievements.AchievementResponseDTO;
import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.entities.Achievements;
import com.portfolio.entities.FileAsset;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.AchievementRepository;
import com.portfolio.repositories.FileAssetRepository;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.services.AchievementService;
import com.portfolio.services.FileService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final ProfileRepository profileRepository;
    private final FileService fileService;
    private final FileAssetRepository fileAssetRepository;
    private final AchievementRepository achievementRepository;
    private final Helper helper;

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
                .status(dto.getStatus())
                .order(dto.getOrder())
                .build();
        Achievements saved = achievementRepository.save(achievement);
        linkFileAsset(saved.getId(), dto.getProofPublicId(), dto.getProofUrl());
        return mapToResponse(saved);
    }
 
    @Override
    public AchievementResponseDTO updateAchievement(Long id, AchievementRequestDTO dto) throws GenericException {
        Achievements existing = achievementRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ACHIEVEMENT_NOT_FOUND, "Achievement not found"));
        if (dto.getOrder() != null && achievementRepository.existsByProfileIdAndOrderAndIdNot(existing.getProfileId(), dto.getOrder(), existing.getId())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_ACHIEVEMENT, "Achievement already exists with same order for the profile");
        }
        existing.setTitle(dto.getTitle());
        existing.setIssuer(dto.getIssuer());
        existing.setDescription(dto.getDescription());
        existing.setAchievedAt(dto.getAchievedAt());
        existing.setStatus(dto.getStatus());
        existing.setOrder(dto.getOrder());
        Achievements saved = achievementRepository.save(existing);
        linkFileAsset(id, dto.getProofPublicId(), dto.getProofUrl());
        return mapToResponse(saved);
    }
 
    @Override
    public AchievementResponseDTO getAchievementById(Long id) throws GenericException {
        Achievements achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ACHIEVEMENT_NOT_FOUND, "Achievement not found"));
        return mapToResponse(achievement);
    }
 
    @Override
    public Page<AchievementResponseDTO> getByProfile(Long profileId, String search, String sortDir, String sortBy, Pageable pageable) {
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
    public Void deleteById(Long id) throws GenericException {
        if (!achievementRepository.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.ACHIEVEMENT_NOT_FOUND, "Achievement not found");
        }
        try {
            fileService.deleteByResource(String.valueOf(id), ResourceTypeEnum.ACHIEVEMENT.name());
        } catch (Exception ignored) {}
        achievementRepository.deleteById(id);
        return null;
    }
 
    @Override
    public ImageUploadResponse uploadImage(
            Long profileId,
            MultipartFile file
    ) throws GenericException, IOException {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId("TEMP_" + profileId);
        uploadReq.setResourceType(ResourceTypeEnum.ACHIEVEMENT);
        uploadReq.setPrimary(true);
        try {
            FileAssetDTO asset = fileService.upload(file, uploadReq);
            return new ImageUploadResponse(asset.getPath(), asset.getPublicId());
        } catch (Exception e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Failed to upload achievement image: " + e.getMessage());
        }
    }
 
    public List<AchievementResponseDTO> getByProfile(Long profileId) {
        return achievementRepository
                .findByProfileIdAndStatusOrderByOrderAsc(profileId, StatusEnum.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
 
    private void linkFileAsset(Long resourceId, String publicId, String url) {
        if (publicId == null || publicId.isBlank()) return;
        
        Optional<FileAsset> assetOpt = Optional.empty();
        try {
            Long assetId = Long.parseLong(publicId);
            assetOpt = fileAssetRepository.findById(assetId);
        } catch (NumberFormatException e) {
            assetOpt = fileAssetRepository.findByPublicId(publicId);
        }

        if (assetOpt.isEmpty() && url != null && !url.isBlank()) {
            assetOpt = fileAssetRepository.findByPath(url);
        }

        Long targetAssetId = assetOpt.map(FileAsset::getId).orElse(null);

        List<FileAsset> existing = fileAssetRepository.findByResourceIdAndResourceTypeOrderBySortOrderAsc(String.valueOf(resourceId), ResourceTypeEnum.ACHIEVEMENT);
        for (FileAsset asset : existing) {
            if (targetAssetId == null || !targetAssetId.equals(asset.getId())) {
                try { fileService.delete(asset.getId()); } catch (Exception ignored) {}
            }
        }

        if (assetOpt.isPresent()) {
            FileAsset asset = assetOpt.get();
            asset.setResourceId(String.valueOf(resourceId));
            asset.setPrimary(true);
            fileAssetRepository.save(asset);
        } else {
            FileAsset asset = new FileAsset();
            asset.setResourceId(String.valueOf(resourceId));
            asset.setResourceType(ResourceTypeEnum.ACHIEVEMENT);
            asset.setPath(url);
            asset.setPublicId(publicId);
            asset.setPrimary(true);
            fileAssetRepository.save(asset);
        }
    }
 
    private AchievementResponseDTO mapToResponse(Achievements c) {
        String proofUrl = null;
        String proofPublicId = null;
        Optional<FileAsset> assetOpt = fileAssetRepository.findByResourceIdAndResourceTypeAndIsPrimaryTrue(String.valueOf(c.getId()), ResourceTypeEnum.ACHIEVEMENT);
        if (assetOpt.isPresent()) {
            proofUrl = assetOpt.get().getPath();
            proofPublicId = assetOpt.get().getPublicId();
        }
        AchievementResponseDTO responseDTO = AchievementResponseDTO.builder()
                .id(c.getId())
                .title(c.getTitle())
                .issuer(c.getIssuer())
                .description(c.getDescription())
                .achievedAt(c.getAchievedAt())
                .proofUrl(proofUrl)
                .proofPublicId(proofPublicId)
                .order(c.getOrder())
                .status(c.getStatus())
                .build();
        helper.setAudit(c, responseDTO);
        return responseDTO;
    }
}

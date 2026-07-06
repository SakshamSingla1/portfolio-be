package com.portfolio.servicesImpl;

import com.portfolio.dao.achievement.AchievementDao;
import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dao.profile.ProfileDao;
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
import com.portfolio.services.AchievementService;
import com.portfolio.services.FileService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final ProfileDao profileDao;
    private final FileService fileService;
    private final FileAssetDao fileAssetDao;
    private final AchievementDao achievementDao;
    private final Helper helper;

    @Override
    @Transactional
    public AchievementResponseDTO createAchievement(AchievementRequestDTO dto) throws GenericException {
        if (achievementDao.existsByProfileIdAndOrder(dto.getProfileId(), dto.getOrder() != null ? Integer.parseInt(dto.getOrder()) : null)) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_ACHIEVEMENT, "Achievement already exists with same order for the profile");
        }
        Achievements achievement = Achievements.builder()
                .profileId(dto.getProfileId())
                .title(dto.getTitle())
                .issuer(dto.getIssuer())
                .description(dto.getDescription())
                .achievedAt(dto.getAchievedAt())
                .status(dto.getStatus())
                .order(dto.getOrder() != null ? Integer.parseInt(dto.getOrder()) : null)
                .build();
        Achievements saved = achievementDao.save(achievement);
        linkFileAsset(saved.getId(), dto.getProofPublicId(), dto.getProofUrl());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public AchievementResponseDTO updateAchievement(Long id, AchievementRequestDTO dto) throws GenericException {
        Achievements existing = achievementDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ACHIEVEMENT_NOT_FOUND, "Achievement not found"));
        if (dto.getOrder() != null && achievementDao.existsByProfileIdAndOrderAndIdNot(existing.getProfileId(), Integer.parseInt(dto.getOrder()), existing.getId())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_ACHIEVEMENT, "Achievement already exists with same order for the profile");
        }
        existing.setTitle(dto.getTitle());
        existing.setIssuer(dto.getIssuer());
        existing.setDescription(dto.getDescription());
        existing.setAchievedAt(dto.getAchievedAt());
        existing.setStatus(dto.getStatus());
        existing.setOrder(dto.getOrder() != null ? Integer.parseInt(dto.getOrder()) : null);
        Achievements saved = achievementDao.save(existing);
        linkFileAsset(id, dto.getProofPublicId(), dto.getProofUrl());
        return mapToResponse(saved);
    }

    @Override
    public AchievementResponseDTO getAchievementById(Long id) throws GenericException {
        return achievementDao.findDTOById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ACHIEVEMENT_NOT_FOUND, "Achievement not found"));
    }

    @Override
    public Page<AchievementResponseDTO> getByProfile(Long profileId, String search, Pageable pageable) {
        return achievementDao.findByCriteria(profileId,search,pageable);
    }

    @Override
    public Void deleteById(Long id) throws GenericException {
        if (!achievementDao.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.ACHIEVEMENT_NOT_FOUND, "Achievement not found");
        }
        try {
            fileService.deleteByResource(id, ResourceTypeEnum.ACHIEVEMENT.name());
        } catch (Exception ignored) {}
        achievementDao.deleteById(id);
        return null;
    }

    @Override
    public ImageUploadResponse uploadImage(
            Long profileId,
            MultipartFile file
    ) throws GenericException, IOException {
        profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId(profileId);
        uploadReq.setResourceType(ResourceTypeEnum.ACHIEVEMENT);
        uploadReq.setPrimary(true);
        try {
            FileAssetDTO asset = fileService.upload(file, uploadReq);
            return new ImageUploadResponse(asset.getPath(), asset.getPublicId());
        } catch (Exception e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Failed to upload achievement image: " + e.getMessage());
        }
    }

    @Override
    public List<AchievementResponseDTO> getByProfile(Long profileId) {
        return achievementDao
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
            assetOpt = fileAssetDao.findById(assetId);
        } catch (NumberFormatException e) {
            assetOpt = fileAssetDao.findByPublicId(publicId);
        }

        if (assetOpt.isEmpty() && url != null && !url.isBlank()) {
            assetOpt = fileAssetDao.findByPath(url);
        }

        Long targetAssetId = assetOpt.map(FileAsset::getId).orElse(null);

        List<FileAsset> existing = fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(resourceId, ResourceTypeEnum.ACHIEVEMENT);
        for (FileAsset asset : existing) {
            if (targetAssetId == null || !targetAssetId.equals(asset.getId())) {
                try { fileService.delete(asset.getId()); } catch (Exception ignored) {}
            }
        }

        if (assetOpt.isPresent()) {
            FileAsset asset = assetOpt.get();
            asset.setResourceId(resourceId);
            asset.setPrimary(true);
            fileAssetDao.save(asset);
        } else {
            FileAsset asset = new FileAsset();
            asset.setResourceId(resourceId);
            asset.setResourceType(ResourceTypeEnum.ACHIEVEMENT);
            asset.setPath(url);
            asset.setPublicId(publicId);
            asset.setPrimary(true);
            fileAssetDao.save(asset);
        }
    }

    private AchievementResponseDTO mapToResponse(Achievements c) {
        String proofUrl = null;
        String proofPublicId = null;
        Optional<FileAsset> assetOpt = fileAssetDao.findByResourceIdAndResourceTypeAndIsPrimaryTrue(c.getId(), ResourceTypeEnum.ACHIEVEMENT);
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

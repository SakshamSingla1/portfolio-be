package com.portfolio.servicesImpl;

import com.portfolio.dao.certification.CertificationDao;
import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dtos.Certifications.CertificationRequestDTO;
import com.portfolio.dtos.Certifications.CertificationResponseDTO;
import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.entities.Certifications;
import com.portfolio.entities.FileAsset;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.CertificationService;
import com.portfolio.services.FileService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final CertificationDao certificationDao;
    private final ProfileDao profileDao;
    private final FileService fileService;
    private final FileAssetDao fileAssetDao;
    private final Helper helper;

    @Override
    public CertificationResponseDTO createCertification(CertificationRequestDTO dto) throws GenericException {
        if (certificationDao.existsByProfileIdAndOrder(dto.getProfileId(), dto.getOrder())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_CERTIFICATION, "Certification already exists with same order for the profile");
        }
        Certifications certification = Certifications.builder()
                .profileId(dto.getProfileId())
                .title(dto.getTitle())
                .issuer(dto.getIssuer())
                .credentialId(dto.getCredentialId())
                .issueDate(dto.getIssueDate())
                .expiryDate(dto.getExpiryDate())
                .status(dto.getStatus())
                .order(dto.getOrder())
                .build();
        Certifications saved = certificationDao.save(certification);
        linkFileAsset(saved.getId(), dto.getCredentialUrl());
        return mapToResponse(saved);
    }

    @Override
    public CertificationResponseDTO updateCertification(Long id, CertificationRequestDTO dto) throws GenericException {
        Certifications existing = certificationDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.CERTIFICATION_NOT_FOUND, "Certification not found"));
        if (dto.getOrder() != null && certificationDao.existsByProfileIdAndOrderAndIdNot(existing.getProfileId(), dto.getOrder(), existing.getId())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_CERTIFICATION, "Certification already exists with same order for the profile");
        }
        existing.setTitle(dto.getTitle());
        existing.setIssuer(dto.getIssuer());
        existing.setCredentialId(dto.getCredentialId());
        existing.setIssueDate(dto.getIssueDate());
        existing.setExpiryDate(dto.getExpiryDate());
        existing.setStatus(dto.getStatus());
        existing.setOrder(dto.getOrder());
        existing.setUpdatedAt(LocalDateTime.now());
        Certifications saved = certificationDao.save(existing);
        linkFileAsset(id, dto.getCredentialUrl());
        return mapToResponse(saved);
    }

    @Override
    public CertificationResponseDTO getCertificationById(Long id) throws GenericException {
        return certificationDao.findDTOById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.CERTIFICATION_NOT_FOUND, "Certification not found"));
    }

    @Override
    public Page<CertificationResponseDTO> getByProfile(Long profileId, String search, Pageable pageable) {
        return certificationDao.findByCriteria(profileId,search,pageable);
    }

    @Override
    public Void deleteById(Long id) throws GenericException {
        if (!certificationDao.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.CERTIFICATION_NOT_FOUND, "Certification not found");
        }
        try {
            fileService.deleteByResource(id.intValue(), ResourceTypeEnum.CERTIFICATION.name());
        } catch (Exception ignored) {}
        certificationDao.deleteById(id);
        return null;
    }

    @Override
    public ImageUploadResponse uploadCredentialImage(
            Long profileId,
            MultipartFile file
    ) throws GenericException, IOException {
        profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId(profileId.intValue());
        uploadReq.setResourceType(ResourceTypeEnum.CERTIFICATION);
        uploadReq.setPrimary(true);
        try {
            FileAssetDTO asset = fileService.upload(file, uploadReq);
            return new ImageUploadResponse(asset.getPath(), asset.getPublicId());
        } catch (Exception e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Failed to upload certification image: " + e.getMessage());
        }
    }

    public List<CertificationResponseDTO> getByProfile(Long profileId) {
        return certificationDao
                .findByProfileIdAndStatusOrderByOrderAsc(profileId, StatusEnum.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void linkFileAsset(Long resourceId, String url) {
        if (url == null || url.isBlank()) return;
        List<FileAsset> existing = fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(resourceId.intValue(), ResourceTypeEnum.CERTIFICATION);
        for (FileAsset asset : existing) {
            if (!url.equals(asset.getPath())) {
                try { fileService.delete(asset.getId()); } catch (Exception ignored) {}
            }
        }
        Optional<FileAsset> assetOpt = fileAssetDao.findByPath(url);
        if (assetOpt.isPresent()) {
            FileAsset asset = assetOpt.get();
            asset.setResourceId(resourceId.intValue());
            asset.setPrimary(true);
            fileAssetDao.save(asset);
        } else {
            FileAsset asset = new FileAsset();
            asset.setResourceId(resourceId.intValue());
            asset.setResourceType(ResourceTypeEnum.CERTIFICATION);
            asset.setPath(url);
            asset.setPrimary(true);
            fileAssetDao.save(asset);
        }
    }

    private CertificationResponseDTO mapToResponse(Certifications c) {
        String credentialUrl = null;
        Optional<FileAsset> assetOpt = fileAssetDao.findByResourceIdAndResourceTypeAndIsPrimaryTrue(c.getId().intValue(), ResourceTypeEnum.CERTIFICATION);
        if (assetOpt.isPresent()) {
            credentialUrl = assetOpt.get().getPath();
        }
        CertificationResponseDTO responseDTO = CertificationResponseDTO.builder()
                .id(c.getId())
                .title(c.getTitle())
                .issuer(c.getIssuer())
                .credentialId(c.getCredentialId())
                .credentialUrl(credentialUrl)
                .status(c.getStatus())
                .order(c.getOrder())
                .issueDate(c.getIssueDate())
                .expiryDate(c.getExpiryDate())
                .build();
        helper.setAudit(c, responseDTO);
        return responseDTO;
    }
}

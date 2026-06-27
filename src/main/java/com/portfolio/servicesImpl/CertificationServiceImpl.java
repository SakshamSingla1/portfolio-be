package com.portfolio.servicesImpl;

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
import com.portfolio.repositories.CertificationsRepository;
import com.portfolio.repositories.FileAssetRepository;
import com.portfolio.repositories.ProfileRepository;
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
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final CertificationsRepository certificationsRepository;
    private final ProfileRepository profileRepository;
    private final FileService fileService;
    private final FileAssetRepository fileAssetRepository;
    private final Helper helper;

    @Override
    public CertificationResponseDTO createCertification(CertificationRequestDTO dto) throws GenericException {
        if (certificationsRepository.existsByProfileIdAndOrder(dto.getProfileId(), dto.getOrder())) {
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
        Certifications saved = certificationsRepository.save(certification);
        linkFileAsset(saved.getId(), dto.getCredentialUrl());
        return mapToResponse(saved);
    }
 
    @Override
    public CertificationResponseDTO updateCertification(Long id, CertificationRequestDTO dto) throws GenericException {
        Certifications existing = certificationsRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.CERTIFICATION_NOT_FOUND, "Certification not found"));
        if (dto.getOrder() != null && certificationsRepository.existsByProfileIdAndOrderAndIdNot(existing.getProfileId(), dto.getOrder(), existing.getId())) {
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
        Certifications saved = certificationsRepository.save(existing);
        linkFileAsset(id, dto.getCredentialUrl());
        return mapToResponse(saved);
    }
 
    @Override
    public CertificationResponseDTO getCertificationById(Long id) throws GenericException {
        Certifications certification = certificationsRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.CERTIFICATION_NOT_FOUND, "Certification not found"));
        return mapToResponse(certification);
    }
 
    @Override
    public Page<CertificationResponseDTO> getByProfile(Long profileId, String search, String sortDir, String sortBy, Pageable pageable) {
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
        Page<Certifications> page;
        if (profileId != null && search != null && !search.isBlank()) {
            page = certificationsRepository.findByProfileIdWithSearch(profileId, search, sortedPageable);
        } else if (profileId != null) {
            page = certificationsRepository.findByProfileId(profileId, sortedPageable);
        } else if (search != null && !search.isBlank()) {
            page = certificationsRepository.findBySearch(search, sortedPageable);
        } else {
            page = certificationsRepository.findAll(sortedPageable);
        }
        return page.map(this::mapToResponse);
    }
 
    @Override
    public Void deleteById(Long id) throws GenericException {
        if (!certificationsRepository.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.CERTIFICATION_NOT_FOUND, "Certification not found");
        }
        try {
            fileService.deleteByResource(String.valueOf(id), ResourceTypeEnum.CERTIFICATION.name());
        } catch (Exception ignored) {}
        certificationsRepository.deleteById(id);
        return null;
    }
 
    @Override
    public ImageUploadResponse uploadCredentialImage(
            Long profileId,
            MultipartFile file
    ) throws GenericException, IOException {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId("TEMP_" + profileId);
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
        return certificationsRepository
                .findByProfileIdAndStatusOrderByOrderAsc(profileId, StatusEnum.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
 
    private void linkFileAsset(Long resourceId, String url) {
        if (url == null || url.isBlank()) return;
        List<FileAsset> existing = fileAssetRepository.findByResourceIdAndResourceTypeOrderBySortOrderAsc(String.valueOf(resourceId), ResourceTypeEnum.CERTIFICATION);
        for (FileAsset asset : existing) {
            if (!url.equals(asset.getPath())) {
                try { fileService.delete(asset.getId()); } catch (Exception ignored) {}
            }
        }
        Optional<FileAsset> assetOpt = fileAssetRepository.findByPath(url);
        if (assetOpt.isPresent()) {
            FileAsset asset = assetOpt.get();
            asset.setResourceId(String.valueOf(resourceId));
            asset.setPrimary(true);
            fileAssetRepository.save(asset);
        } else {
            FileAsset asset = new FileAsset();
            asset.setResourceId(String.valueOf(resourceId));
            asset.setResourceType(ResourceTypeEnum.CERTIFICATION);
            asset.setPath(url);
            asset.setPrimary(true);
            fileAssetRepository.save(asset);
        }
    }
 
    private CertificationResponseDTO mapToResponse(Certifications c) {
        String credentialUrl = null;
        Optional<FileAsset> assetOpt = fileAssetRepository.findByResourceIdAndResourceTypeAndIsPrimaryTrue(String.valueOf(c.getId()), ResourceTypeEnum.CERTIFICATION);
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

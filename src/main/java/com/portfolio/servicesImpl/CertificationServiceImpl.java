package com.portfolio.servicesImpl;

import com.portfolio.dtos.Achievements.AchievementResponseDTO;
import com.portfolio.dtos.Certifications.CertificationRequestDTO;
import com.portfolio.dtos.Certifications.CertificationResponseDTO;
import com.portfolio.dtos.ImageUploadResponse;
import com.portfolio.entities.Certifications;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.CertificationsRepository;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.services.CertificationService;
import com.portfolio.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final CertificationsRepository certificationsRepository;
    private final ProfileRepository profileRepository;
    private final CloudinaryService cloudinaryService;

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
                .credentialUrl(dto.getCredentialUrl())
                .issueDate(dto.getIssueDate())
                .expiryDate(dto.getExpiryDate())
                .status(dto.getStatus())
                .order(dto.getOrder())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return mapToResponse(certificationsRepository.save(certification));
    }

    @Override
    public CertificationResponseDTO updateCertification(String id, CertificationRequestDTO dto) throws GenericException {
        Certifications existing = certificationsRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.CERTIFICATION_NOT_FOUND, "Certification not found"));
        if (dto.getOrder() != null && certificationsRepository.existsByProfileIdAndOrderAndIdNot(existing.getProfileId(), dto.getOrder(), existing.getId())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_CERTIFICATION, "Certification already exists with same order for the profile");
        }
        existing.setTitle(dto.getTitle());
        existing.setIssuer(dto.getIssuer());
        existing.setCredentialId(dto.getCredentialId());
        existing.setCredentialUrl(dto.getCredentialUrl());
        existing.setIssueDate(dto.getIssueDate());
        existing.setExpiryDate(dto.getExpiryDate());
        existing.setStatus(dto.getStatus());
        existing.setOrder(dto.getOrder());
        existing.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(certificationsRepository.save(existing));
    }

    @Override
    public CertificationResponseDTO getCertificationById(String id) throws GenericException {
        Certifications certification = certificationsRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.CERTIFICATION_NOT_FOUND, "Certification not found"));
        return mapToResponse(certification);
    }

    @Override
    public Page<CertificationResponseDTO> getByProfile(String profileId, String search, String sortDir, String sortBy, Pageable pageable) {
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
    public Void deleteById(String id) throws GenericException {
        if (!certificationsRepository.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.CERTIFICATION_NOT_FOUND, "Certification not found");
        }
        certificationsRepository.deleteById(id);
        return null;
    }

    @Override
    public ImageUploadResponse uploadCredentialImage(
            String profileId,
            MultipartFile file
    ) throws GenericException, IOException {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        Map uploadResult = cloudinaryService.uploadFile(file);
        return new ImageUploadResponse(uploadResult.get("secure_url").toString(), uploadResult.get("public_id").toString());
    }

    public List<CertificationResponseDTO> getByProfile(String profileId) {
        return certificationsRepository
                .findByProfileIdAndStatusOrderByOrderAsc(profileId, StatusEnum.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private CertificationResponseDTO mapToResponse(Certifications c) {
        return CertificationResponseDTO.builder()
                .id(c.getId())
                .title(c.getTitle())
                .issuer(c.getIssuer())
                .credentialId(c.getCredentialId())
                .credentialUrl(c.getCredentialUrl())
                .status(c.getStatus())
                .order(c.getOrder())
                .issueDate(c.getIssueDate())
                .expiryDate(c.getExpiryDate())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}

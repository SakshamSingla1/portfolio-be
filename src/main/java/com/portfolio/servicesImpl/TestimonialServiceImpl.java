package com.portfolio.servicesImpl;

import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.testimonial.TestimonialDao;
import com.portfolio.dtos.Testimonial.TestimonialRequestDTO;
import com.portfolio.dtos.Testimonial.TestimonialResponseDTO;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.entities.Testimonial;
import com.portfolio.entities.FileAsset;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.TestimonialService;
import com.portfolio.services.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestimonialServiceImpl implements TestimonialService {

    private final TestimonialDao testimonialDao;
    private final ProfileDao profileDao;
    private final FileService fileService;
    private final FileAssetDao fileAssetDao;

    @Transactional
    @Override
    public TestimonialResponseDTO createTestimonial(TestimonialRequestDTO dto) throws GenericException {
        if (testimonialDao.existsByProfileIdAndOrder(dto.getProfileId(), dto.getOrder() != null ? Integer.parseInt(dto.getOrder()) : null)) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_TESTIMONIAL, "Testimonial already exists with same order for the profile");
        }
        Testimonial testimonial = Testimonial.builder()
                .profileId(dto.getProfileId())
                .name(dto.getName())
                .role(dto.getRole())
                .company(dto.getCompany())
                .message(dto.getMessage())
                .linkedInUrl(dto.getLinkedInUrl())
                .status(dto.getStatus())
                .order(dto.getOrder() != null ? Integer.parseInt(dto.getOrder()) : null)
                .build();
        Testimonial saved = testimonialDao.save(testimonial);
        linkFileAsset(saved.getId(), dto.getImageUrl(), dto.getImageId());
        return mapToResponse(saved);
    }

    @Transactional
    @Override
    public TestimonialResponseDTO updateTestimonial(Long id, TestimonialRequestDTO dto) throws GenericException {
        Testimonial existing = testimonialDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TESTIMONIAL_NOT_FOUND, "Testimonial not found"));
        if (dto.getOrder() != null && testimonialDao.existsByProfileIdAndOrderAndIdNot(existing.getProfileId(), Integer.parseInt(dto.getOrder()), existing.getId())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_TESTIMONIAL, "Testimonial already exists with same order for the profile");
        }
        existing.setName(dto.getName());
        existing.setRole(dto.getRole());
        existing.setCompany(dto.getCompany());
        existing.setMessage(dto.getMessage());
        existing.setLinkedInUrl(dto.getLinkedInUrl());
        existing.setStatus(dto.getStatus());
        existing.setOrder(dto.getOrder() != null ? Integer.parseInt(dto.getOrder()) : null);
        existing.setUpdatedAt(LocalDateTime.now());
        Testimonial saved = testimonialDao.save(existing);
        linkFileAsset(id, dto.getImageUrl(), dto.getImageId());
        return mapToResponse(saved);
    }

    @Override
    public TestimonialResponseDTO getTestimonialById(Long id) throws GenericException {
        return testimonialDao.findDTOById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TESTIMONIAL_NOT_FOUND, "Testimonial not found"));
    }

    @Override
    public Page<TestimonialResponseDTO> getByProfile(Long profileId, StatusEnum status, String search, String sortDir, String sortBy, Pageable pageable) {
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
        return testimonialDao.findByCriteria(profileId, status, search, sortedPageable);
    }

    @Override
    public Void deleteById(Long id) throws GenericException {
        if (!testimonialDao.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.TESTIMONIAL_NOT_FOUND, "Testimonial not found");
        }
        try {
            fileService.deleteByResource(id, ResourceTypeEnum.TESTIMONIAL.name());
        } catch (Exception ignored) {
            // best-effort cleanup; failure is non-fatal
        }
        testimonialDao.deleteById(id);
        return null;
    }

    @Override
    public int bulkDeleteByIds(List<Long> ids) {
        int deleted = 0;
        for (Long id : ids) {
            try {
                deleteById(id);
                deleted++;
            } catch (GenericException e) {
                log.warn("Skipping bulk delete for testimonial {}: {}", id, e.getMessage());
            }
        }
        return deleted;
    }

    @Override
    public List<TestimonialResponseDTO> getByProfile(Long profileId) {
        List<Testimonial> testimonials = testimonialDao
                .findByProfileIdAndStatusOrderByOrderAsc(profileId, StatusEnum.ACTIVE);
        return mapToResponseBatch(testimonials);
    }

    private List<TestimonialResponseDTO> mapToResponseBatch(List<Testimonial> testimonials) {
        if (testimonials.isEmpty()) return List.of();

        List<Long> ids = testimonials.stream().map(Testimonial::getId).toList();
        Map<Long, FileAsset> imageByTestimonialId = fileAssetDao
                .findByResourceIdInAndResourceTypeAndIsPrimaryTrue(ids, ResourceTypeEnum.TESTIMONIAL)
                .stream()
                .collect(Collectors.toMap(FileAsset::getResourceId, a -> a, (a, b) -> a));

        return testimonials.stream().map(c -> {
            FileAsset asset = imageByTestimonialId.get(c.getId());
            return TestimonialResponseDTO.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .role(c.getRole())
                    .company(c.getCompany())
                    .message(c.getMessage())
                    .imageId(asset != null ? asset.getId() : null)
                    .imageUrl(asset != null ? asset.getPath() : null)
                    .linkedInUrl(c.getLinkedInUrl())
                    .status(c.getStatus())
                    .order(c.getOrder())
                    .createdAt(c.getCreatedAt())
                    .updatedAt(c.getUpdatedAt())
                    .build();
        }).toList();
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
        uploadReq.setResourceType(ResourceTypeEnum.TESTIMONIAL);
        uploadReq.setPrimary(true);
        try {
            FileAssetDTO asset = fileService.upload(file, uploadReq);
            return new ImageUploadResponse(asset.getPath(), asset.getPublicId());
        } catch (Exception e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Failed to upload testimonial image: " + e.getMessage());
        }
    }

    private void linkFileAsset(Long resourceId, String url, Long imageAssetId) {
        List<FileAsset> existing = fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(resourceId, ResourceTypeEnum.TESTIMONIAL);

        Long targetAssetId = imageAssetId;
        if (targetAssetId == null && url != null && !url.isBlank()) {
            targetAssetId = fileAssetDao.findByPath(url).map(FileAsset::getId).orElse(null);
        }

        for (FileAsset asset : existing) {
            if (targetAssetId == null || !targetAssetId.equals(asset.getId())) {
                try { fileService.delete(asset.getId()); } catch (Exception ignored) { /* best-effort cleanup */ }
            }
        }

        if (targetAssetId != null) {
            fileAssetDao.findById(targetAssetId)
                    .ifPresent(asset -> {
                        asset.setResourceId(resourceId);
                        asset.setPrimary(true);
                        fileAssetDao.save(asset);
                    });
        }
    }

    private TestimonialResponseDTO mapToResponse(Testimonial c) {
        String imageUrl = null;
        Long imageId = null;
        Optional<FileAsset> assetOpt = fileAssetDao.findByResourceIdAndResourceTypeAndIsPrimaryTrue(c.getId(), ResourceTypeEnum.TESTIMONIAL);
        if (assetOpt.isPresent()) {
            imageUrl = assetOpt.get().getPath();
            imageId = assetOpt.get().getId();
        }
        return TestimonialResponseDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .role(c.getRole())
                .company(c.getCompany())
                .message(c.getMessage())
                .imageId(imageId)
                .imageUrl(imageUrl)
                .linkedInUrl(c.getLinkedInUrl())
                .status(c.getStatus())
                .order(c.getOrder())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}

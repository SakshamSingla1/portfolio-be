package com.portfolio.servicesImpl;

import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dao.services.ServiceOfferingDao;
import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.dtos.Services.ServiceRequest;
import com.portfolio.dtos.Services.ServiceResponse;
import com.portfolio.entities.FileAsset;
import com.portfolio.entities.ServiceOffering;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.FileService;
import com.portfolio.services.ServiceOfferingService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceOfferingServiceImpl implements ServiceOfferingService {

    private final ServiceOfferingDao serviceOfferingDao;
    private final FileAssetDao fileAssetDao;
    private final FileService fileService;
    private final Helper helper;

    @Override
    @Transactional
    public ServiceResponse create(ServiceRequest req) throws GenericException {
        ServiceOffering entity = ServiceOffering.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .icon(req.getIcon())
                .priceRange(req.getPriceRange())
                .deliveryTime(req.getDeliveryTime())
                .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .build();
        ServiceOffering saved = serviceOfferingDao.save(entity);
        linkBanner(saved.getId(), req.getBannerPublicId(), req.getBannerUrl());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ServiceResponse update(Long id, ServiceRequest req) throws GenericException {
        ServiceOffering existing = serviceOfferingDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SERVICE_NOT_FOUND, "Service not found"));
        existing.setTitle(req.getTitle());
        existing.setDescription(req.getDescription());
        existing.setIcon(req.getIcon());
        existing.setPriceRange(req.getPriceRange());
        existing.setDeliveryTime(req.getDeliveryTime());
        if (req.getSortOrder() != null) existing.setSortOrder(req.getSortOrder());
        if (req.getIsActive() != null) existing.setIsActive(req.getIsActive());
        ServiceOffering saved = serviceOfferingDao.save(existing);
        linkBanner(id, req.getBannerPublicId(), req.getBannerUrl());
        return mapToResponse(saved);
    }

    @Override
    public ServiceResponse getById(Long id) throws GenericException {
        ServiceOffering entity = serviceOfferingDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SERVICE_NOT_FOUND, "Service not found"));
        return mapToResponse(entity);
    }

    @Override
    public Page<ServiceResponse> getAll(Long profileId, String search, Pageable pageable) {
        Page<ServiceOffering> page = (search != null && !search.isBlank())
                ? serviceOfferingDao.findByProfileIdAndTitleContainingIgnoreCase(profileId, search, pageable)
                : serviceOfferingDao.findByProfileId(profileId, pageable);
        return page.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public Void delete(Long id) throws GenericException {
        if (!serviceOfferingDao.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.SERVICE_NOT_FOUND, "Service not found");
        }
        try {
            fileService.deleteByResource(id, ResourceTypeEnum.SERVICE.name());
        } catch (Exception ignored) {}
        serviceOfferingDao.deleteById(id);
        return null;
    }

    @Override
    public ImageUploadResponse uploadBanner(Long profileId, MultipartFile file) throws GenericException, IOException {
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId(profileId);
        uploadReq.setResourceType(ResourceTypeEnum.SERVICE);
        uploadReq.setPrimary(true);
        try {
            FileAssetDTO asset = fileService.upload(file, uploadReq);
            return new ImageUploadResponse(asset.getPath(), asset.getPublicId());
        } catch (Exception e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Failed to upload service banner: " + e.getMessage());
        }
    }

    @Override
    public List<ServiceResponse> getByProfile(Long profileId) {
        return serviceOfferingDao
                .findByProfileIdAndIsActiveTrueOrderBySortOrderAsc(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void linkBanner(Long resourceId, String publicId, String url) {
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

        List<FileAsset> existing = fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(resourceId, ResourceTypeEnum.SERVICE);
        for (FileAsset asset : existing) {
            if (targetAssetId == null || !targetAssetId.equals(asset.getId())) {
                try { fileService.delete(asset.getId()); } catch (Exception ignored) {}
            }
        }

        if (assetOpt.isPresent()) {
            FileAsset asset = assetOpt.get();
            asset.setResourceId(resourceId);
            asset.setResourceType(ResourceTypeEnum.SERVICE);
            asset.setPrimary(true);
            fileAssetDao.save(asset);
        } else {
            FileAsset asset = new FileAsset();
            asset.setResourceId(resourceId);
            asset.setResourceType(ResourceTypeEnum.SERVICE);
            asset.setPath(url);
            asset.setPublicId(publicId);
            asset.setPrimary(true);
            fileAssetDao.save(asset);
        }
    }

    private ServiceResponse mapToResponse(ServiceOffering entity) {
        String bannerUrl = null;
        String bannerPublicId = null;
        Optional<FileAsset> assetOpt = fileAssetDao.findByResourceIdAndResourceTypeAndIsPrimaryTrue(entity.getId(), ResourceTypeEnum.SERVICE);
        if (assetOpt.isPresent()) {
            bannerUrl = assetOpt.get().getPath();
            bannerPublicId = assetOpt.get().getPublicId();
        }
        ServiceResponse response = ServiceResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .icon(entity.getIcon())
                .priceRange(entity.getPriceRange())
                .deliveryTime(entity.getDeliveryTime())
                .sortOrder(entity.getSortOrder())
                .isActive(entity.getIsActive())
                .bannerUrl(bannerUrl)
                .bannerPublicId(bannerPublicId)
                .build();
        helper.setAudit(entity, response);
        return response;
    }
}

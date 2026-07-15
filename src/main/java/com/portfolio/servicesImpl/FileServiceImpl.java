package com.portfolio.servicesImpl;

import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.entities.FileAsset;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.services.CloudinaryService;
import com.portfolio.services.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileAssetDao fileAssetDao;
    private final CloudinaryService cloudinaryService;

    private static final String FOLDER_PREFIX = "portfolio/assets/";

    @Override
    public FileAssetDTO upload(MultipartFile file, FileUploadRequest request) throws Exception {
        String folder = FOLDER_PREFIX + request.getResourceType().name().toLowerCase();
        String contentType = file.getContentType();
        Map<String, Object> result = (contentType != null && contentType.startsWith("image/"))
                ? cloudinaryService.uploadImage(file, folder)
                : cloudinaryService.uploadRawDocument(file, folder);

        // Delete existing primary asset (same resource) to avoid orphaned Cloudinary files
        if (request.isPrimary()) {
            fileAssetDao
                    .findByResourceIdAndResourceTypeAndIsPrimaryTrue(request.getResourceId(), request.getResourceType())
                    .ifPresent(existing -> {
                        try {
                            if (existing.getPublicId() != null && !existing.getPublicId().isBlank()) {
                                cloudinaryService.deleteFile(existing.getPublicId());
                            }
                            fileAssetDao.delete(existing);
                        } catch (Exception ignored) {
                            // best-effort cleanup; failure is non-fatal
                        }
                    });
        } else if (request.getMetaData() != null && !request.getMetaData().isBlank()) {
            // For non-primary assets tagged with metadata (e.g. ABOUT_ME_IMAGE), replace the existing tagged asset
            fileAssetDao
                    .findByResourceIdAndResourceTypeOrderBySortOrderAsc(request.getResourceId(), request.getResourceType())
                    .stream()
                    .filter(a -> request.getMetaData().equals(a.getMetaData()))
                    .forEach(existing -> {
                        try {
                            if (existing.getPublicId() != null && !existing.getPublicId().isBlank()) {
                                cloudinaryService.deleteFile(existing.getPublicId());
                            }
                            fileAssetDao.delete(existing);
                        } catch (Exception ignored) {
                            // best-effort cleanup; failure is non-fatal
                        }
                    });
        }

        FileAsset asset = new FileAsset();
        asset.setLocation(folder);
        asset.setPath((String) result.get("secure_url"));
        asset.setPublicId((String) result.get("public_id"));
        asset.setResourceId(request.getResourceId());
        asset.setResourceType(request.getResourceType());
        asset.setMimeType(file.getContentType());
        asset.setMetaData(request.getMetaData());
        asset.setPrimary(request.isPrimary());
        asset.setSortOrder(request.getSortOrder());
        asset.setPlatform(request.getPlatform());

        fileAssetDao.save(asset);
        return toDTO(asset);
    }

    @Override
    public List<FileAssetDTO> getByResource(Long resourceId, String resourceType) {
        ResourceTypeEnum type = ResourceTypeEnum.valueOf(resourceType.toUpperCase());
        return fileAssetDao
                .findByResourceIdAndResourceTypeOrderBySortOrderAsc(resourceId, type)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FileAssetDTO getById(Long id) throws Exception {
        return fileAssetDao.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.DATA_NOT_FOUND, "File not found"));
    }

    @Override
    public void delete(Long id) throws Exception {
        FileAsset asset = fileAssetDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.DATA_NOT_FOUND, "File not found"));
        if (asset.getPublicId() != null && !asset.getPublicId().isBlank()) {
            cloudinaryService.deleteFile(asset.getPublicId());
        }
        fileAssetDao.deleteById(id);
    }

    @Override
    public void deleteByResource(Long resourceId, String resourceType) throws Exception {
        ResourceTypeEnum type = ResourceTypeEnum.valueOf(resourceType.toUpperCase());
        List<FileAsset> assets = fileAssetDao
                .findByResourceIdAndResourceTypeOrderBySortOrderAsc(resourceId, type);
        for (FileAsset asset : assets) {
            if (asset.getPublicId() != null && !asset.getPublicId().isBlank()) {
                try { cloudinaryService.deleteFile(asset.getPublicId()); } catch (Exception ignored) { /* best-effort cleanup */ }
            }
        }
        fileAssetDao.deleteByResourceIdAndResourceType(resourceId, type);
    }

    @Override
    public FileAssetDTO getPrimaryFile(Long resourceId, ResourceTypeEnum resourceType) {
        return fileAssetDao.findByResourceIdAndResourceTypeAndIsPrimaryTrue(resourceId, resourceType)
                .map(this::toDTO)
                .orElse(null);
    }

    @Override
    public List<FileAssetDTO> getFiles(Long resourceId, ResourceTypeEnum resourceType) {
        return fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(resourceId, resourceType)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, FileAssetDTO> getPrimaryFilesForResources(List<Long> resourceIds, ResourceTypeEnum resourceType) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return Map.of();
        }
        return fileAssetDao.findByResourceIdInAndResourceTypeAndIsPrimaryTrue(resourceIds, resourceType)
                .stream()
                .collect(Collectors.toMap(
                        FileAsset::getResourceId,
                        this::toDTO,
                        (existing, replacement) -> existing
                ));
    }

    @Override
    public Map<Long, List<FileAssetDTO>> getFilesForResources(List<Long> resourceIds, ResourceTypeEnum resourceType) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return Map.of();
        }
        return fileAssetDao.findByResourceIdInAndResourceType(resourceIds, resourceType)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.groupingBy(FileAssetDTO::getResourceId));
    }

    private FileAssetDTO toDTO(FileAsset asset) {
        return FileAssetDTO.builder()
                .id(asset.getId())
                .location(asset.getLocation())
                .path(asset.getPath())
                .publicId(asset.getPublicId())
                .resourceId(asset.getResourceId())
                .resourceType(asset.getResourceType())
                .mimeType(asset.getMimeType())
                .metaData(asset.getMetaData())
                .createdAt(asset.getCreatedAt())
                .validityFrom(asset.getValidityFrom())
                .validityTo(asset.getValidityTo())
                .platform(asset.getPlatform())
                .createdBy(asset.getCreatedBy())
                .creatorName(asset.getCreatorName())
                .isPrimary(asset.isPrimary())
                .sortOrder(asset.getSortOrder())
                .build();
    }
}

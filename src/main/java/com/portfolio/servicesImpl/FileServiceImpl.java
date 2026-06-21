package com.portfolio.servicesImpl;

import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.entities.FileAsset;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.repositories.FileAssetRepository;
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

    private final FileAssetRepository fileAssetRepository;
    private final CloudinaryService cloudinaryService;

    private static final String FOLDER_PREFIX = "portfolio/assets/";

    @Override
    public FileAssetDTO upload(MultipartFile file, FileUploadRequest request) throws Exception {
        String folder = FOLDER_PREFIX + request.getResourceType().name().toLowerCase();
        Map<String, Object> result = cloudinaryService.uploadImage(file, folder);

        // If this asset is primary, demote any existing primary for the same resource
        if (request.isPrimary()) {
            fileAssetRepository
                    .findByResourceIdAndResourceTypeAndIsPrimaryTrue(String.valueOf(request.getResourceId()), request.getResourceType())
                    .ifPresent(existing -> {
                        existing.setPrimary(false);
                        fileAssetRepository.save(existing);
                    });
        }

        FileAsset asset = new FileAsset();
        asset.setLocation(folder);
        asset.setPath((String) result.get("secure_url"));
        asset.setPublicId((String) result.get("public_id"));
        asset.setResourceId(String.valueOf(request.getResourceId()));
        asset.setResourceType(request.getResourceType());
        asset.setMimeType(file.getContentType());
        asset.setMetaData(request.getMetaData());
        asset.setPrimary(request.isPrimary());
        asset.setSortOrder(request.getSortOrder());
        asset.setPlatform(request.getPlatform());

        fileAssetRepository.save(asset);
        return toDTO(asset);
    }

    @Override
    public List<FileAssetDTO> getByResource(Long resourceId, String resourceType) {
        ResourceTypeEnum type = ResourceTypeEnum.valueOf(resourceType.toUpperCase());
        return fileAssetRepository
                .findByResourceIdAndResourceTypeOrderBySortOrderAsc(String.valueOf(resourceId), type)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FileAssetDTO getById(Long id) throws Exception {
        return fileAssetRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.DATA_NOT_FOUND, "File not found"));
    }

    @Override
    public void delete(Long id) throws Exception {
        FileAsset asset = fileAssetRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.DATA_NOT_FOUND, "File not found"));
        if (asset.getPublicId() != null && !asset.getPublicId().isBlank()) {
            cloudinaryService.deleteFile(asset.getPublicId());
        }
        fileAssetRepository.deleteById(id);
    }

    @Override
    public void deleteByResource(Long resourceId, String resourceType) throws Exception {
        ResourceTypeEnum type = ResourceTypeEnum.valueOf(resourceType.toUpperCase());
        List<FileAsset> assets = fileAssetRepository
                .findByResourceIdAndResourceTypeOrderBySortOrderAsc(String.valueOf(resourceId), type);
        for (FileAsset asset : assets) {
            if (asset.getPublicId() != null && !asset.getPublicId().isBlank()) {
                try { cloudinaryService.deleteFile(asset.getPublicId()); } catch (Exception ignored) {}
            }
        }
        fileAssetRepository.deleteByResourceIdAndResourceType(String.valueOf(resourceId), type);
    }

    private FileAssetDTO toDTO(FileAsset asset) {
        return FileAssetDTO.builder()
                .id(asset.getId())
                .location(asset.getLocation())
                .path(asset.getPath())
                .resourceId(asset.getResourceId() == null ? null : Long.valueOf(asset.getResourceId()))
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

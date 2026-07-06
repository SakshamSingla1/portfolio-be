package com.portfolio.dao.file;

import com.portfolio.entities.FileAsset;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.repositories.FileAssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class FileAssetDao {

    private final FileAssetRepository fileAssetRepository;

    public FileAssetDao(FileAssetRepository fileAssetRepository) {
        this.fileAssetRepository = fileAssetRepository;
    }

    public FileAsset save(FileAsset fileAsset) {
        return fileAssetRepository.save(fileAsset);
    }

    public Optional<FileAsset> findById(Long id) {
        return fileAssetRepository.findById(id);
    }

    public void deleteById(Long id) {
        fileAssetRepository.deleteById(id);
    }

    public List<FileAsset> findByResourceIdAndResourceTypeOrderBySortOrderAsc(Long resourceId, ResourceTypeEnum resourceType) {
        return fileAssetRepository.findByResourceIdAndResourceTypeOrderBySortOrderAsc(resourceId, resourceType);
    }

    public Optional<FileAsset> findByResourceIdAndResourceTypeAndIsPrimaryTrue(Long resourceId, ResourceTypeEnum resourceType) {
        return fileAssetRepository.findByResourceIdAndResourceTypeAndIsPrimaryTrue(resourceId, resourceType);
    }

    @Transactional
    public void deleteByResourceIdAndResourceType(Long resourceId, ResourceTypeEnum resourceType) {
        fileAssetRepository.deleteByResourceIdAndResourceType(resourceId, resourceType);
    }

    public Optional<FileAsset> findByPublicId(String publicId) {
        return fileAssetRepository.findByPublicId(publicId);
    }

    public Optional<FileAsset> findByPath(String path) {
        return fileAssetRepository.findByPath(path);
    }

    public List<FileAsset> findByResourceIdInAndResourceType(List<Long> resourceIds, ResourceTypeEnum resourceType) {
        return fileAssetRepository.findByResourceIdInAndResourceType(resourceIds, resourceType);
    }

    public List<FileAsset> findByResourceIdInAndResourceTypeAndIsPrimaryTrue(List<Long> resourceIds, ResourceTypeEnum resourceType) {
        return fileAssetRepository.findByResourceIdInAndResourceTypeAndIsPrimaryTrue(resourceIds, resourceType);
    }

    public void delete(FileAsset fileAsset) {
        fileAssetRepository.delete(fileAsset);
    }
}

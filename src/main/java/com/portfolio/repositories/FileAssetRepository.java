package com.portfolio.repositories;

import com.portfolio.entities.FileAsset;
import com.portfolio.enums.ResourceTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileAssetRepository extends JpaRepository<FileAsset, Long> {

    List<FileAsset> findByResourceIdAndResourceTypeOrderBySortOrderAsc(Long resourceId, ResourceTypeEnum resourceType);

    Optional<FileAsset> findByResourceIdAndResourceTypeAndIsPrimaryTrue(Long resourceId, ResourceTypeEnum resourceType);

    void deleteByResourceIdAndResourceType(Long resourceId, ResourceTypeEnum resourceType);

    Optional<FileAsset> findByPublicId(String publicId);

    Optional<FileAsset> findByPath(String path);

    List<FileAsset> findByResourceIdInAndResourceType(List<Long> resourceIds, ResourceTypeEnum resourceType);

    List<FileAsset> findByResourceIdInAndResourceTypeAndIsPrimaryTrue(List<Long> resourceIds, ResourceTypeEnum resourceType);
}

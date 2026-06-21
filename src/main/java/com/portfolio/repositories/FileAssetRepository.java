package com.portfolio.repositories;

import com.portfolio.entities.FileAsset;
import com.portfolio.enums.ResourceTypeEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileAssetRepository extends MongoRepository<FileAsset, String> {

    List<FileAsset> findByResourceIdAndResourceTypeOrderBySortOrderAsc(String resourceId, ResourceTypeEnum resourceType);

    Optional<FileAsset> findByResourceIdAndResourceTypeAndIsPrimaryTrue(String resourceId, ResourceTypeEnum resourceType);

    void deleteByResourceIdAndResourceType(String resourceId, ResourceTypeEnum resourceType);
}

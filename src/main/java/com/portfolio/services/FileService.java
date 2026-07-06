package com.portfolio.services;

import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.enums.ResourceTypeEnum;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {

    FileAssetDTO upload(MultipartFile file, FileUploadRequest request) throws Exception;

    List<FileAssetDTO> getByResource(Long resourceId, String resourceType);

    FileAssetDTO getById(Long id) throws Exception;

    void delete(Long id) throws Exception;

    void deleteByResource(Long resourceId, String resourceType) throws Exception;

    FileAssetDTO getPrimaryFile(Long resourceId, ResourceTypeEnum resourceType);

    List<FileAssetDTO> getFiles(Long resourceId, ResourceTypeEnum resourceType);

    Map<Long, FileAssetDTO> getPrimaryFilesForResources(List<Long> resourceIds, ResourceTypeEnum resourceType);

    Map<Long, List<FileAssetDTO>> getFilesForResources(List<Long> resourceIds, ResourceTypeEnum resourceType);
}

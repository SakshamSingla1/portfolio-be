package com.portfolio.services;

import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.enums.ResourceTypeEnum;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {

    FileAssetDTO upload(MultipartFile file, FileUploadRequest request) throws Exception;

    List<FileAssetDTO> getByResource(String resourceId, String resourceType);

    FileAssetDTO getById(Long id) throws Exception;

    void delete(Long id) throws Exception;

    void deleteByResource(String resourceId, String resourceType) throws Exception;

    FileAssetDTO getPrimaryFile(String resourceId, ResourceTypeEnum resourceType);

    List<FileAssetDTO> getFiles(String resourceId, ResourceTypeEnum resourceType);

    Map<String, FileAssetDTO> getPrimaryFilesForResources(List<String> resourceIds, ResourceTypeEnum resourceType);

    Map<String, List<FileAssetDTO>> getFilesForResources(List<String> resourceIds, ResourceTypeEnum resourceType);
}

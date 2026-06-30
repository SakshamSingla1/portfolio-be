package com.portfolio.services;

import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.enums.ResourceTypeEnum;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {

    FileAssetDTO upload(MultipartFile file, FileUploadRequest request) throws Exception;

    List<FileAssetDTO> getByResource(Integer resourceId, String resourceType);

    FileAssetDTO getById(Long id) throws Exception;

    void delete(Long id) throws Exception;

    void deleteByResource(Integer resourceId, String resourceType) throws Exception;

    FileAssetDTO getPrimaryFile(Integer resourceId, ResourceTypeEnum resourceType);

    List<FileAssetDTO> getFiles(Integer resourceId, ResourceTypeEnum resourceType);

    Map<Integer, FileAssetDTO> getPrimaryFilesForResources(List<Integer> resourceIds, ResourceTypeEnum resourceType);

    Map<Integer, List<FileAssetDTO>> getFilesForResources(List<Integer> resourceIds, ResourceTypeEnum resourceType);
}

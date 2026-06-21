package com.portfolio.services;

import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.File.FileUploadRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    FileAssetDTO upload(MultipartFile file, FileUploadRequest request) throws Exception;

    List<FileAssetDTO> getByResource(String resourceId, String resourceType);

    FileAssetDTO getById(String id) throws Exception;

    void delete(String id) throws Exception;

    void deleteByResource(String resourceId, String resourceType) throws Exception;
}

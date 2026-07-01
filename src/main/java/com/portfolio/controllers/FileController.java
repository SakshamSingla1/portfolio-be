package com.portfolio.controllers;

import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "Upload a file and link it to a resource", description = "Uploads a file and links it to a resource (project, achievement, etc.) by resource ID and type. Supports metadata, sort order, and platform context.")
    @PostMapping("/upload")
    public ResponseEntity<ResponseModel<FileAssetDTO>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("resourceId") Integer resourceId,
            @RequestParam("resourceType") ResourceTypeEnum resourceType,
            @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary,
            @RequestParam(value = "sortOrder", defaultValue = "0") int sortOrder,
            @RequestParam(value = "platform", required = false) String platform,
            @RequestParam(value = "metaData", required = false) String metaData
    ) throws Exception {
        FileUploadRequest request = new FileUploadRequest();
        request.setResourceId(resourceId);
        request.setResourceType(resourceType);
        request.setPrimary(isPrimary);
        request.setSortOrder(sortOrder);
        request.setPlatform(platform);
        request.setMetaData(metaData);

        FileAssetDTO result = fileService.upload(file, request);
        return ApiResponse.respond(result, "File uploaded successfully", "Failed to upload file");
    }

    @Operation(summary = "Get all files for a resource", description = "Returns all file assets associated with a specific resource type and ID.")
    @GetMapping("/{resourceType}/{resourceId}")
    public ResponseEntity<ResponseModel<List<FileAssetDTO>>> getByResource(
            @PathVariable String resourceType,
            @PathVariable Integer resourceId
    ) {
        List<FileAssetDTO> result = fileService.getByResource(resourceId, resourceType);
        return ApiResponse.respond(result, "Files fetched successfully", "Failed to fetch files");
    }

    @Operation(summary = "Get a file by ID", description = "Fetches a single file asset record by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<FileAssetDTO>> getById(@PathVariable Long id) throws Exception {
        return ApiResponse.respond(fileService.getById(id), "File fetched successfully", "Failed to fetch file");
    }

    @Operation(summary = "Delete a file by ID", description = "Deletes a single file asset and its underlying stored file by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> delete(@PathVariable Long id) throws Exception {
        fileService.delete(id);
        return ApiResponse.respond(null, "File deleted successfully", "Failed to delete file");
    }

    @Operation(summary = "Delete all files for a resource", description = "Deletes all file assets associated with a specific resource type and ID.")
    @DeleteMapping("/{resourceType}/{resourceId}")
    public ResponseEntity<ResponseModel<Void>> deleteByResource(
            @PathVariable String resourceType,
            @PathVariable Integer resourceId
    ) throws Exception {
        fileService.deleteByResource(resourceId, resourceType);
        return ApiResponse.respond(null, "Files deleted successfully", "Failed to delete files");
    }
}

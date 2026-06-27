package com.portfolio.servicesImpl;

import com.portfolio.dtos.Logos.LogoRequest;
import com.portfolio.dtos.Logos.LogoResponse;
import com.portfolio.dtos.Logos.LogoDropdown;
import com.portfolio.entities.Logo;
import com.portfolio.entities.FileAsset;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.LogoRepository;
import com.portfolio.repositories.FileAssetRepository;
import com.portfolio.services.FileService;
import com.portfolio.services.LogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogoServiceImpl implements LogoService {

    private final LogoRepository logoRepository;
    private final FileService fileService;
    private final FileAssetRepository fileAssetRepository;

    // ================= CREATE =================
    @Override
    public LogoResponse create(LogoRequest request) throws GenericException {
 
        if (logoRepository.existsByName(request.getName())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_LOGO,"Logo with this name already exists");
        }
 
        Logo logo = Logo.builder()
                .name(request.getName())
                .build();
 
        Logo savedLogo = logoRepository.save(logo);
        linkFileAsset(savedLogo.getId(), request.getUrl());
        savedLogo.setUrl(request.getUrl());
        return mapToResponse(savedLogo);
    }
 
    // ================= UPDATE =================
    @Override
    public LogoResponse update(Long id, LogoRequest request) throws GenericException {
 
        Logo existingLogo = logoRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LOGO_NOT_FOUND,"Logo not found"));
 
        if (!existingLogo.getName().equalsIgnoreCase(request.getName()) && logoRepository.existsByName(request.getName())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_LOGO,"Logo with this name already exists");
        }
        existingLogo.setName(request.getName());
        existingLogo.setUpdatedAt(LocalDateTime.now());
        Logo updatedLogo = logoRepository.save(existingLogo);
        linkFileAsset(id, request.getUrl());
        updatedLogo.setUrl(request.getUrl());
        return mapToResponse(updatedLogo);
    }
 
    @Override
    public LogoResponse getById(Long id) throws GenericException {
        Logo logo = logoRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LOGO_NOT_FOUND,"Logo not found"));
        return mapToResponse(logo);
    }
 
    public Page<LogoDropdown> getAllLogosByPage(Pageable pageable, String search, String sortDir, String sortBy) {
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt"
        );
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        Page<Logo> logos = (search != null && !search.isBlank())
                ? logoRepository.findByNameWithSearch(search, sortedPageable)
                : logoRepository.findAll(sortedPageable);
        return logos.map(this::mapToDropdown);
    }
 
    @Override
    public void delete(Long id) throws GenericException {
        Logo logo = logoRepository.findById(id)
                .orElseThrow(() -> new GenericException(
                        ExceptionCodeEnum.LOGO_NOT_FOUND, "Logo not found"));
        try {
            fileService.deleteByResource(String.valueOf(id), ResourceTypeEnum.LOGO.name());
        } catch (Exception ignored) {}
        logoRepository.delete(logo);
    }
 
    private void linkFileAsset(Long resourceId, String url) {
        if (url == null || url.isBlank()) return;
        List<FileAsset> existing = fileAssetRepository.findByResourceIdAndResourceTypeOrderBySortOrderAsc(String.valueOf(resourceId), ResourceTypeEnum.LOGO);
        for (FileAsset asset : existing) {
            if (!url.equals(asset.getPath())) {
                try { fileService.delete(asset.getId()); } catch (Exception ignored) {}
            }
        }
        Optional<FileAsset> assetOpt = fileAssetRepository.findByPath(url);
        if (assetOpt.isPresent()) {
            FileAsset asset = assetOpt.get();
            asset.setResourceId(String.valueOf(resourceId));
            asset.setPrimary(true);
            fileAssetRepository.save(asset);
        } else {
            FileAsset asset = new FileAsset();
            asset.setResourceId(String.valueOf(resourceId));
            asset.setResourceType(ResourceTypeEnum.LOGO);
            asset.setPath(url);
            asset.setPrimary(true);
            fileAssetRepository.save(asset);
        }
    }
 
    private String getUrlForLogo(Long logoId) {
        return fileAssetRepository.findByResourceIdAndResourceTypeAndIsPrimaryTrue(String.valueOf(logoId), ResourceTypeEnum.LOGO)
                .map(FileAsset::getPath)
                .orElse(null);
    }
 
    private LogoResponse mapToResponse(Logo logo) {
        return LogoResponse.builder()
                .id(logo.getId())
                .name(logo.getName())
                .url(getUrlForLogo(logo.getId()))
                .createdAt(logo.getCreatedAt())
                .updatedAt(logo.getUpdatedAt())
                .build();
    }
 
    private LogoDropdown mapToDropdown(Logo logo) {
        return LogoDropdown.builder()
                .id(logo.getId())
                .name(logo.getName())
                .url(getUrlForLogo(logo.getId()))
                .createdAt(logo.getCreatedAt())
                .updatedAt(logo.getUpdatedAt())
                .build();
    }
}

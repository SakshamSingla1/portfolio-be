package com.portfolio.servicesImpl;

import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dao.logo.LogoDao;
import com.portfolio.dtos.Logos.LogoRequest;
import com.portfolio.dtos.Logos.LogoResponse;
import com.portfolio.dtos.Logos.LogoDropdown;
import com.portfolio.entities.Logo;
import com.portfolio.entities.FileAsset;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.exceptions.GenericException;
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

    private final LogoDao logoDao;
    private final FileService fileService;
    private final FileAssetDao fileAssetDao;

    // ================= CREATE =================
    @Override
    public LogoResponse create(LogoRequest request) throws GenericException {

        if (logoDao.existsByName(request.getName())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_LOGO,"Logo with this name already exists");
        }

        Logo logo = Logo.builder()
                .name(request.getName())
                .build();

        Logo savedLogo = logoDao.save(logo);
        linkFileAsset(savedLogo.getId(), request.getUrl());
        savedLogo.setUrl(request.getUrl());
        return mapToResponse(savedLogo);
    }

    // ================= UPDATE =================
    @Override
    public LogoResponse update(Long id, LogoRequest request) throws GenericException {

        Logo existingLogo = logoDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LOGO_NOT_FOUND,"Logo not found"));

        if (!existingLogo.getName().equalsIgnoreCase(request.getName()) && logoDao.existsByName(request.getName())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_LOGO,"Logo with this name already exists");
        }
        existingLogo.setName(request.getName());
        existingLogo.setUpdatedAt(LocalDateTime.now());
        Logo updatedLogo = logoDao.save(existingLogo);
        linkFileAsset(id, request.getUrl());
        updatedLogo.setUrl(request.getUrl());
        return mapToResponse(updatedLogo);
    }

    @Override
    public LogoResponse getById(Long id) throws GenericException {
        return logoDao.findDTOById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LOGO_NOT_FOUND, "Logo not found"));
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
        return logoDao.findByCriteria(search, sortedPageable);
    }

    @Override
    public void delete(Long id) throws GenericException {
        Logo logo = logoDao.findById(id)
                .orElseThrow(() -> new GenericException(
                        ExceptionCodeEnum.LOGO_NOT_FOUND, "Logo not found"));
        try {
            fileService.deleteByResource(id, ResourceTypeEnum.LOGO.name());
        } catch (Exception ignored) {}
        logoDao.delete(logo);
    }

    private void linkFileAsset(Long resourceId, String url) {
        if (url == null || url.isBlank()) return;
        List<FileAsset> existing = fileAssetDao.findByResourceIdAndResourceTypeOrderBySortOrderAsc(resourceId, ResourceTypeEnum.LOGO);
        for (FileAsset asset : existing) {
            if (!url.equals(asset.getPath())) {
                try { fileService.delete(asset.getId()); } catch (Exception ignored) {}
            }
        }
        Optional<FileAsset> assetOpt = fileAssetDao.findByPath(url);
        if (assetOpt.isPresent()) {
            FileAsset asset = assetOpt.get();
            asset.setResourceId(resourceId);
            asset.setPrimary(true);
            fileAssetDao.save(asset);
        } else {
            FileAsset asset = new FileAsset();
            asset.setResourceId(resourceId);
            asset.setResourceType(ResourceTypeEnum.LOGO);
            asset.setPath(url);
            asset.setPrimary(true);
            fileAssetDao.save(asset);
        }
    }

    private String getUrlForLogo(Long logoId) {
        return fileAssetDao.findByResourceIdAndResourceTypeAndIsPrimaryTrue(logoId, ResourceTypeEnum.LOGO)
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

}

package com.portfolio.servicesImpl;

import com.portfolio.dtos.LogoRequest;
import com.portfolio.dtos.LogoResponse;
import com.portfolio.dtos.logo.LogoDropdown;
import com.portfolio.entities.Logo;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.LogoRepository;
import com.portfolio.services.LogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogoServiceImpl implements LogoService {

    private final LogoRepository logoRepository;

    // ================= CREATE =================
    @Override
    public LogoResponse create(LogoRequest request) throws GenericException {

        if (logoRepository.existsByName(request.getName())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_LOGO,"Logo with this name already exists");
        }

        Logo logo = Logo.builder()
                .name(request.getName())
                .url(request.getUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Logo savedLogo = logoRepository.save(logo);
        return mapToResponse(savedLogo);
    }

    // ================= UPDATE =================
    @Override
    public LogoResponse update(String id, LogoRequest request) throws GenericException {

        Logo existingLogo = logoRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LOGO_NOT_FOUND,"Logo not found"));

        if (!existingLogo.getName().equalsIgnoreCase(request.getName()) && logoRepository.existsByName(request.getName())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_LOGO,"Logo with this name already exists");
        }
        existingLogo.setName(request.getName());
        existingLogo.setUrl(request.getUrl());
        existingLogo.setUpdatedAt(LocalDateTime.now());
        Logo updatedLogo = logoRepository.save(existingLogo);
        return mapToResponse(updatedLogo);
    }

    @Override
    public LogoResponse getById(String id) throws GenericException {
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
    public void delete(String id) throws GenericException {
        Logo logo = logoRepository.findById(id)
                .orElseThrow(() -> new GenericException(
                        ExceptionCodeEnum.LOGO_NOT_FOUND, "Logo not found"));
        logoRepository.delete(logo);
    }


    private LogoResponse mapToResponse(Logo logo) {
        return LogoResponse.builder()
                .id(logo.getId())
                .name(logo.getName())
                .url(logo.getUrl())
                .createdAt(logo.getCreatedAt())
                .updatedAt(logo.getUpdatedAt())
                .build();
    }

    private LogoDropdown mapToDropdown(Logo logo) {
        return LogoDropdown.builder()
                .id(logo.getId())
                .name(logo.getName())
                .url(logo.getUrl())
                .createdAt(logo.getCreatedAt())
                .updatedAt(logo.getUpdatedAt())
                .build();
    }
}

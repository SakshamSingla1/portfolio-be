package com.portfolio.services;

import com.portfolio.dtos.Logos.LogoRequest;
import com.portfolio.dtos.Logos.LogoResponse;
import com.portfolio.dtos.Logos.LogoDropdown;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LogoService {
    LogoResponse create(LogoRequest request) throws GenericException;
    LogoResponse update(Long id,LogoRequest request) throws GenericException;
    LogoResponse getById(Long id) throws GenericException;
    Page<LogoDropdown> getAllLogosByPage(Pageable pageable, String search, String sortDir, String sortBy);
    void delete(Long id) throws GenericException;
}

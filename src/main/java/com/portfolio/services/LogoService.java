package com.portfolio.services;

import com.portfolio.dtos.LogoRequest;
import com.portfolio.dtos.LogoResponse;
import com.portfolio.dtos.logo.LogoDropdown;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LogoService {
    LogoResponse create(LogoRequest request) throws GenericException;
    LogoResponse update(String id,LogoRequest request) throws GenericException;
    LogoResponse getById(String id) throws GenericException;
    Page<LogoDropdown> getAllLogosByPage(Pageable pageable, String search, String sortDir, String sortBy);
    void delete(String id) throws GenericException;
}

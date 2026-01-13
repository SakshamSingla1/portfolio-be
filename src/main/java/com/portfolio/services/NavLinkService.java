package com.portfolio.services;

import com.portfolio.dtos.NavLinks.NavLinkRequestDTO;
import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NavLinkService {
    NavLinkResponseDTO createNavLink(NavLinkRequestDTO request) throws GenericException;
    NavLinkResponseDTO updateNavLink(String index, NavLinkRequestDTO request) throws GenericException;
    void deleteNavLink(String index) throws GenericException;
    Page<NavLinkResponseDTO> getAllNavLinks(Pageable pageable, String search, StatusEnum status, String sortBy, String sortDir);
    NavLinkResponseDTO getNavLink(String index) throws GenericException;
}
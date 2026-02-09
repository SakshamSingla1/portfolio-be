package com.portfolio.services;

import com.portfolio.dtos.NavLinks.NavLinkRequestDTO;
import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NavLinkService {
    NavLinkResponseDTO createNavLink(NavLinkRequestDTO request) throws GenericException;
    NavLinkResponseDTO updateNavLink(String id, NavLinkRequestDTO request) throws GenericException;
    void deleteNavLink(String id) throws GenericException;
    List<NavLinkResponseDTO> getNavLinks(String role);
    Page<NavLinkResponseDTO> getAllNavLinks(
            Pageable pageable,
            String role,
            String search,
            StatusEnum status,
            String sortBy,
            String sortDir
    );
    NavLinkResponseDTO getNavLink(String id) throws GenericException;
}

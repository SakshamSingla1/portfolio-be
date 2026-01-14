package com.portfolio.services;

import com.portfolio.dtos.NavLinks.NavLinkRequestDTO;
import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.enums.RoleEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NavLinkService {
    NavLinkResponseDTO createNavLink(NavLinkRequestDTO request) throws GenericException;
    NavLinkResponseDTO updateNavLink(String roleIndex, RoleEnum role, NavLinkRequestDTO request) throws GenericException;
    void deleteNavLink(String roleIndex, RoleEnum role) throws GenericException;
    List<NavLinkResponseDTO> getNavLinks(RoleEnum role);
    Page<NavLinkResponseDTO> getAllNavLinks(Pageable pageable, RoleEnum role, String search, StatusEnum status, String sortBy, String sortDir);
    NavLinkResponseDTO getNavLink(RoleEnum role, String roleIndex) throws GenericException;
}


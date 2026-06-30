package com.portfolio.services;

import com.portfolio.dtos.NavLinks.NavLinkRequestDTO;
import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.dtos.NavLinks.GroupedNavLinkResponseDTO;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NavLinkService {
    NavLinkResponseDTO createNavLink(NavLinkRequestDTO request) throws GenericException;
    NavLinkResponseDTO updateNavLink(Long id, NavLinkRequestDTO request) throws GenericException;
    void deleteNavLink(Long id) throws GenericException;
    List<NavLinkResponseDTO> getNavLinks();
    Page<NavLinkResponseDTO> getAllNavLinks(
            Pageable pageable,
            String search,
            StatusEnum status
    );
    NavLinkResponseDTO getNavLink(Long id) throws GenericException;
    List<GroupedNavLinkResponseDTO> getGroupedNavLinks();
}

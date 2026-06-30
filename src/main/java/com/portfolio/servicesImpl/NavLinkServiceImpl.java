package com.portfolio.servicesImpl;

import com.portfolio.dao.nav_link.NavLinkDao;
import com.portfolio.dtos.NavLinks.NavLinkRequestDTO;
import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.dtos.NavLinks.GroupedNavLinkResponseDTO;
import com.portfolio.entities.NavLink;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.RoleEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.NavLinkService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NavLinkServiceImpl implements NavLinkService {

    private final NavLinkDao navLinkDao;
    private final Helper helper;

    @Override
    public NavLinkResponseDTO createNavLink(NavLinkRequestDTO request) throws GenericException {

        if (request == null) {
            throw new GenericException(
                    ExceptionCodeEnum.NAV_LINK_NOT_FOUND,
                    "Request cannot be null"
            );
        }
                NavLink navLink = NavLink.builder()
                .index(request.getIndex())
                .name(request.getName())
                .path(request.getPath())
                .icon(request.getIcon())
                .navGroup(request.getNavGroup())
                .status(request.getStatus() != null ? request.getStatus() : StatusEnum.ACTIVE)
                .build();

        navLinkDao.save(navLink);
        return toResponseDTO(navLink);
    }

    @Override
    public NavLinkResponseDTO updateNavLink(Long id, NavLinkRequestDTO request)
            throws GenericException {

        NavLink existing = navLinkDao.findById(id)
                .orElseThrow(() -> new GenericException(
                        ExceptionCodeEnum.NAV_LINK_NOT_FOUND,
                        "Nav Link not found"
                ));

        existing.setName(request.getName());
        existing.setIndex(request.getIndex());
        existing.setPath(request.getPath());
        existing.setIcon(request.getIcon());
        existing.setNavGroup(request.getNavGroup());
        existing.setStatus(request.getStatus());

        navLinkDao.save(existing);
        return toResponseDTO(existing);
    }

    @Override
    public void deleteNavLink(Long id) throws GenericException {

        NavLink navLink = navLinkDao.findById(id)
                .orElseThrow(() -> new GenericException(
                        ExceptionCodeEnum.NAV_LINK_NOT_FOUND,
                        "Nav Link not found"
                ));

        navLink.setStatus(StatusEnum.INACTIVE);
        navLinkDao.save(navLink);
    }

    @Override
    public List<NavLinkResponseDTO> getNavLinks() {

        return navLinkDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<NavLinkResponseDTO> getAllNavLinks(
            Pageable pageable,
            String search,
            StatusEnum status){
                return navLinkDao.findByCriteria(search, status, pageable);
            }

    @Override
    public NavLinkResponseDTO getNavLink(Long id) throws GenericException {
        return navLinkDao.findDTOById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.NAV_LINK_NOT_FOUND, "Nav Link not found"));
    }

    private NavLinkResponseDTO toResponseDTO(NavLink navLink) {
        NavLinkResponseDTO responseDTO = NavLinkResponseDTO.builder()
                .id(navLink.getId())
                .index(navLink.getIndex())
                .name(navLink.getName())
                .path(navLink.getPath())
                .icon(navLink.getIcon())
                .navGroup(navLink.getNavGroup())
                .status(navLink.getStatus())
                .createdAt(navLink.getCreatedAt())
                .updatedAt(navLink.getUpdatedAt())
                .createdBy(navLink.getCreatedBy())
                .updatedBy(navLink.getUpdatedBy())
                .createdByName(navLink.getCreatedBy() != null ? "Unknown" : null)
                .updatedByName(navLink.getUpdatedBy() != null ? "Unknown" : null)
                .build();
        return responseDTO;
    }

    @Override
    public List<GroupedNavLinkResponseDTO> getGroupedNavLinks() {

        List<NavLink> navLinks = navLinkDao.findAll();

        // Group by navGroup, treating null as "default"
        Map<String, List<NavLink>> groupedByNavGroup = navLinks.stream()
                .collect(Collectors.groupingBy(
                        navLink -> navLink.getNavGroup() != null ? navLink.getNavGroup() : "default"
                ));

        return groupedByNavGroup.entrySet().stream()
                .map(entry -> GroupedNavLinkResponseDTO.builder()
                        .navGroup(entry.getKey())
                        .navlinks(entry.getValue().stream()
                                .map(this::toResponseDTO)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}

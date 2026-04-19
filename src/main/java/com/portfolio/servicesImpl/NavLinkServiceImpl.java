package com.portfolio.servicesImpl;

import com.portfolio.dtos.NavLinks.NavLinkRequestDTO;
import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.dtos.NavLinks.GroupedNavLinkResponseDTO;
import com.portfolio.entities.NavLink;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.RoleEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.NavLinkRepository;
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

    private final NavLinkRepository navLinkRepository;
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

        navLinkRepository.save(navLink);
        return toResponseDTO(navLink);
    }

    @Override
    public NavLinkResponseDTO updateNavLink(String id, NavLinkRequestDTO request)
            throws GenericException {

        NavLink existing = navLinkRepository.findById(id)
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

        navLinkRepository.save(existing);
        return toResponseDTO(existing);
    }

    @Override
    public void deleteNavLink(String id) throws GenericException {

        NavLink navLink = navLinkRepository.findById(id)
                .orElseThrow(() -> new GenericException(
                        ExceptionCodeEnum.NAV_LINK_NOT_FOUND,
                        "Nav Link not found"
                ));

        navLink.setStatus(StatusEnum.INACTIVE);
        navLinkRepository.save(navLink);
    }

    @Override
    public List<NavLinkResponseDTO> getNavLinks() {

        return navLinkRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<NavLinkResponseDTO> getAllNavLinks(
            Pageable pageable,
            String search,
            StatusEnum status,
            String sortBy,
            String sortDir
    ) {

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy
        );

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasStatus = status != null;

        Page<NavLink> navLinks;

        if (hasSearch && hasStatus) {
            navLinks = navLinkRepository.searchByStatus(
                    search, status, sortedPageable
            );
        } else if (hasStatus) {
            navLinks = navLinkRepository.findByStatus(
                    status, sortedPageable
            );
        } else if (hasSearch) {
            navLinks = navLinkRepository.search(
                    search, sortedPageable
            );
        } else {
            navLinks = navLinkRepository.findAll(sortedPageable);
        }

        return navLinks.map(this::toResponseDTO);
    }

    @Override
    public NavLinkResponseDTO getNavLink(String id) throws GenericException {

        NavLink navLink = navLinkRepository.findById(id)
                .orElseThrow(() -> new GenericException(
                        ExceptionCodeEnum.NAV_LINK_NOT_FOUND,
                        "Nav Link not found"
                ));

        return toResponseDTO(navLink);
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
                .build();
        helper.setAudit(navLink, responseDTO);
        return responseDTO;
    }

    @Override
    public List<GroupedNavLinkResponseDTO> getGroupedNavLinks() {

        List<NavLink> navLinks = navLinkRepository.findAll();
        
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

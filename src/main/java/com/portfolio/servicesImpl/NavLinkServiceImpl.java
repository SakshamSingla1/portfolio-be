package com.portfolio.servicesImpl;

import com.portfolio.dtos.NavLinks.NavLinkRequestDTO;
import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.entities.NavLink;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.RoleEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.NavLinkRepository;
import com.portfolio.services.NavLinkService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NavLinkServiceImpl implements NavLinkService {

    private final NavLinkRepository navLinkRepository;

    public NavLinkServiceImpl(NavLinkRepository navLinkRepository) {
        this.navLinkRepository = navLinkRepository;
    }

    @Override
    public NavLinkResponseDTO createNavLink(NavLinkRequestDTO request) throws GenericException {

        if (request == null || request.getRoles() == null || request.getRoles().isEmpty()) {
            throw new GenericException(
                    ExceptionCodeEnum.NAV_LINK_NOT_FOUND,
                    "Request or roles cannot be null"
            );
        }

        for (String role : request.getRoles()) {
            RoleEnum.valueOf(role);

            if (navLinkRepository.existsByRolesContainingAndIndex(role, request.getIndex())) {
                throw new GenericException(
                        ExceptionCodeEnum.DUPLICATE_NAV_LINK,
                        "NavLink already exists for role and index"
                );
            }

            if (navLinkRepository.existsByRolesContainingAndPath(role, request.getPath())) {
                throw new GenericException(
                        ExceptionCodeEnum.DUPLICATE_NAV_LINK,
                        "Path already exists for role"
                );
            }
        }

        NavLink navLink = NavLink.builder()
                .roles(request.getRoles())
                .index(request.getIndex())
                .name(request.getName())
                .path(request.getPath())
                .icon(request.getIcon())
                .status(request.getStatus() != null ? request.getStatus() : StatusEnum.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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

        List<String> effectiveRoles =
                request.getRoles() != null ? request.getRoles() : existing.getRoles();

        for (String role : effectiveRoles) {
            RoleEnum.valueOf(role);

            navLinkRepository
                    .findByRolesContainingAndPath(role, request.getPath())
                    .filter(conflict -> !conflict.getId().equals(existing.getId()))
                    .ifPresent(conflict -> {
                        throw new RuntimeException(
                                new GenericException(
                                        ExceptionCodeEnum.DUPLICATE_NAV_LINK,
                                        "Path already exists for role"
                                )
                        );
                    });

            navLinkRepository
                    .findByRolesContainingAndIndex(role, request.getIndex())
                    .filter(conflict -> !conflict.getId().equals(existing.getId()))
                    .ifPresent(conflict -> {
                        throw new RuntimeException(
                                new GenericException(
                                        ExceptionCodeEnum.DUPLICATE_NAV_LINK,
                                        "Index already exists for role"
                                )
                        );
                    });
        }

        existing.setName(request.getName());
        existing.setIndex(request.getIndex());
        existing.setPath(request.getPath());
        existing.setIcon(request.getIcon());
        existing.setRoles(effectiveRoles);
        existing.setStatus(request.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());

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
        navLink.setUpdatedAt(LocalDateTime.now());
        navLinkRepository.save(navLink);
    }

    @Override
    public List<NavLinkResponseDTO> getNavLinks(String role) {

        RoleEnum.valueOf(role);

        return navLinkRepository.findByRolesContaining(role)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<NavLinkResponseDTO> getAllNavLinks(
            Pageable pageable,
            String role,
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
        boolean hasRole = role != null && !role.isBlank();

        Page<NavLink> navLinks;

        if (hasSearch && hasStatus && hasRole) {
            navLinks = navLinkRepository.searchByRoleAndStatus(
                    search, status, role, sortedPageable
            );
        } else if (hasSearch && hasStatus) {
            navLinks = navLinkRepository.searchByStatus(
                    search, status, sortedPageable
            );
        } else if (hasSearch && hasRole) {
            navLinks = navLinkRepository.searchByRole(
                    search, role, sortedPageable
            );
        } else if (hasStatus && hasRole) {
            navLinks = navLinkRepository.findByStatusAndRolesContaining(
                    status, role, sortedPageable
            );
        } else if (hasStatus) {
            navLinks = navLinkRepository.findByStatus(
                    status, sortedPageable
            );
        } else if (hasRole) {
            navLinks = navLinkRepository.findByRolesContaining(
                    role, sortedPageable
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
        return NavLinkResponseDTO.builder()
                .id(navLink.getId())
                .roles(navLink.getRoles())
                .index(navLink.getIndex())
                .name(navLink.getName())
                .path(navLink.getPath())
                .icon(navLink.getIcon())
                .status(navLink.getStatus())
                .createdAt(navLink.getCreatedAt())
                .updatedAt(navLink.getUpdatedAt())
                .build();
    }
}

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
        if (request == null) {
            throw new GenericException(ExceptionCodeEnum.NAV_LINK_NOT_FOUND, "Request is null");
        }

        if (navLinkRepository.existsByRoleAndIndex(request.getRole(), request.getIndex())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_NAV_LINK,"NavLink already exists for this role and index");
        }

        if (navLinkRepository.existsByRoleAndPath(request.getRole(), request.getPath())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_NAV_LINK,"Path already exists for this role");
        }
        NavLink navLink = NavLink.builder()
                .role(request.getRole())
                .index(request.getIndex())
                .name(request.getName())
                .path(request.getPath())
                .status(StatusEnum.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        navLinkRepository.save(navLink);

        return toResponseDTO(navLink);
    }

    @Override
    public NavLinkResponseDTO updateNavLink(String roleIndex, RoleEnum role, NavLinkRequestDTO request) throws GenericException {
        NavLink existing = navLinkRepository.findByRoleAndIndex(role, roleIndex)
                .orElseThrow(() -> new GenericException(
                        ExceptionCodeEnum.NAV_LINK_NOT_FOUND, "Nav Link not found"
                ));
        NavLink pathConflict = navLinkRepository.findByRoleAndPath(role, request.getPath())
                .orElse(null);
        if (pathConflict != null && !pathConflict.getId().equals(existing.getId())) {
            throw new GenericException(
                    ExceptionCodeEnum.DUPLICATE_NAV_LINK,
                    "Path already exists for this role"
            );
        }
        NavLink indexConflict = navLinkRepository
                .findByRoleAndIndex(request.getRole(), request.getIndex())
                .orElse(null);
        if (indexConflict != null && !indexConflict.getId().equals(existing.getId())) {
            throw new GenericException(
                    ExceptionCodeEnum.DUPLICATE_NAV_LINK,
                    "Index already exists for this role"
            );
        }
        existing.setName(request.getName());
        existing.setIndex(request.getIndex());
        existing.setPath(request.getPath());
        existing.setRole(request.getRole());
        existing.setStatus(request.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());

        navLinkRepository.save(existing);
        return toResponseDTO(existing);
    }

    @Override
    public void deleteNavLink(String roleIndex, RoleEnum role) throws GenericException {
        NavLink navLink = navLinkRepository.findByRoleAndIndex(role, roleIndex)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.NAV_LINK_NOT_FOUND, "Nav Link not found"));
        navLinkRepository.delete(navLink);
    }

    @Override
    public List<NavLinkResponseDTO> getNavLinks(RoleEnum role) {
        List<NavLink> navLinks = navLinkRepository.findByRole(role);
        return navLinks.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public Page<NavLinkResponseDTO> getAllNavLinks(
            Pageable pageable, RoleEnum role, String search, StatusEnum status, String sortBy, String sortDir) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        boolean hasSearch = search != null && !search.isBlank();
        boolean hasStatus = status != null;
        boolean hasRole = role != null;

        Page<NavLink> navlinks;
        if(hasStatus && hasSearch && hasRole){
            navlinks = navLinkRepository.searchByRoleAndStatus(search,status,role,sortedPageable);
        }else if(hasStatus && hasSearch){
            navlinks = navLinkRepository.searchByStatus(search,status,sortedPageable);
        }else if(hasSearch && hasRole){
            navlinks = navLinkRepository.searchByRole(search,role,sortedPageable);
        }else if(hasStatus && hasRole){
            navlinks = navLinkRepository.findByStatusAndRole(status,role,sortedPageable);
        }else if(hasStatus){
            navlinks = navLinkRepository.findByStatus(status,sortedPageable);
        }else if(hasRole){
            navlinks = navLinkRepository.findByRole(role,sortedPageable);
        }else if(hasSearch){
            navlinks = navLinkRepository.search(search,sortedPageable);
        }else{
            navlinks = navLinkRepository.findAll(sortedPageable);
        }
        return navlinks.map(this::toResponseDTO);
    }

    @Override
    public NavLinkResponseDTO getNavLink(RoleEnum role, String roleIndex) throws GenericException {
        NavLink navLink = navLinkRepository.findByRoleAndIndex(role, roleIndex)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.NAV_LINK_NOT_FOUND, "Nav Link not found"));
        return toResponseDTO(navLink);
    }

    private NavLinkResponseDTO toResponseDTO(NavLink navLink) {
        return NavLinkResponseDTO.builder()
                .id(navLink.getId())
                .role(navLink.getRole())
                .index(navLink.getIndex())
                .name(navLink.getName())
                .path(navLink.getPath())
                .status(navLink.getStatus())
                .createdAt(navLink.getCreatedAt())
                .updatedAt(navLink.getUpdatedAt())
                .build();
    }
}

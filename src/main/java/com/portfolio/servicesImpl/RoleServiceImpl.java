package com.portfolio.servicesImpl;

import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.dtos.Role.*;
import com.portfolio.entities.NavLink;
import com.portfolio.entities.Permission;
import com.portfolio.entities.RolePermission;
import com.portfolio.entities.Role;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.RoleStatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.NavLinkRepository;
import com.portfolio.repositories.PermissionRepository;
import com.portfolio.repositories.RolePermissionRepository;
import com.portfolio.repositories.RoleRepository;
import com.portfolio.services.RoleService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final NavLinkRepository navLinkRepository;
    private final Helper helper;

    @Override
    @Transactional
    public RoleListResponseDTO upsertRole(String id, RoleRequestBodyDTO roleRequestBodyDTO) throws GenericException {
        if (roleRequestBodyDTO == null || roleRequestBodyDTO.getName() == null) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Role name is required");
        }
        Role role;
        if (id != null) {
            role = roleRepository.findById(String.valueOf(id))
                    .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ROLE_NOT_FOUND, "Role not found"));
        } else {
            if (roleRepository.existsByName(roleRequestBodyDTO.getName())) {
                throw new GenericException(ExceptionCodeEnum.DUPLICATE_ROLE, "Role with this name already exists");
            }
            role = Role.builder().build();
        }
        role.setName(roleRequestBodyDTO.getName());
        role.setDescription(roleRequestBodyDTO.getDescription());
        role.setStatus(roleRequestBodyDTO.getStatus() != null ? roleRequestBodyDTO.getStatus() : RoleStatusEnum.ACTIVE);
        Role savedRole = roleRepository.save(role);
        if (roleRequestBodyDTO.getRolePermissions() != null) {
            rolePermissionRepository.deleteByRoleId(savedRole.getId());

            List<RolePermission> permissions = roleRequestBodyDTO.getRolePermissions().stream()
                    .map(rp -> RolePermission.builder()
                            .roleId(savedRole.getId())
                            .navLinkId(rp.getNavLinkId())
                            .permissionId(rp.getPermissionId())
                            .build())
                    .collect(Collectors.toList());

            rolePermissionRepository.saveAll(permissions);
        }

        return mapToRoleListResponseDTO(savedRole);
    }

    @Override
    public RolePermissionResponseDTO getRolePermissionsByRoleId(String id) throws GenericException {
        Role role = roleRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ROLE_NOT_FOUND, "Role not found"));

        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(id);
        
        List<ModulePermissionDTO> navLinkDTOs = rolePermissions.stream()
                .collect(Collectors.groupingBy(RolePermission::getNavLinkId))
                .entrySet().stream()
                .map(entry -> {
                    try {
                        NavLink navlink = navLinkRepository.findById(entry.getKey())
                                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.NAV_LINK_NOT_FOUND,"Navlink not found"));
                        
                        List<PermissionDTO> permissions = entry.getValue().stream()
                                .map(rp -> {
                                    Permission permission = permissionRepository.findById(rp.getPermissionId()).orElse(null);
                                    if (permission != null) {
                                        return PermissionDTO.builder()
                                                .id(permission.getId())
                                                .name(permission.getName())
                                                .build();
                                    }
                                    return null;
                                })
                                .filter(dto -> dto != null)
                                .collect(Collectors.toList());
                        
                        return ModulePermissionDTO.builder()
                                .navLinkId(navlink.getId())
                                .name(navlink.getName())
                                .path(navlink.getPath())
                                .navGroup(navlink.getNavGroup())
                                .index(navlink.getIndex())
                                .permissions(permissions)
                                .build();
                    } catch (GenericException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        RolePermissionResponseDTO responseDTO = RolePermissionResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .status(role.getStatus())
                .navLinks(navLinkDTOs)
                .build();
        
        helper.setAudit(role, responseDTO);
        return responseDTO;
    }

    @Override
    public Page<RoleListResponseDTO> getAllRolesByCriteria(
            String search, String roleIds,
            String navLinkIds, String permissionIds, String status,
            String startDate, String endDate, Pageable pageable
    ) throws GenericException {
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                (search == null || search.isBlank()) ? "createdAt" : "name"
        );
        
        Page<Role> roles;
        if (search != null && !search.isBlank()) {
            if (status != null && !status.isBlank()) {
                roles = roleRepository.searchByNameAndStatus(search, status, pageable);
            } else {
                roles = roleRepository.searchActiveByName(search, pageable);
            }
        } else if (status != null && !status.isBlank()) {
            roles = roleRepository.findByStatus(status, pageable);
        } else if (roleIds != null && !roleIds.isBlank()) {
            List<String> roleIdList = Arrays.asList(roleIds.split(","));
            roles = roleRepository.findByIdIn(roleIdList, pageable);
        } else {
            roles = roleRepository.findAllActive(pageable);
        }
        
        return roles.map(this::mapToRoleListResponseDTO);
    }

    @Override
    public Page<RoleListResponseDTO> getAllRolesByLogin(
            String search, String role,
            String status,
            String sortBy,
            String sortDir,
            Pageable pageable
    ) throws GenericException {
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                (search == null || search.isBlank()) ? "createdAt" : "name"
        );
        
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        
        Page<Role> roles;
        
        if (search != null && !search.isBlank()) {
            if (status != null && !status.isBlank()) {
                roles = roleRepository.searchByNameAndStatus(search, status, sortedPageable);
            } else {
                roles = roleRepository.searchActiveByName(search, sortedPageable);
            }
        } else if (status != null && !status.isBlank()) {
            roles = roleRepository.findByStatus(status, sortedPageable);
        } else if (role != null && !role.isBlank()) {
            roles = roleRepository.searchByNameAndStatus(search, role, sortedPageable);
        } else {
            roles = roleRepository.findAllActive(sortedPageable);
        }
        
        return roles.map(this::mapToRoleListResponseDTO);
    }

    @Override
    public RoleListResponseDTO getRoleByName(String name) throws GenericException {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ROLE_NOT_FOUND, "Role not found"));
        return mapToRoleListResponseDTO(role);
    }

    @Override
    public RoleListResponseDTO getRoleById(String id) throws GenericException {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ROLE_NOT_FOUND, "Role not found"));
        return mapToRoleListResponseDTO(role);
    }

    private RoleListResponseDTO mapToRoleListResponseDTO(Role role) {
        RoleListResponseDTO responseDTO = RoleListResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .status(role.getStatus())
                .build();
        
        helper.setAudit(role, responseDTO);
        return responseDTO;
    }
}

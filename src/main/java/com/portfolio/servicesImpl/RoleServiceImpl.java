package com.portfolio.servicesImpl;

import com.portfolio.dao.role.RoleDao;
import com.portfolio.dao.role.RolePermissionDao;
import com.portfolio.dtos.Role.*;
import com.portfolio.entities.RolePermission;
import com.portfolio.entities.Role;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.RoleStatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.RoleService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;
    private final RolePermissionDao rolePermissionDao;
    private final Helper helper;

    @Override
    @Transactional
    public RoleListResponseDTO upsertRole(Long id, RoleRequestBodyDTO roleRequestBodyDTO) throws GenericException {
        if (roleRequestBodyDTO == null || roleRequestBodyDTO.getName() == null) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Role name is required");
        }
        Role role;
        if (id != null) {
            role = roleDao.findById(id)
                    .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ROLE_NOT_FOUND, "Role not found"));
        } else {
            if (roleDao.existsByName(roleRequestBodyDTO.getName())) {
                throw new GenericException(ExceptionCodeEnum.DUPLICATE_ROLE, "Role with this name already exists");
            }
            role = Role.builder().build();
        }
        role.setName(roleRequestBodyDTO.getName());
        role.setDescription(roleRequestBodyDTO.getDescription());
        role.setStatus(roleRequestBodyDTO.getStatus() != null ? roleRequestBodyDTO.getStatus() : RoleStatusEnum.ACTIVE);
        Role savedRole = roleDao.save(role);
        if (roleRequestBodyDTO.getRolePermissions() != null) {
            rolePermissionDao.deleteByRoleId(savedRole.getId());

            List<RolePermission> permissions = roleRequestBodyDTO.getRolePermissions().stream()
                    .filter(rp -> rp.getNavLinkId() != null && rp.getPermissionId() != null)
                    .distinct()
                    .collect(Collectors.toMap(
                            rp -> rp.getNavLinkId() + "_" + rp.getPermissionId(),
                            rp -> RolePermission.builder()
                                    .roleId(savedRole.getId())
                                    .navLinkId(rp.getNavLinkId())
                                    .permissionId(rp.getPermissionId())
                                    .build(),
                            (a, b) -> a
                    ))
                    .values()
                    .stream()
                    .collect(Collectors.toList());

            rolePermissionDao.saveAll(permissions);
        }

        return mapToRoleListResponseDTO(savedRole);
    }

    @Override
    public RolePermissionResponseDTO getRolePermissionsByRoleId(Long id) throws GenericException {
        Role role = roleDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ROLE_NOT_FOUND, "Role not found"));

        List<NavLinkPermissionRow> rows = rolePermissionDao.findNavLinksWithPermissionsByRoleId(id);

        Map<Long, List<NavLinkPermissionRow>> byNavLink = rows.stream()
                .collect(Collectors.groupingBy(
                        NavLinkPermissionRow::getNavLinkId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<ModulePermissionDTO> navLinkDTOs = byNavLink.entrySet().stream()
                .map(entry -> {
                    NavLinkPermissionRow first = entry.getValue().get(0);
                    List<PermissionDTO> permissions = entry.getValue().stream()
                            .filter(row -> row.getPermissionId() != null)
                            .map(row -> PermissionDTO.builder()
                                    .id(row.getPermissionId())
                                    .name(row.getPermissionName())
                                    .build())
                            .collect(Collectors.toList());

                    return ModulePermissionDTO.builder()
                            .navLinkId(first.getNavLinkId())
                            .name(first.getNavLinkName())
                            .path(first.getPath())
                            .navGroup(first.getNavGroup())
                            .index(first.getIndex())
                            .permissions(permissions)
                            .build();
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
    public Page<RoleListResponseDTO> getAllRolesByCriteria(String search,String roleIds,String navLinkIds,String permissionIds,
            String status,String startDate, String endDate,Pageable pageable
    ) throws GenericException {
        List<Long> navLinksList = helper.parseIds(navLinkIds);
        Page<RoleListResponseDTO> roleListResponseDTOS = roleDao.findByCriteria(search, navLinksList,
                (status != null && !status.trim().isEmpty()) ? RoleStatusEnum.valueOf(status) : null, pageable);
        List<Long> pageRoleIds = roleListResponseDTOS.getContent().stream().map(RoleListResponseDTO::getId).toList();
        if (!pageRoleIds.isEmpty()) {
            Map<Long, List<RoleMappedModule>> modulesMap = roleDao.findDistinctModulesByRoleIds(pageRoleIds);
            roleListResponseDTOS.forEach(dto -> dto.setRoleMappedModules(modulesMap.getOrDefault(dto.getId(), List.of())));
        }
        return roleListResponseDTOS;
    }

    @Override
    public RoleListResponseDTO getRoleByName(String name) throws GenericException {
        Role role = roleDao.findByName(name)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ROLE_NOT_FOUND, "Role not found"));
        return mapToRoleListResponseDTO(role);
    }

    @Override
    public RoleListResponseDTO getRoleById(Long id) throws GenericException {
        Role role = roleDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ROLE_NOT_FOUND, "Role not found"));
        return mapToRoleListResponseDTO(role);
    }

    @Override
    public List<RoleMappedModule> findDistinctModulesByRoleId(Long roleId){
        return roleDao.findDistinctModulesByRoleId(roleId);
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

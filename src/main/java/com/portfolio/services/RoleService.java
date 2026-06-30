package com.portfolio.services;

import com.portfolio.dtos.Role.RoleMappedModule;
import com.portfolio.dtos.Role.RoleRequestBodyDTO;
import com.portfolio.dtos.Role.RoleListResponseDTO;
import com.portfolio.dtos.Role.RolePermissionResponseDTO;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    RoleListResponseDTO upsertRole(Long id, RoleRequestBodyDTO roleRequestBodyDTO) throws GenericException;
    RolePermissionResponseDTO getRolePermissionsByRoleId(Long id) throws GenericException;
    Page<RoleListResponseDTO> getAllRolesByCriteria(
            String search, String roleIds,
            String navLinkIds, String permissionIds, String status,
            String startDate, String endDate, Pageable pageable
    ) throws GenericException;
    RoleListResponseDTO getRoleByName(String name) throws GenericException;
    RoleListResponseDTO getRoleById(Long id) throws GenericException;
    List<RoleMappedModule> findDistinctModulesByRoleId(Long roleId);
}

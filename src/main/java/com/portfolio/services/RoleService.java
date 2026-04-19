package com.portfolio.services;

import com.portfolio.dtos.Role.RoleRequestBodyDTO;
import com.portfolio.dtos.Role.RoleListResponseDTO;
import com.portfolio.dtos.Role.RolePermissionResponseDTO;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    RoleListResponseDTO upsertRole(String id, RoleRequestBodyDTO roleRequestBodyDTO) throws GenericException;
    RolePermissionResponseDTO getRolePermissionsByRoleId(String id) throws GenericException;
    Page<RoleListResponseDTO> getAllRolesByCriteria(
            String search, String roleIds,
            String navLinkIds, String permissionIds, String status,
            String startDate, String endDate, Pageable pageable
    ) throws GenericException;

    Page<RoleListResponseDTO> getAllRolesByLogin(
            String search, String role,
            String status,
            String sortBy,
            String sortDir,
            Pageable pageable
    ) throws GenericException;

    RoleListResponseDTO getRoleByName(String name) throws GenericException;

    RoleListResponseDTO getRoleById(String id) throws GenericException;
}

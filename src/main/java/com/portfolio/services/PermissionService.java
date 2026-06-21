package com.portfolio.services;

import com.portfolio.dtos.Permission.PermissionRequestDTO;
import com.portfolio.dtos.Permission.PermissionResponseDTO;
import com.portfolio.entities.Permission;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionService {
    List<Permission> getAllPermissions();
    PermissionResponseDTO createPermission(PermissionRequestDTO request) throws GenericException;
    PermissionResponseDTO updatePermission(Long id, PermissionRequestDTO request) throws GenericException;
    void deletePermission(Long id) throws GenericException;
    PermissionResponseDTO getPermissionById(Long id) throws GenericException;
    Page<PermissionResponseDTO> getAllPermissionsPaginated(Pageable pageable, String search, String permissionIds) throws GenericException;

    Page<PermissionResponseDTO> getAllPermissionsByLogin(
            String search, String role,
            String status,
            String sortBy,
            String sortDir,
            Pageable pageable
    ) throws GenericException;
}

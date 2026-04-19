package com.portfolio.servicesImpl;

import com.portfolio.dtos.Permission.PermissionRequestDTO;
import com.portfolio.dtos.Permission.PermissionResponseDTO;
import com.portfolio.entities.Permission;
import com.portfolio.entities.RolePermission;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.PermissionRepository;
import com.portfolio.repositories.RolePermissionRepository;
import com.portfolio.services.PermissionService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final Helper helper;

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public PermissionResponseDTO createPermission(PermissionRequestDTO request) throws GenericException {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Permission name is required");
        }

        if (permissionRepository.existsByName(request.getName())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PERMISSION, "Permission with this name already exists");
        }

        Permission permission = Permission.builder()
                .name(request.getName())
                .build();

        Permission savedPermission = permissionRepository.save(permission);
        return mapToResponseDTO(savedPermission);
    }

    @Override
    public PermissionResponseDTO updatePermission(String id, PermissionRequestDTO request) throws GenericException {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Permission name is required");
        }

        Permission existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PERMISSION_NOT_FOUND, "Permission not found"));

        if (!existingPermission.getName().equals(request.getName()) && 
            permissionRepository.existsByName(request.getName())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PERMISSION, "Permission with this name already exists");
        }
        existingPermission.setName(request.getName());
        Permission updatedPermission = permissionRepository.save(existingPermission);
        return mapToResponseDTO(updatedPermission);
    }

    @Override
    public void deletePermission(String id) throws GenericException {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PERMISSION_NOT_FOUND, "Permission not found"));
        
        permissionRepository.delete(permission);
    }

    @Override
    public PermissionResponseDTO getPermissionById(String id) throws GenericException {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PERMISSION_NOT_FOUND, "Permission not found"));
        
        return mapToResponseDTO(permission);
    }

    @Override
    public Page<PermissionResponseDTO> getAllPermissionsPaginated(Pageable pageable, String search, String permissionIds) throws GenericException {
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                (search == null || search.isBlank()) ? "createdAt" : "name"
        );
        
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        
        Page<Permission> permissions;
        
        if (search != null && !search.isBlank()) {
            if (permissionIds != null && !permissionIds.isBlank()) {
                List<String> permissionIdList = Arrays.asList(permissionIds.split(","));
                permissions = permissionRepository.searchByNameAndIds(search, permissionIdList, sortedPageable);
            } else {
                permissions = permissionRepository.searchByName(search, sortedPageable);
            }
        } else if (permissionIds != null && !permissionIds.isBlank()) {
            List<String> permissionIdList = Arrays.asList(permissionIds.split(","));
            permissions = permissionRepository.findByIdIn(permissionIdList, sortedPageable);
        } else {
            permissions = permissionRepository.findAll(sortedPageable);
        }
        
        return permissions.map(this::mapToResponseDTO);
    }

    @Override
    public Page<PermissionResponseDTO> getAllPermissionsByLogin(
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
        
        Page<Permission> permissions;
        
        if (search != null && !search.isBlank()) {
            if (status != null && !status.isBlank()) {
                permissions = permissionRepository.searchByNameAndStatus(search, status, sortedPageable);
            } else {
                permissions = permissionRepository.searchByName(search, sortedPageable);
            }
        } else if (status != null && !status.isBlank()) {
            permissions = permissionRepository.findByStatus(status, sortedPageable);
        } else if (role != null && !role.isBlank()) {
            // Filter permissions by role through role-permission mapping
            List<RolePermission> rolePermissions = rolePermissionRepository.findAll();
            List<String> permissionIds = rolePermissions.stream()
                    .filter(rp -> role.equals(rp.getRoleId()))
                    .map(RolePermission::getPermissionId)
                    .distinct()
                    .collect(Collectors.toList());
            
            if (permissionIds.isEmpty()) {
                permissions = Page.empty(sortedPageable);
            } else {
                permissions = permissionRepository.searchByNameAndIds(search, permissionIds, sortedPageable);
            }
        } else {
            permissions = permissionRepository.findAll(sortedPageable);
        }
        
        return permissions.map(this::mapToResponseDTO);
    }

    private PermissionResponseDTO mapToResponseDTO(Permission permission) {
        PermissionResponseDTO responseDTO = PermissionResponseDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .build();
        
        helper.setAudit(permission, responseDTO);
        return responseDTO;
    }
}

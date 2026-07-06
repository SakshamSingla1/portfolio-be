package com.portfolio.servicesImpl;

import com.portfolio.dao.permission.PermissionDao;
import com.portfolio.dao.role.RolePermissionDao;
import com.portfolio.dtos.Permission.PermissionRequestDTO;
import com.portfolio.dtos.Permission.PermissionResponseDTO;
import com.portfolio.entities.Permission;
import com.portfolio.entities.RolePermission;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
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

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionDao permissionDao;
    private final RolePermissionDao rolePermissionDao;
    private final Helper helper;

    @Override
    public List<Permission> getAllPermissions() {
        return permissionDao.findAll();
    }

    @Override
    public PermissionResponseDTO createPermission(PermissionRequestDTO request) throws GenericException {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Permission name is required");
        }

        if (permissionDao.existsByName(request.getName())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PERMISSION, "Permission with this name already exists");
        }

        Permission permission = Permission.builder()
                .name(request.getName())
                .build();

        Permission savedPermission = permissionDao.save(permission);
        return mapToResponseDTO(savedPermission);
    }

    @Override
    public PermissionResponseDTO updatePermission(Long id, PermissionRequestDTO request) throws GenericException {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Permission name is required");
        }

        Permission existingPermission = permissionDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PERMISSION_NOT_FOUND, "Permission not found"));

        if (!existingPermission.getName().equals(request.getName()) &&
            permissionDao.existsByName(request.getName())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_PERMISSION, "Permission with this name already exists");
        }
        existingPermission.setName(request.getName());
        Permission updatedPermission = permissionDao.save(existingPermission);
        return mapToResponseDTO(updatedPermission);
    }

    @Override
    public void deletePermission(Long id) throws GenericException {
        Permission permission = permissionDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PERMISSION_NOT_FOUND, "Permission not found"));

        permissionDao.delete(permission);
    }

    @Override
    public PermissionResponseDTO getPermissionById(Long id) throws GenericException {
        return permissionDao.findDTOById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PERMISSION_NOT_FOUND, "Permission not found"));
    }

    @Override
    public Page<PermissionResponseDTO> getAllPermissionsPaginated(Pageable pageable, String search, String permissionIds) throws GenericException {
        Sort sort = Sort.by(
                Sort.Direction.ASC,
                (search == null || search.isBlank()) ? "createdAt" : "name"
        );

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        List<Long> permissionIdList = (permissionIds != null && !permissionIds.isBlank())
                ? Arrays.stream(permissionIds.split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(Long::valueOf).toList()
                : null;

        return permissionDao.findByCriteria(search, permissionIdList, sortedPageable);
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

        List<Long> rolePermissionIds = null;
        if (role != null && !role.isBlank()) {
            Long roleId = Long.valueOf(role);
            rolePermissionIds = rolePermissionDao.findByRoleId(roleId).stream()
                    .map(RolePermission::getPermissionId)
                    .distinct()
                    .toList();
            if (rolePermissionIds.isEmpty()) return Page.empty(sortedPageable);
        }

        return permissionDao.findByCriteria(search, rolePermissionIds, sortedPageable);
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

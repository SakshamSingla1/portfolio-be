package com.portfolio.dao.role;

import com.portfolio.entities.RolePermission;
import com.portfolio.repositories.RolePermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Slf4j
public class RolePermissionDao {

    private final RolePermissionRepository rolePermissionRepository;

    public RolePermissionDao(RolePermissionRepository rolePermissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
    }

    public RolePermission save(RolePermission rolePermission) {
        return rolePermissionRepository.save(rolePermission);
    }

    public List<RolePermission> saveAll(List<RolePermission> rolePermissions) {
        return rolePermissionRepository.saveAll(rolePermissions);
    }

    public List<RolePermission> findByRoleId(Long roleId) {
        return rolePermissionRepository.findByRoleId(roleId);
    }

    public List<RolePermission> findByNavLinkId(Long navLinkId) {
        return rolePermissionRepository.findByNavLinkId(navLinkId);
    }

    public List<RolePermission> findByPermissionId(Long permissionId) {
        return rolePermissionRepository.findByPermissionId(permissionId);
    }

    public List<RolePermission> findByRoleIdIn(List<Long> roleIds) {
        return rolePermissionRepository.findByRoleIdIn(roleIds);
    }

    public RolePermission findByRoleIdAndNavLinkIdAndPermissionId(Long roleId, Long navLinkId, Long permissionId) {
        return rolePermissionRepository.findByRoleIdAndNavLinkIdAndPermissionId(roleId, navLinkId, permissionId);
    }

    @Transactional
    public void deleteByRoleId(Long roleId) {
        rolePermissionRepository.deleteByRoleId(roleId);
    }

    public List<RolePermission> findByRoleIdAndNavLinkId(Long roleId, Long navLinkId) {
        return rolePermissionRepository.findByRoleIdAndNavLinkId(roleId, navLinkId);
    }

    public List<RolePermission> findByNavLinkIdIn(List<Long> navLinkIds) {
        return rolePermissionRepository.findByNavLinkIdIn(navLinkIds);
    }

    public List<RolePermission> findByPermissionIdIn(List<Long> permissionIds) {
        return rolePermissionRepository.findByPermissionIdIn(permissionIds);
    }

    public List<RolePermission> findAll() {
        return rolePermissionRepository.findAll();
    }
}

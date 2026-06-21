package com.portfolio.repositories;

import com.portfolio.entities.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRoleId(Long roleId);

    List<RolePermission> findByNavLinkId(Long navLinkId);

    List<RolePermission> findByPermissionId(Long permissionId);

    List<RolePermission> findByRoleIdIn(List<Long> roleIds);

    RolePermission findByRoleIdAndNavLinkIdAndPermissionId(Long roleId, Long navLinkId, Long permissionId);

    void deleteByRoleId(Long roleId);

    List<RolePermission> findByRoleIdAndNavLinkId(Long roleId, Long navLinkId);

    List<RolePermission> findByNavLinkIdIn(List<Long> navLinkIds);

    List<RolePermission> findByPermissionIdIn(List<Long> permissionIds);
}

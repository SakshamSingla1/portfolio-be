package com.portfolio.repositories;

import com.portfolio.dtos.Role.NavLinkPermissionRow;
import com.portfolio.entities.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRoleId(Long roleId);

    List<RolePermission> findByNavLinkId(Long navLinkId);

    List<RolePermission> findByPermissionId(Long permissionId);

    List<RolePermission> findByRoleIdIn(List<Long> roleIds);

    RolePermission findByRoleIdAndNavLinkIdAndPermissionId(Long roleId, Long navLinkId, Long permissionId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM RolePermission rp WHERE rp.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);

    List<RolePermission> findByRoleIdAndNavLinkId(Long roleId, Long navLinkId);

    List<RolePermission> findByNavLinkIdIn(List<Long> navLinkIds);

    List<RolePermission> findByPermissionIdIn(List<Long> permissionIds);

    @Query("""
            SELECT new com.portfolio.dtos.Role.NavLinkPermissionRow(
                rp.navLinkId, n.name, n.path, n.navGroup, n.index,
                rp.permissionId, p.name
            )
            FROM RolePermission rp
            JOIN NavLink n ON n.id = rp.navLinkId
            LEFT JOIN Permission p ON p.id = rp.permissionId
            WHERE rp.roleId = :roleId
              AND rp.navLinkId IS NOT NULL
            ORDER BY n.index ASC
            """)
    List<NavLinkPermissionRow> findNavLinksWithPermissionsByRoleId(@Param("roleId") Long roleId);
}

package com.portfolio.repositories;

import com.portfolio.dtos.Role.RoleListResponseDTO;
import com.portfolio.dtos.Role.RoleMappedModule;
import com.portfolio.entities.Role;
import com.portfolio.enums.RoleStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    @Query(value = """
    SELECT NEW com.portfolio.dtos.Role.RoleListResponseDTO(
        r.id, r.name, r.status, r.description, r.createdAt,
        r.createdBy, r.updatedAt, r.updatedBy, p1.fullName, p2.fullName
    )
    FROM Role r
    LEFT JOIN RolePermission rp ON rp.roleId = r.id
    LEFT JOIN Permission p ON p.id = rp.permissionId
    LEFT JOIN NavLink nav ON nav.id = rp.navLinkId
    LEFT JOIN Profile p1 ON p1.id = r.createdBy
    LEFT JOIN Profile p2 ON p2.id = r.updatedBy
    WHERE (
        :search IS NULL OR :search = ''
        OR LOWER(r.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
        OR LOWER(r.description) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
        OR LOWER(p.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
        OR LOWER(nav.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
    )
    AND ( :navLinkIds IS NULL OR nav.id IN :navLinkIds )
    AND ( :status IS NULL OR r.status = :status )
    GROUP BY
        r.id, r.name, r.status, r.description, r.createdAt,
        r.createdBy, r.updatedAt, r.updatedBy, p1.fullName, p2.fullName
    """,
    countQuery = """
    SELECT COUNT(DISTINCT r.id)
    FROM Role r
    LEFT JOIN RolePermission rp ON rp.roleId = r.id
    LEFT JOIN Permission p ON p.id = rp.permissionId
    LEFT JOIN NavLink nav ON nav.id = rp.navLinkId
    WHERE (
        :search IS NULL OR :search = ''
        OR LOWER(r.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
        OR LOWER(r.description) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
        OR LOWER(p.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
        OR LOWER(nav.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
    )
    AND ( :navLinkIds IS NULL OR nav.id IN :navLinkIds )
    AND ( :status IS NULL OR r.status = :status )
    """)
    Page<RoleListResponseDTO> findByCriteria(
            @Param("search") String search,
            @Param("navLinkIds") List<Long> navLinkIds,
            @Param("status") RoleStatusEnum status,
            Pageable pageable
    );

    @Query("""
         SELECT DISTINCT NEW com.portfolio.dtos.Role.RoleMappedModule(
             nav.id as moduleId , nav.name as moduleName
         )
         FROM RolePermission rp
         JOIN NavLink nav ON nav.id = rp.navLinkId
         WHERE rp.roleId = :roleId
         ORDER BY nav.id ASC
    """)
    List<RoleMappedModule> findDistinctModulesByRoleId(@Param("roleId") Long roleId);

    // idx_role_permissions_role_id — batch load modules for a page of roles, eliminates N+1
    @Query("""
         SELECT DISTINCT rp.roleId, nav.id, nav.name
         FROM RolePermission rp
         JOIN NavLink nav ON nav.id = rp.navLinkId
         WHERE rp.roleId IN :roleIds
         ORDER BY rp.roleId ASC, nav.id ASC
    """)
    List<Object[]> findDistinctModulesByRoleIds(@Param("roleIds") List<Long> roleIds);
}

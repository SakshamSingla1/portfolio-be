package com.portfolio.repositories;

import com.portfolio.dtos.Permission.PermissionResponseDTO;
import com.portfolio.entities.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findById(Long id);

    List<Permission> findAll();

    @Query("""
            SELECT NEW com.portfolio.dtos.Permission.PermissionResponseDTO(
                p.id, p.name, p.createdAt, p.updatedAt, p.createdBy, p.updatedBy, p1.fullName, p2.fullName
            ) FROM Permission p
            LEFT JOIN Profile p1 ON p1.id = p.createdBy
            LEFT JOIN Profile p2 ON p2.id = p.updatedBy
            WHERE LOWER(p.name) LIKE CONCAT('%', LOWER(CAST(:name AS string)), '%')
    """)
    List<PermissionResponseDTO> searchByName(@Param("name") String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("""
            SELECT NEW com.portfolio.dtos.Permission.PermissionResponseDTO(
                p.id, p.name, p.createdAt, p.updatedAt, p.createdBy, p.updatedBy, p1.fullName, p2.fullName
            ) FROM Permission p
            LEFT JOIN Profile p1 ON p1.id = p.createdBy
            LEFT JOIN Profile p2 ON p2.id = p.updatedBy
            WHERE p.id = :id
    """)
    Optional<PermissionResponseDTO> findDTOById(@Param("id") Long id);

    @Query(value = """
            SELECT NEW com.portfolio.dtos.Permission.PermissionResponseDTO(
                p.id, p.name, p.createdAt, p.updatedAt, p.createdBy, p.updatedBy, p1.fullName, p2.fullName
            ) FROM Permission p
            LEFT JOIN Profile p1 ON p1.id = p.createdBy
            LEFT JOIN Profile p2 ON p2.id = p.updatedBy
            WHERE (:search IS NULL OR :search = '' OR LOWER(p.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            AND (:permissionIds IS NULL OR p.id IN :permissionIds)
    """,
            countQuery = """
            SELECT COUNT(p) FROM Permission p
            WHERE (:search IS NULL OR :search = '' OR LOWER(p.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            AND (:permissionIds IS NULL OR p.id IN :permissionIds)
            """)
    Page<PermissionResponseDTO> findByCriteria(
            @Param("search") String search,
            @Param("permissionIds") List<Long> permissionIds,
            Pageable pageable
    );
}

package com.portfolio.repositories;

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

    @Query("SELECT p FROM Permission p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Permission> searchByName(@Param("name") String name);

    @Query("SELECT p FROM Permission p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Permission> searchByName(@Param("name") String name, Pageable pageable);

    Page<Permission> findAll(Pageable pageable);

    Page<Permission> findByIdIn(List<Long> permissionIds, Pageable pageable);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT p FROM Permission p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.id IN :permissionIds")
    Page<Permission> searchByNameAndIds(
         @Param("name") String name,
         @Param("permissionIds") List<Long> permissionIds,
         Pageable pageable
    );

    // Permission entity has no status field — these return empty to preserve the existing API contract
    @Query("SELECT p FROM Permission p WHERE p.id IS NULL")
    Page<Permission> findByStatus(String status, Pageable pageable);

    @Query("SELECT p FROM Permission p WHERE p.id IS NULL")
    Page<Permission> searchByNameAndStatus(String name, String status, Pageable pageable);
}

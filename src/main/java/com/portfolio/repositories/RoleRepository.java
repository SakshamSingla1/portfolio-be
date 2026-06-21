package com.portfolio.repositories;

import com.portfolio.entities.Role;
import com.portfolio.enums.RoleStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    Page<Role> findByStatus(RoleStatusEnum status, Pageable pageable);

    @Query("SELECT r FROM Role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Role> searchByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT r FROM Role r WHERE r.status != com.portfolio.enums.RoleStatusEnum.DELETED")
    Page<Role> findAllActive(Pageable pageable);

    @Query("SELECT r FROM Role r WHERE r.status != com.portfolio.enums.RoleStatusEnum.DELETED AND LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Role> searchActiveByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT r FROM Role r WHERE r.status != com.portfolio.enums.RoleStatusEnum.DELETED AND r.status = :#{T(com.portfolio.enums.RoleStatusEnum).valueOf(#status)}")
    Page<Role> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT r FROM Role r WHERE r.status != com.portfolio.enums.RoleStatusEnum.DELETED AND LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')) AND r.status = :#{T(com.portfolio.enums.RoleStatusEnum).valueOf(#status)}")
    Page<Role> searchByNameAndStatus(@Param("name") String name, @Param("status") String status, Pageable pageable);

    @Query("SELECT r FROM Role r WHERE r.status != com.portfolio.enums.RoleStatusEnum.DELETED AND r.id IN :roleIds")
    Page<Role> findByIdIn(@Param("roleIds") List<Long> roleIds, Pageable pageable);

    @Query("SELECT r FROM Role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Role> findByName(@Param("name") String name, Pageable pageable);
}

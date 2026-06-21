package com.portfolio.repositories;

import com.portfolio.entities.Profile;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByPhone(String phone);

    Optional<Profile> findByUserName(String userName);

    boolean existsByEmail(String newEmail);

    Page<Profile> findByStatus(StatusEnum status, Pageable pageable);

    @Query("SELECT p FROM Profile p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.userName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Profile> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Profile p WHERE (LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.userName) LIKE LOWER(CONCAT('%', :search, '%'))) AND p.status = :status")
    Page<Profile> searchByStatus(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable
    );

    @Query("SELECT p FROM Profile p WHERE (LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.userName) LIKE LOWER(CONCAT('%', :search, '%'))) AND p.roleId = :role")
    Page<Profile> searchByRole(
            @Param("search") String search,
            @Param("role") Long role,
            Pageable pageable
    );

    @Query("SELECT p FROM Profile p WHERE (LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.userName) LIKE LOWER(CONCAT('%', :search, '%'))) AND p.status = :status AND p.roleId = :role")
    Page<Profile> searchByRoleAndStatus(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            @Param("role") Long role,
            Pageable pageable
    );
}

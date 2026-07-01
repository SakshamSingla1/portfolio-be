package com.portfolio.repositories;

import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.entities.NavLink;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NavLinkRepository extends JpaRepository<NavLink, Long> {

    @Query("""
            SELECT NEW com.portfolio.dtos.NavLinks.NavLinkResponseDTO(
            n.id, n.index, n.name, n.path, n.icon, n.navGroup, n.status,
            n.createdAt, n.updatedAt, n.createdBy, n.updatedBy, p1.fullName, p2.fullName)
            FROM NavLink n
            LEFT JOIN Profile p1 ON n.createdBy = p1.id
            LEFT JOIN Profile p2 ON n.updatedBy = p2.id
            WHERE n.id = :id
            """)
    Optional<NavLinkResponseDTO> findDTOById(@Param("id") Long id);

    @Query("""
            SELECT NEW com.portfolio.dtos.NavLinks.NavLinkResponseDTO(
            n.id, n.index, n.name, n.path, n.icon, n.navGroup, n.status, 
            n.createdAt, n.updatedAt, n.createdBy, n.updatedBy, p1.fullName, 
            p2.fullName)
            FROM NavLink n
            LEFT JOIN Profile p1 ON n.createdBy = p1.id
            LEFT JOIN Profile p2 ON n.updatedBy = p2.id
            WHERE (CAST(:search AS string) IS NULL OR LOWER(n.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            AND (CAST(:status AS string) IS NULL OR n.status = :status)
            """)
    Page<NavLinkResponseDTO> findByCriteria(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable
    );
}

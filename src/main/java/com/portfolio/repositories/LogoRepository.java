package com.portfolio.repositories;

import com.portfolio.dtos.Logos.LogoDropdown;
import com.portfolio.dtos.Logos.LogoResponse;
import com.portfolio.entities.Logo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogoRepository extends JpaRepository<Logo, Long> {

    boolean existsByName(String name);

    @Query("""
            SELECT NEW com.portfolio.dtos.Logos.LogoResponse(
                l.id, l.name, fa.path, l.createdAt, l.updatedAt
            ) FROM Logo l
            LEFT JOIN FileAsset fa ON fa.resourceId = l.id AND fa.resourceType = 'LOGO'
            WHERE l.id = :id
    """)
    Optional<LogoResponse> findDTOById(@Param("id") Long id);

    @Query("""
            SELECT NEW com.portfolio.dtos.Logos.LogoDropdown(
                l.id, l.name, fa.path, l.createdAt, l.updatedAt
            ) FROM Logo l
            LEFT JOIN FileAsset fa ON fa.resourceId = l.id AND fa.resourceType = 'LOGO'
            WHERE (:search IS NULL OR :search = '' OR LOWER(l.name) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<LogoDropdown> findByCriteria(@Param("search") String search, Pageable pageable);
}

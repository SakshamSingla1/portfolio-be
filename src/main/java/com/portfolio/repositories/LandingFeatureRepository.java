package com.portfolio.repositories;

import com.portfolio.dtos.LandingPage.LandingFeatureResponse;
import com.portfolio.entities.LandingFeature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LandingFeatureRepository extends JpaRepository<LandingFeature, Long> {

    List<LandingFeature> findByIsActiveTrueOrderBySortOrderAsc();

    // idx_landing_features_is_active + sort_order
    @Query("""
            SELECT NEW com.portfolio.dtos.LandingPage.LandingFeatureResponse(
                f.id, f.iconName, f.colorKey, f.title, f.description, f.sortOrder, f.isActive,
                f.createdAt, f.updatedAt, f.createdBy, f.updatedBy, p1.fullName, p2.fullName
            ) FROM LandingFeature f
            LEFT JOIN Profile p1 ON p1.id = f.createdBy
            LEFT JOIN Profile p2 ON p2.id = f.updatedBy
            WHERE f.isActive = true
            ORDER BY f.sortOrder ASC
            """)
    List<LandingFeatureResponse> findActiveAsDTOs();

    @Query("""
            SELECT NEW com.portfolio.dtos.LandingPage.LandingFeatureResponse(
                f.id, f.iconName, f.colorKey, f.title, f.description, f.sortOrder, f.isActive,
                f.createdAt, f.updatedAt, f.createdBy, f.updatedBy, p1.fullName, p2.fullName
            ) FROM LandingFeature f
            LEFT JOIN Profile p1 ON p1.id = f.createdBy
            LEFT JOIN Profile p2 ON p2.id = f.updatedBy
            WHERE f.id = :id
            """)
    Optional<LandingFeatureResponse> findDTOById(@Param("id") Long id);

    @Query(value = """
            SELECT NEW com.portfolio.dtos.LandingPage.LandingFeatureResponse(
                f.id, f.iconName, f.colorKey, f.title, f.description, f.sortOrder, f.isActive,
                f.createdAt, f.updatedAt, f.createdBy, f.updatedBy, p1.fullName, p2.fullName
            ) FROM LandingFeature f
            LEFT JOIN Profile p1 ON p1.id = f.createdBy
            LEFT JOIN Profile p2 ON p2.id = f.updatedBy
            WHERE (:search IS NULL OR :search = ''
                OR LOWER(f.title) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(f.description) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            AND (:isActive IS NULL OR f.isActive = :isActive)
            """,
            countQuery = """
            SELECT COUNT(f) FROM LandingFeature f
            WHERE (:search IS NULL OR :search = ''
                OR LOWER(f.title) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(f.description) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            AND (:isActive IS NULL OR f.isActive = :isActive)
            """)
    Page<LandingFeatureResponse> findByCriteria(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}

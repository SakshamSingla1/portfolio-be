package com.portfolio.repositories;

import com.portfolio.dtos.LandingPage.LandingAudienceCardResponse;
import com.portfolio.entities.LandingAudienceCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LandingAudienceCardRepository extends JpaRepository<LandingAudienceCard, Long> {

    List<LandingAudienceCard> findByIsActiveTrueOrderBySortOrderAsc();

    // idx_landing_audience_cards_is_active + sort_order
    @Query("""
            SELECT NEW com.portfolio.dtos.LandingPage.LandingAudienceCardResponse(
                c.id, c.iconName, c.colorKey, c.title, c.description, c.sortOrder, c.isActive,
                c.createdAt, c.updatedAt, c.createdBy, c.updatedBy, p1.fullName, p2.fullName
            ) FROM LandingAudienceCard c
            LEFT JOIN Profile p1 ON p1.id = c.createdBy
            LEFT JOIN Profile p2 ON p2.id = c.updatedBy
            WHERE c.isActive = true
            ORDER BY c.sortOrder ASC
            """)
    List<LandingAudienceCardResponse> findActiveAsDTOs();

    @Query("""
            SELECT NEW com.portfolio.dtos.LandingPage.LandingAudienceCardResponse(
                c.id, c.iconName, c.colorKey, c.title, c.description, c.sortOrder, c.isActive,
                c.createdAt, c.updatedAt, c.createdBy, c.updatedBy, p1.fullName, p2.fullName
            ) FROM LandingAudienceCard c
            LEFT JOIN Profile p1 ON p1.id = c.createdBy
            LEFT JOIN Profile p2 ON p2.id = c.updatedBy
            WHERE c.id = :id
            """)
    Optional<LandingAudienceCardResponse> findDTOById(@Param("id") Long id);

    @Query("""
            SELECT NEW com.portfolio.dtos.LandingPage.LandingAudienceCardResponse(
                c.id, c.iconName, c.colorKey, c.title, c.description, c.sortOrder, c.isActive,
                c.createdAt, c.updatedAt, c.createdBy, c.updatedBy, p1.fullName, p2.fullName
            ) FROM LandingAudienceCard c
            LEFT JOIN Profile p1 ON p1.id = c.createdBy
            LEFT JOIN Profile p2 ON p2.id = c.updatedBy
            WHERE (:search IS NULL OR :search = ''
                OR LOWER(c.title) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(c.description) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            AND (:isActive IS NULL OR c.isActive = :isActive)
            """)
    Page<LandingAudienceCardResponse> findByCriteria(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}

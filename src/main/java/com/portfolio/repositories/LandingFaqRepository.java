package com.portfolio.repositories;

import com.portfolio.dtos.LandingPage.LandingFaqResponse;
import com.portfolio.entities.LandingFaq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LandingFaqRepository extends JpaRepository<LandingFaq, Long> {

    List<LandingFaq> findByIsActiveTrueOrderBySortOrderAsc();

    // idx_landing_faqs_is_active + sort_order
    @Query("""
            SELECT NEW com.portfolio.dtos.LandingPage.LandingFaqResponse(
                f.id, f.question, f.answer, f.sortOrder, f.isActive,
                f.createdAt, f.updatedAt, f.createdBy, f.updatedBy, p1.fullName, p2.fullName
            ) FROM LandingFaq f
            LEFT JOIN Profile p1 ON p1.id = f.createdBy
            LEFT JOIN Profile p2 ON p2.id = f.updatedBy
            WHERE f.isActive = true
            ORDER BY f.sortOrder ASC
            """)
    List<LandingFaqResponse> findActiveAsDTOs();

    @Query("""
            SELECT NEW com.portfolio.dtos.LandingPage.LandingFaqResponse(
                f.id, f.question, f.answer, f.sortOrder, f.isActive,
                f.createdAt, f.updatedAt, f.createdBy, f.updatedBy, p1.fullName, p2.fullName
            ) FROM LandingFaq f
            LEFT JOIN Profile p1 ON p1.id = f.createdBy
            LEFT JOIN Profile p2 ON p2.id = f.updatedBy
            WHERE f.id = :id
            """)
    Optional<LandingFaqResponse> findDTOById(@Param("id") Long id);

    @Query("""
            SELECT NEW com.portfolio.dtos.LandingPage.LandingFaqResponse(
                f.id, f.question, f.answer, f.sortOrder, f.isActive,
                f.createdAt, f.updatedAt, f.createdBy, f.updatedBy, p1.fullName, p2.fullName
            ) FROM LandingFaq f
            LEFT JOIN Profile p1 ON p1.id = f.createdBy
            LEFT JOIN Profile p2 ON p2.id = f.updatedBy
            WHERE (:search IS NULL OR :search = ''
                OR LOWER(f.question) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(f.answer) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:isActive IS NULL OR f.isActive = :isActive)
            """)
    Page<LandingFaqResponse> findByCriteria(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}

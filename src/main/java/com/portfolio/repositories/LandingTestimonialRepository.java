package com.portfolio.repositories;

import com.portfolio.dtos.LandingPage.LandingTestimonialResponse;
import com.portfolio.entities.LandingTestimonial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LandingTestimonialRepository extends JpaRepository<LandingTestimonial, Long> {

    List<LandingTestimonial> findByIsActiveTrueOrderBySortOrderAsc();

    // idx_landing_testimonials_is_active + sort_order
    @Query("""
            SELECT NEW com.portfolio.dtos.LandingPage.LandingTestimonialResponse(
                t.id, t.authorName, t.authorRole, t.authorCompany, t.avatarUrl, t.content, t.linkedinUrl,
                t.sortOrder, t.isActive,
                t.createdAt, t.updatedAt, t.createdBy, t.updatedBy, p1.fullName, p2.fullName
            ) FROM LandingTestimonial t
            LEFT JOIN Profile p1 ON p1.id = t.createdBy
            LEFT JOIN Profile p2 ON p2.id = t.updatedBy
            WHERE t.isActive = true
            ORDER BY t.sortOrder ASC
            """)
    List<LandingTestimonialResponse> findActiveAsDTOs();

    @Query("""
            SELECT NEW com.portfolio.dtos.LandingPage.LandingTestimonialResponse(
                t.id, t.authorName, t.authorRole, t.authorCompany, t.avatarUrl, t.content, t.linkedinUrl,
                t.sortOrder, t.isActive,
                t.createdAt, t.updatedAt, t.createdBy, t.updatedBy, p1.fullName, p2.fullName
            ) FROM LandingTestimonial t
            LEFT JOIN Profile p1 ON p1.id = t.createdBy
            LEFT JOIN Profile p2 ON p2.id = t.updatedBy
            WHERE t.id = :id
            """)
    Optional<LandingTestimonialResponse> findDTOById(@Param("id") Long id);

    @Query("""
            SELECT NEW com.portfolio.dtos.LandingPage.LandingTestimonialResponse(
                t.id, t.authorName, t.authorRole, t.authorCompany, t.avatarUrl, t.content, t.linkedinUrl,
                t.sortOrder, t.isActive,
                t.createdAt, t.updatedAt, t.createdBy, t.updatedBy, p1.fullName, p2.fullName
            ) FROM LandingTestimonial t
            LEFT JOIN Profile p1 ON p1.id = t.createdBy
            LEFT JOIN Profile p2 ON p2.id = t.updatedBy
            WHERE (:search IS NULL OR :search = ''
                OR LOWER(t.authorName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(t.content) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:isActive IS NULL OR t.isActive = :isActive)
            """)
    Page<LandingTestimonialResponse> findByCriteria(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}

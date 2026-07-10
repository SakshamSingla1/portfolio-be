package com.portfolio.repositories;

import com.portfolio.entities.BlogPost;
import com.portfolio.enums.BlogStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    boolean existsByProfileIdAndSlug(Long profileId, String slug);

    Optional<BlogPost> findByProfileIdAndSlug(Long profileId, String slug);

    Page<BlogPost> findByProfileId(Long profileId, Pageable pageable);

    Page<BlogPost> findByProfileIdAndStatus(Long profileId, BlogStatusEnum status, Pageable pageable);

    long countByProfileId(Long profileId);

    long countByProfileIdAndStatus(Long profileId, BlogStatusEnum status);

    @Modifying
    @Transactional
    @Query("UPDATE BlogPost p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Query("""
            SELECT p FROM BlogPost p
            WHERE (:profileId IS NULL OR p.profileId = :profileId)
            AND (:status IS NULL OR p.status = :status)
            AND (:search IS NULL OR :search = ''
                OR LOWER(p.title) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(p.excerpt) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            """)
    Page<BlogPost> findByCriteria(
            @Param("profileId") Long profileId,
            @Param("status") BlogStatusEnum status,
            @Param("search") String search,
            Pageable pageable
    );
}

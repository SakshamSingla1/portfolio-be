package com.portfolio.repositories;

import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.entities.SocialLinks;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLinks, Long> {

    Optional<SocialLinks> findByPlatformAndUrl(PlatformEnum platform, String url);

    Optional<SocialLinks> findByProfileIdAndPlatformAndStatus(Long profileId, PlatformEnum platform, StatusEnum status);

    boolean existsByProfileIdAndPlatformAndStatusNot(Long profileId, PlatformEnum platform, StatusEnum status);

    @Query("""
            SELECT NEW com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO(
                s.id, s.platform, s.url, s.order, s.status, s.createdAt, s.updatedAt
            ) FROM SocialLinks s
            WHERE (:profileId IS NULL OR s.profileId = :profileId)
            AND (:status IS NULL OR s.status = :status)
            AND (:search IS NULL OR :search = '' OR LOWER(CAST(s.platform AS String)) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
    """)
    Page<SocialLinkResponseDTO> findByCriteria(
            @Param("profileId") Long profileId,
            @Param("status") StatusEnum status,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
            SELECT NEW com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO(
                s.id, s.platform, s.url, s.order, s.status, s.createdAt, s.updatedAt
            ) FROM SocialLinks s
            WHERE s.id = :id
    """)
    Optional<SocialLinkResponseDTO> findDTOById(@Param("id") Long id);

    long countByProfileId(Long profileId);

    List<SocialLinks> findByProfileIdAndStatusOrderByOrderAsc(Long profileId, StatusEnum statusEnum);

    @Query("SELECT s FROM SocialLinks s WHERE s.platform = :platform AND s.status = :status")
    Optional<SocialLinks> findPortfolioUrlByPlatformAndStatus(
            @Param("platform") PlatformEnum platform,
            @Param("status") StatusEnum status
    );

    @Query("SELECT s FROM SocialLinks s WHERE s.platform = :platform AND LOWER(s.url) LIKE CONCAT('%', LOWER(CAST(:host AS string)), '%')")
    Optional<SocialLinks> findByPlatformAndUrlContainingHost(
            @Param("platform") PlatformEnum platform,
            @Param("host") String host
    );
}

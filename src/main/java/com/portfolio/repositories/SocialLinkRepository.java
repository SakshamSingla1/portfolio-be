package com.portfolio.repositories;

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

    Page<SocialLinks> findByStatus(StatusEnum status, Pageable pageable);

    Page<SocialLinks> findByProfileId(Long profileId, Pageable pageable);

    Page<SocialLinks> findByStatusAndProfileId(StatusEnum status, Long profileId, Pageable pageable);

    @Query("SELECT s FROM SocialLinks s WHERE LOWER(CAST(s.platform AS String)) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<SocialLinks> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT s FROM SocialLinks s WHERE LOWER(CAST(s.platform AS String)) LIKE LOWER(CONCAT('%', :search, '%')) AND s.status = :status")
    Page<SocialLinks> searchByStatus(@Param("search") String search, @Param("status") StatusEnum status, Pageable pageable);

    @Query("SELECT s FROM SocialLinks s WHERE LOWER(CAST(s.platform AS String)) LIKE LOWER(CONCAT('%', :search, '%')) AND s.profileId = :profileId")
    Page<SocialLinks> searchByProfileId(@Param("search") String search, @Param("profileId") Long profileId, Pageable pageable);

    @Query("SELECT s FROM SocialLinks s WHERE LOWER(CAST(s.platform AS String)) LIKE LOWER(CONCAT('%', :search, '%')) AND s.status = :status AND s.profileId = :profileId")
    Page<SocialLinks> searchByProfileIdAndStatus(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            @Param("profileId") Long profileId,
            Pageable pageable
    );

    long countByProfileId(Long profileId);

    List<SocialLinks> findByProfileIdAndStatusOrderByOrderAsc(Long profileId, StatusEnum statusEnum);

    @Query("SELECT s FROM SocialLinks s WHERE s.platform = :platform AND s.status = :status")
    Optional<SocialLinks> findPortfolioUrlByPlatformAndStatus(
            @Param("platform") PlatformEnum platform,
            @Param("status") StatusEnum status
    );
}

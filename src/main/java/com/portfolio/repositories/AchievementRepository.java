package com.portfolio.repositories;

import com.portfolio.entities.Achievements;
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
public interface AchievementRepository extends JpaRepository<Achievements, Long> {

    @Query("SELECT a FROM Achievements a WHERE a.profileId = :profileId AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.issuer) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Achievements> findByProfileIdWithSearch(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT a FROM Achievements a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.issuer) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Achievements> findBySearch(@Param("search") String search, Pageable pageable);

    Page<Achievements> findByProfileId(Long profileId, Pageable pageable);

    boolean existsByProfileIdAndOrder(Long profileId, String order);

    boolean existsByProfileIdAndOrderAndIdNot(Long profileId, String order, Long id);

    List<Achievements> findByProfileIdAndStatusOrderByOrderAsc(Long profileId, StatusEnum status);

    long countByProfileId(Long profileId);

    Optional<Achievements> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId);
}

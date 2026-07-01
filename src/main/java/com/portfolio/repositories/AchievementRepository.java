package com.portfolio.repositories;

import com.portfolio.dtos.Achievements.AchievementResponseDTO;
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

    @Query("""
            SELECT NEW com.portfolio.dtos.Achievements.AchievementResponseDTO(
                a.id, a.title, a.description, a.issuer, a.achievedAt,
                fa.path, fa.publicId, a.order, a.status, a.createdAt,
                a.updatedAt, a.createdBy, a.updatedBy, p1.fullName, p2.fullName
            ) FROM Achievements a
              LEFT JOIN FileAsset fa ON fa.resourceId = a.id AND fa.resourceType = 'ACHIEVEMENT'
              LEFT JOIN Profile p1 ON p1.id = a.createdBy
              LEFT JOIN Profile p2 ON p2.id = a.updatedBy
            WHERE (:profileId IS NULL OR a.profileId = :profileId)
             AND (:search IS NULL OR :search = ''
                 OR LOWER(a.title) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                 OR LOWER(a.description) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                 OR LOWER(a.issuer) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
             ORDER BY a.order ASC
    """)
    Page<AchievementResponseDTO> findByCriteria(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
            SELECT NEW com.portfolio.dtos.Achievements.AchievementResponseDTO(
                a.id, a.title, a.description, a.issuer, a.achievedAt,
                fa.path, fa.publicId, a.order, a.status, a.createdAt,
                a.updatedAt, a.createdBy, a.updatedBy, p1.fullName, p2.fullName
            ) FROM Achievements a
              LEFT JOIN FileAsset fa ON fa.resourceId = a.id AND fa.resourceType = 'ACHIEVEMENT'
              LEFT JOIN Profile p1 ON p1.id = a.createdBy
              LEFT JOIN Profile p2 ON p2.id = a.updatedBy
            WHERE a.id = :id
    """)
    Optional<AchievementResponseDTO> findDTOById(@Param("id") Long id);

    boolean existsByProfileIdAndOrder(Long profileId, String order);

    boolean existsByProfileIdAndOrderAndIdNot(Long profileId, String order, Long id);

    List<Achievements> findByProfileIdAndStatusOrderByOrderAsc(Long profileId, StatusEnum status);

    long countByProfileId(Long profileId);

    Optional<Achievements> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId);
}

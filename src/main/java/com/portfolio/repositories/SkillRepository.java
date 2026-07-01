package com.portfolio.repositories;

import com.portfolio.dtos.Skill.SkillResponse;
import com.portfolio.entities.Skill;
import com.portfolio.enums.SkillLevelEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    @Query("""
            SELECT NEW com.portfolio.dtos.Skill.SkillResponse(
                s.id, s.logoId, s.logoName, fa.path,
                s.category, s.level, s.createdAt, s.updatedAt
            ) FROM Skill s
            LEFT JOIN FileAsset fa ON fa.resourceId = s.logoId AND fa.resourceType = 'LOGO'
            WHERE (:profileId IS NULL OR s.profileId = :profileId)
            AND (:search IS NULL OR :search = '' OR LOWER(s.logoName) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
    """)
    Page<SkillResponse> findByCriteria(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
            SELECT NEW com.portfolio.dtos.Skill.SkillResponse(
                s.id, s.logoId, s.logoName, fa.path,
                s.category, s.level, s.createdAt, s.updatedAt
            ) FROM Skill s
            LEFT JOIN FileAsset fa ON fa.resourceId = s.logoId AND fa.resourceType = 'LOGO'
            WHERE s.id = :id
    """)
    Optional<SkillResponse> findDTOById(@Param("id") Long id);

    Page<Skill> findByProfileId(Long profileId, Pageable pageable);

    List<Skill> findByProfileId(Long profileId);

    boolean existsByLogoIdAndProfileId(Long logoId, Long profileId);

    long countByProfileId(Long profileId);

    Optional<Skill> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId);

    Long countByLevel(SkillLevelEnum levelEnum);
}

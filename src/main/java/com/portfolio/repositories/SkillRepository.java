package com.portfolio.repositories;

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

    @Query("SELECT s FROM Skill s WHERE s.profileId = :profileId AND LOWER(s.logoName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Skill> findByProfileIdWithSearch(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT s FROM Skill s WHERE LOWER(s.logoName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Skill> findBySearch(@Param("search") String search, Pageable pageable);

    Page<Skill> findByProfileId(Long profileId, Pageable pageable);

    List<Skill> findByProfileId(Long profileId);

    boolean existsByLogoIdAndProfileId(Long logoId, Long profileId);

    long countByProfileId(Long profileId);

    Optional<Skill> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId);

    Long countByLevel(SkillLevelEnum levelEnum);
}

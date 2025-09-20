package com.portfolio.repositories;

import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.dtos.SkillResponse;
import com.portfolio.entities.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {

    // Check if a profile already has this skill
    boolean existsByLogoIdAndProfileId(Integer logoId, Integer profileId);

    // Fetch skills by profile with search and pagination
    @Query("""
        SELECT new com.portfolio.dtos.SkillResponse(s.id, s.logo.id,s.logo.name, s.logo.url,s.logo.category,s.level)
        FROM Skill s
        WHERE s.profile.id = :profileId
          AND (:search IS NULL OR :search = '' OR LOWER(s.logo.name) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<SkillResponse> findByProfileIdWithSearch(Integer profileId, String search, Pageable pageable);

    List<Skill> findByProfileId(Integer profileId);
}

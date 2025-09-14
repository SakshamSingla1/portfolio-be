package com.portfolio.repositories;

import com.portfolio.entities.Education;
import com.portfolio.entities.Profile;
import com.portfolio.enums.DegreeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<Education, Integer> {

    Education findByDegreeAndProfile(DegreeEnum degree, Profile profile);

    boolean existsByDegreeAndProfileId(DegreeEnum degree, Integer profileId);

    void deleteByDegreeAndProfileId(DegreeEnum degree, Integer profileId);

    @Query("""
    SELECT e 
    FROM Education e
    WHERE e.profile.id = :profileId
      AND (:search IS NULL OR :search = ''
           OR LOWER(e.institution) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(e.fieldOfStudy) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(e.location) LIKE LOWER(CONCAT('%', :search, '%'))
      )
    ORDER BY e.startYear DESC
""")
    Page<Education> findByProfileIdWithSearch(Integer profileId, String search, Pageable pageable);
}

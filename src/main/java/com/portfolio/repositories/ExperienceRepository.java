package com.portfolio.repositories;

import com.portfolio.entities.Experience;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Repository for Experience entity.
 * Provides CRUD operations and custom queries:
 * - Check uniqueness of experiences
 * - Paginated search by profile with optional keyword
 */
@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Integer> {

    /**
     * Check if an experience already exists for a profile with the same company, job title, and start date
     */
    boolean existsByProfileIdAndCompanyNameAndJobTitleAndStartDate(
            Integer profileId, String companyName, String jobTitle, Date startDate
    );

    /**
     * Check uniqueness for update operation (exclude current record)
     */
    boolean existsByProfileIdAndCompanyNameAndJobTitleAndStartDateAndIdNot(
            Integer profileId, String companyName, String jobTitle, Date startDate, Integer id
    );

    /**
     * Paginated search by profile ID with optional search on companyName, jobTitle, location, or description
     */
    @Query("""
        SELECT e
        FROM Experience e
        WHERE e.profile.id = :profileId
          AND (:search IS NULL OR :search = ''
               OR LOWER(e.companyName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(e.location) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))
          )
        ORDER BY e.startDate DESC
    """)
    Page<Experience> findByProfileIdWithSearch(Integer profileId, String search, Pageable pageable);
}

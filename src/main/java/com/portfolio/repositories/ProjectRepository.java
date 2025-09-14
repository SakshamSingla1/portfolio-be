package com.portfolio.repositories;

import com.portfolio.entities.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    // Find a project by name within a profile
    Project findByProjectNameAndProfileId(String projectName, Integer profileId);

    // Check existence by project name and profile
    boolean existsByProjectNameAndProfileId(String projectName, Integer profileId);

    // Paginated search by profile with optional search text
    @Query("""
        SELECT p FROM Project p
        WHERE p.profile.id = :profileId
          AND (:search IS NULL OR :search = ''
               OR LOWER(p.projectName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(p.projectDescription) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<Project> findByProfileIdWithSearch(Integer profileId, String search, Pageable pageable);
}

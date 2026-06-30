package com.portfolio.repositories;

import com.portfolio.entities.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByProjectNameAndProfileId(String projectName, Long profileId);

    List<Project> findByProfileId(Long profileId);

    @Query("""
            SELECT p FROM Project p
            WHERE (:profileId IS NULL OR p.profileId = :profileId)
            AND (:search IS NULL OR :search = ''
                OR LOWER(p.projectName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.projectDescription) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<Project> findByCriteria(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable
    );

    Page<Project> findByProfileId(Long profileId, Pageable pageable);

    long countByProfileId(Long profileId);

    Optional<Project> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId);
}

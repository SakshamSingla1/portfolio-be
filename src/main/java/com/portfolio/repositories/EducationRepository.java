package com.portfolio.repositories;

import com.portfolio.entities.Education;
import com.portfolio.enums.DegreeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {

    boolean existsByDegreeAndProfileId(DegreeEnum degree, Long profileId);

    List<Education> findByProfileId(Long profileId);

    @Query("SELECT e FROM Education e WHERE e.profileId = :profileId AND (LOWER(e.institution) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.fieldOfStudy) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.location) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Education> findByProfileIdWithSearch(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT e FROM Education e WHERE LOWER(e.institution) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.fieldOfStudy) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.location) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Education> findBySearch(@Param("search") String search, Pageable pageable);

    Page<Education> findByProfileId(Long profileId, Pageable pageable);

    long countByProfileId(Long profileId);

    Optional<Education> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId);
}

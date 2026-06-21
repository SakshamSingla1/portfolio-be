package com.portfolio.repositories;

import com.portfolio.entities.Experience;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    boolean existsByProfileIdAndCompanyNameAndJobTitleAndStartDate(
            Long profileId, String companyName, String jobTitle, LocalDate startDate);

    boolean existsByProfileIdAndCompanyNameAndJobTitleAndStartDateAndIdNot(
            Long profileId, String companyName, String jobTitle, LocalDate startDate, Long id);

    @Query("SELECT e FROM Experience e WHERE e.profileId = :profileId AND (LOWER(e.companyName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.location) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Experience> findByProfileIdWithSearch(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT e FROM Experience e WHERE LOWER(e.companyName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.location) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Experience> findBySearch(@Param("search") String search, Pageable pageable);

    Page<Experience> findByProfileId(Long profileId, Pageable pageable);

    List<Experience> findByProfileId(Long profileId);

    long countByProfileId(Long profileId);

    Optional<Experience> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId);
}

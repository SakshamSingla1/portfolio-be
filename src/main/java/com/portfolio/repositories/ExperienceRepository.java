package com.portfolio.repositories;

import com.portfolio.dtos.Experience.ExperienceResponse;
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

    List<Experience> findByProfileId(Long profileId);

    long countByProfileId(Long profileId);

    Optional<Experience> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId);

    @Query("""
            SELECT NEW com.portfolio.dtos.Experience.ExperienceResponse(
                ex.id, ex.companyName, ex.jobTitle, ex.location,
                ex.startDate, ex.endDate, ex.employmentStatus, ex.description,
                ex.createdAt, ex.updatedAt, ex.createdBy, ex.updatedBy, p1.fullName, p2.fullName
            ) FROM Experience ex
            LEFT JOIN Profile p1 ON p1.id = ex.createdBy
            LEFT JOIN Profile p2 ON p2.id = ex.updatedBy
            WHERE (:profileId IS NULL OR ex.profileId = :profileId)
            AND (:search IS NULL OR :search = ''
                OR LOWER(ex.companyName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(ex.jobTitle) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(ex.location) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<ExperienceResponse> findByCriteria(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable
    );
}

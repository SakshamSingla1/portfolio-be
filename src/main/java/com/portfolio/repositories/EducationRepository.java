package com.portfolio.repositories;

import com.portfolio.dtos.Education.EducationResponse;
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

    @Query("""
            SELECT NEW com.portfolio.dtos.Education.EducationResponse(
                e.id, e.degree, e.institution, e.fieldOfStudy, e.startYear, e.location,
                e.endYear, e.description, e.grade, e.createdAt,
                e.updatedAt, e.createdBy, e.updatedBy, p1.fullName, p2.fullName
            ) FROM Education e
            LEFT JOIN Profile p1 ON p1.id = e.createdBy
            LEFT JOIN Profile p2 ON p2.id = e.updatedBy
            WHERE e.id = :id AND e.profileId = :profileId
    """)
    Optional<EducationResponse> findDTOByIdAndProfileId(@Param("id") Long id, @Param("profileId") Long profileId);

    boolean existsByDegreeAndProfileId(DegreeEnum degree, Long profileId);

    List<Education> findByProfileId(Long profileId);

    @Query(value = """
            SELECT NEW com.portfolio.dtos.Education.EducationResponse(
                e.id, e.degree, e.institution, e.fieldOfStudy, e.startYear,
                e.location, e.endYear, e.description, e.grade, e.createdAt,
                e.updatedAt, e.createdBy, e.updatedBy, p1.fullName, p2.fullName
            ) FROM Education e
            LEFT JOIN Profile p1 ON p1.id = e.createdBy
            LEFT JOIN Profile p2 ON p2.id = e.updatedBy
            WHERE( :profileId is NULL OR e.profileId = :profileId)
            AND (:search IS NULL OR :search = ''
                OR LOWER(e.institution) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(e.fieldOfStudy) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')   
                OR LOWER(e.degree) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
    """,
            countQuery = """
            SELECT COUNT(e) FROM Education e
            WHERE (:profileId IS NULL OR e.profileId = :profileId)
            AND (:search IS NULL OR :search = ''
                OR LOWER(e.institution) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(e.fieldOfStudy) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(e.degree) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            """)
    Page<EducationResponse> findByCriteria(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable
    );

    long countByProfileId(Long profileId);

    Optional<Education> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId);
}

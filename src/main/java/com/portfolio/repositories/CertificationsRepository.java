package com.portfolio.repositories;

import com.portfolio.dtos.Certifications.CertificationResponseDTO;
import com.portfolio.entities.Certifications;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificationsRepository extends JpaRepository<Certifications, Long> {

    @Query(value = """
            SELECT NEW com.portfolio.dtos.Certifications.CertificationResponseDTO(
                c.id, c.title, c.issuer, c.credentialId, fa.path, c.status, c.order,
                c.issueDate, c.expiryDate, c.createdAt, c.updatedAt, c.createdBy,
                c.updatedBy, p1.fullName, p2.fullName
            ) FROM Certifications c
            LEFT JOIN FileAsset fa ON fa.resourceId = c.id AND fa.resourceType = 'CERTIFICATION'
            LEFT JOIN Profile p1 ON p1.id = c.createdBy 
            LEFT JOIN Profile p2 ON p2.id = c.updatedBy
            WHERE (:profileId IS NULL OR c.profileId = :profileId)
            AND (:search IS NULL OR :search = ''
            OR LOWER(c.title) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
            OR LOWER(c.issuer) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            ORDER BY c.order ASC
    """,
            countQuery = """
            SELECT COUNT(c) FROM Certifications c
            WHERE (:profileId IS NULL OR c.profileId = :profileId)
            AND (:search IS NULL OR :search = ''
            OR LOWER(c.title) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
            OR LOWER(c.issuer) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            """)
    Page<CertificationResponseDTO> findByCriteria(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable);

    @Query("""
            SELECT NEW com.portfolio.dtos.Certifications.CertificationResponseDTO(
                c.id, c.title, c.issuer, c.credentialId, fa.path, c.status, c.order,
                c.issueDate, c.expiryDate, c.createdAt, c.updatedAt, c.createdBy,
                c.updatedBy, p1.fullName, p2.fullName
            ) FROM Certifications c
            LEFT JOIN FileAsset fa ON fa.resourceId = c.id AND fa.resourceType = 'CERTIFICATION'
            LEFT JOIN Profile p1 ON p1.id = c.createdBy
            LEFT JOIN Profile p2 ON p2.id = c.updatedBy
            WHERE c.id = :id
    """)
    Optional<CertificationResponseDTO> findDTOById(@Param("id") Long id);

    boolean existsByProfileIdAndOrder(Long profileId, String order);

    boolean existsByProfileIdAndOrderAndIdNot(Long profileId, String order, Long id);

    List<Certifications> findByProfileIdAndStatusOrderByOrderAsc(Long profileId, StatusEnum statusEnum);

    long countByProfileId(Long profileId);

    Optional<Certifications> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId);
}

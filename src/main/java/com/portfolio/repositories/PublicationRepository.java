package com.portfolio.repositories;

import com.portfolio.dtos.Publication.PublicationResponseDTO;
import com.portfolio.entities.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    @Query(value = """
            SELECT NEW com.portfolio.dtos.Publication.PublicationResponseDTO(
                p.id, p.profileId, p.title, p.type, p.url, p.publisher, p.publishedDate,
                p.description, p.coAuthors, p.sortOrder,
                p.createdAt, p.updatedAt, p.createdBy, p.updatedBy, p1.fullName, p2.fullName
            ) FROM Publication p
            LEFT JOIN Profile p1 ON p1.id = p.createdBy
            LEFT JOIN Profile p2 ON p2.id = p.updatedBy
            WHERE (:profileId IS NULL OR p.profileId = :profileId)
            AND (:search IS NULL OR :search = ''
            OR LOWER(p.title) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
            OR LOWER(p.publisher) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            ORDER BY p.sortOrder ASC, p.publishedDate DESC
            """,
            countQuery = """
            SELECT COUNT(p) FROM Publication p
            WHERE (:profileId IS NULL OR p.profileId = :profileId)
            AND (:search IS NULL OR :search = ''
            OR LOWER(p.title) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
            OR LOWER(p.publisher) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            """)
    Page<PublicationResponseDTO> findByCriteria(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable);

    @Query("""
            SELECT NEW com.portfolio.dtos.Publication.PublicationResponseDTO(
                p.id, p.profileId, p.title, p.type, p.url, p.publisher, p.publishedDate,
                p.description, p.coAuthors, p.sortOrder,
                p.createdAt, p.updatedAt, p.createdBy, p.updatedBy, p1.fullName, p2.fullName
            ) FROM Publication p
            LEFT JOIN Profile p1 ON p1.id = p.createdBy
            LEFT JOIN Profile p2 ON p2.id = p.updatedBy
            WHERE p.id = :id
            """)
    Optional<PublicationResponseDTO> findDTOById(@Param("id") Long id);

    List<Publication> findByProfileIdOrderBySortOrderAscPublishedDateDesc(Long profileId);

    long countByProfileId(Long profileId);
}

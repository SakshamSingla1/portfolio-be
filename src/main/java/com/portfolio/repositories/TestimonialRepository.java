package com.portfolio.repositories;

import com.portfolio.dtos.Testimonial.TestimonialResponseDTO;
import com.portfolio.entities.Testimonial;
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
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {

    @Query(value = """
            SELECT NEW com.portfolio.dtos.Testimonial.TestimonialResponseDTO(
                t.id, t.name, t.role, t.company, t.message,
                fa.path, fa.id, t.linkedInUrl, t.order, t.status, t.createdAt, t.updatedAt
            ) FROM Testimonial t
            LEFT JOIN FileAsset fa ON fa.resourceId = t.id AND fa.resourceType = 'TESTIMONIAL'
            WHERE (:profileId IS NULL OR t.profileId = :profileId)
            AND (:search IS NULL OR :search = ''
                OR LOWER(t.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(t.company) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(t.role) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
    """,
            countQuery = """
            SELECT COUNT(t) FROM Testimonial t
            WHERE (:profileId IS NULL OR t.profileId = :profileId)
            AND (:search IS NULL OR :search = ''
                OR LOWER(t.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(t.company) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(t.role) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            """)
    Page<TestimonialResponseDTO> findByCriteria(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
            SELECT NEW com.portfolio.dtos.Testimonial.TestimonialResponseDTO(
                t.id, t.name, t.role, t.company, t.message,
                fa.path, fa.id, t.linkedInUrl, t.order, t.status, t.createdAt, t.updatedAt
            ) FROM Testimonial t
            LEFT JOIN FileAsset fa ON fa.resourceId = t.id AND fa.resourceType = 'TESTIMONIAL'
            WHERE t.id = :id
    """)
    Optional<TestimonialResponseDTO> findDTOById(@Param("id") Long id);

    Page<Testimonial> findByProfileId(Long profileId, Pageable pageable);

    boolean existsByProfileIdAndOrder(Long profileId, Integer order);

    boolean existsByProfileIdAndOrderAndIdNot(Long profileId, Integer order, Long id);

    List<Testimonial> findByProfileIdAndStatusOrderByOrderAsc(Long profileId, StatusEnum statusEnum);

    long countByProfileId(Long profileId);

    Optional<Testimonial> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId);
}

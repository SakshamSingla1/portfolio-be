package com.portfolio.repositories;

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

    @Query("SELECT t FROM Testimonial t WHERE t.profileId = :profileId AND (LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.company) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.role) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Testimonial> findByProfileIdWithSearch(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT t FROM Testimonial t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.company) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.role) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Testimonial> findBySearch(@Param("search") String search, Pageable pageable);

    Page<Testimonial> findByProfileId(Long profileId, Pageable pageable);

    boolean existsByProfileIdAndOrder(Long profileId, String order);

    boolean existsByProfileIdAndOrderAndIdNot(Long profileId, String order, Long id);

    List<Testimonial> findByProfileIdAndStatusOrderByOrderAsc(Long profileId, StatusEnum statusEnum);

    long countByProfileId(Long profileId);

    Optional<Testimonial> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId);
}

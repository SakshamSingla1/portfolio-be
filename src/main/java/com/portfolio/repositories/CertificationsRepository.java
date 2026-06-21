package com.portfolio.repositories;

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

    @Query("SELECT c FROM Certifications c WHERE c.profileId = :profileId AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.issuer) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Certifications> findByProfileIdWithSearch(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT c FROM Certifications c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.issuer) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Certifications> findBySearch(@Param("search") String search, Pageable pageable);

    Page<Certifications> findByProfileId(Long profileId, Pageable pageable);

    boolean existsByProfileIdAndOrder(Long profileId, String order);

    boolean existsByProfileIdAndOrderAndIdNot(Long profileId, String order, Long id);

    List<Certifications> findByProfileIdAndStatusOrderByOrderAsc(Long profileId, StatusEnum statusEnum);

    long countByProfileId(Long profileId);

    Optional<Certifications> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId);
}

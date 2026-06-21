package com.portfolio.repositories;

import com.portfolio.entities.ContactUs;
import com.portfolio.enums.ContactUsStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactUsRepository extends JpaRepository<ContactUs, Long> {

    @Query("SELECT c FROM ContactUs c WHERE c.profileId = :profileId AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ContactUs> findByProfileIdWithSearch(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT c FROM ContactUs c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<ContactUs> findBySearch(@Param("search") String search, Pageable pageable);

    Page<ContactUs> findByProfileId(Long profileId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE ContactUs c SET c.status = :status WHERE c.id = :id")
    void updateStatusById(@Param("id") Long id, @Param("status") ContactUsStatusEnum status);

    List<ContactUs> findTop5ByProfileIdOrderByCreatedAtDesc(Long profileId);

    long countByProfileId(Long profileId);

    long countByStatusAndProfileId(ContactUsStatusEnum status, Long profileId);

    Optional<ContactUs> findTop1ByProfileIdOrderByCreatedAtDesc(Long profileId);
}

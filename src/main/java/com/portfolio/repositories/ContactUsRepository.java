package com.portfolio.repositories;

import com.portfolio.entities.ContactUs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactUsRepository extends JpaRepository<ContactUs, Integer> {

    // ✅ Paginated search by profile, returning entities
    @Query("""
        SELECT c
        FROM ContactUs c
        WHERE c.profile.id = :profileId
          AND (:search IS NULL OR :search = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<ContactUs> findByProfileIdWithSearch(Integer profileId, String search, Pageable pageable);
}

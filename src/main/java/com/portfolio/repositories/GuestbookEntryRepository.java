package com.portfolio.repositories;

import com.portfolio.entities.GuestbookEntry;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestbookEntryRepository extends JpaRepository<GuestbookEntry, Long> {

    Page<GuestbookEntry> findByProfileIdAndStatusOrderByCreatedAtDesc(Long profileId, StatusEnum status, Pageable pageable);

    Page<GuestbookEntry> findByProfileIdOrderByCreatedAtDesc(Long profileId, Pageable pageable);
}

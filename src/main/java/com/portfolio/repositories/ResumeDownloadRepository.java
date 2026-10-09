package com.portfolio.repositories;

import com.portfolio.entities.ResumeDownload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ResumeDownloadRepository extends JpaRepository<ResumeDownload, Long> {

    long countByProfileId(Long profileId);

    long countByProfileIdAndDownloadedAtBetween(Long profileId, LocalDateTime start, LocalDateTime end);
}

package com.portfolio.repositories;

import com.portfolio.entities.PortfolioExport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PortfolioExportRepository extends JpaRepository<PortfolioExport, Long> {

    Optional<PortfolioExport> findFirstByProfileIdAndTypeAndExpiresAtAfterOrderByGeneratedAtDesc(
            Long profileId, String type, LocalDateTime now);
}

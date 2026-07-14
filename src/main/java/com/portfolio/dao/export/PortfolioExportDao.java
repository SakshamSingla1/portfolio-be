package com.portfolio.dao.export;

import com.portfolio.entities.PortfolioExport;
import com.portfolio.repositories.PortfolioExportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Slf4j
public class PortfolioExportDao {

    private final PortfolioExportRepository repository;

    public PortfolioExportDao(PortfolioExportRepository repository) {
        this.repository = repository;
    }

    public PortfolioExport save(PortfolioExport export) {
        return repository.save(export);
    }

    public Optional<PortfolioExport> findValidExport(Long profileId) {
        return repository.findFirstByProfileIdAndTypeAndExpiresAtAfterOrderByGeneratedAtDesc(
                profileId, "PDF", LocalDateTime.now());
    }
}

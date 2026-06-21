package com.portfolio.repositories;

import com.portfolio.entities.PortfolioView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PortfolioViewRepository extends JpaRepository<PortfolioView, Long> {

    long countByProfileId(Long profileId);

    long countByProfileIdAndTimestampBetween(Long profileId, LocalDateTime start, LocalDateTime end);

    List<PortfolioView> findByProfileIdAndTimestampAfter(Long profileId, LocalDateTime after);

    List<PortfolioView> findTop30ByProfileIdOrderByTimestampDesc(Long profileId);
}

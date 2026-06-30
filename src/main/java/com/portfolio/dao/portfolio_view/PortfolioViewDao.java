package com.portfolio.dao.portfolio_view;

import com.portfolio.entities.PortfolioView;
import com.portfolio.repositories.PortfolioViewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
public class PortfolioViewDao {

    private final PortfolioViewRepository portfolioViewRepository;

    public PortfolioViewDao(PortfolioViewRepository portfolioViewRepository) {
        this.portfolioViewRepository = portfolioViewRepository;
    }

    public PortfolioView save(PortfolioView portfolioView) {
        return portfolioViewRepository.save(portfolioView);
    }

    public long countByProfileId(Long profileId) {
        return portfolioViewRepository.countByProfileId(profileId);
    }

    public long countByProfileIdAndTimestampBetween(Long profileId, LocalDateTime start, LocalDateTime end) {
        return portfolioViewRepository.countByProfileIdAndTimestampBetween(profileId, start, end);
    }

    public List<PortfolioView> findByProfileIdAndTimestampAfter(Long profileId, LocalDateTime after) {
        return portfolioViewRepository.findByProfileIdAndTimestampAfter(profileId, after);
    }

    public List<PortfolioView> findTop30ByProfileIdOrderByTimestampDesc(Long profileId) {
        return portfolioViewRepository.findTop30ByProfileIdOrderByTimestampDesc(profileId);
    }
}

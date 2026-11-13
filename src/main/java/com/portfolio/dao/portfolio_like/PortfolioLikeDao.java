package com.portfolio.dao.portfolio_like;

import com.portfolio.entities.PortfolioLike;
import com.portfolio.repositories.PortfolioLikeRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PortfolioLikeDao {

    private final PortfolioLikeRepository portfolioLikeRepository;

    public PortfolioLikeDao(PortfolioLikeRepository portfolioLikeRepository) {
        this.portfolioLikeRepository = portfolioLikeRepository;
    }

    public PortfolioLike save(PortfolioLike like) {
        return portfolioLikeRepository.save(like);
    }

    public long countByProfileId(Long profileId) {
        return portfolioLikeRepository.countByProfileId(profileId);
    }

    public Optional<PortfolioLike> findByProfileIdAndVisitorId(Long profileId, String visitorId) {
        return portfolioLikeRepository.findByProfileIdAndVisitorId(profileId, visitorId);
    }

    public void deleteByProfileIdAndVisitorId(Long profileId, String visitorId) {
        portfolioLikeRepository.deleteByProfileIdAndVisitorId(profileId, visitorId);
    }
}

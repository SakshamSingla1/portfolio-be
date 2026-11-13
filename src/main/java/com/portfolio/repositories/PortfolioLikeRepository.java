package com.portfolio.repositories;

import com.portfolio.entities.PortfolioLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioLikeRepository extends JpaRepository<PortfolioLike, Long> {

    long countByProfileId(Long profileId);

    Optional<PortfolioLike> findByProfileIdAndVisitorId(Long profileId, String visitorId);

    void deleteByProfileIdAndVisitorId(Long profileId, String visitorId);
}

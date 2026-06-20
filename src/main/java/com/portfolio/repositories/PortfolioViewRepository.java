package com.portfolio.repositories;

import com.portfolio.entities.PortfolioView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PortfolioViewRepository extends MongoRepository<PortfolioView, String> {

    long countByProfileId(String profileId);

    long countByProfileIdAndTimestampBetween(String profileId, LocalDateTime start, LocalDateTime end);

    List<PortfolioView> findByProfileIdAndTimestampAfter(String profileId, LocalDateTime after);

    List<PortfolioView> findTop30ByProfileIdOrderByTimestampDesc(String profileId);
}

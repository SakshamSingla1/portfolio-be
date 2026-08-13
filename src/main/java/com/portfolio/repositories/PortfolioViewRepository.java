package com.portfolio.repositories;

import com.portfolio.entities.PortfolioView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PortfolioViewRepository extends JpaRepository<PortfolioView, Long> {

    long countByProfileId(Long profileId);

    long countByProfileIdAndTimestampBetween(Long profileId, LocalDateTime start, LocalDateTime end);

    List<PortfolioView> findByProfileIdAndTimestampAfter(Long profileId, LocalDateTime after);

    List<PortfolioView> findTop30ByProfileIdOrderByTimestampDesc(Long profileId);

    // Day-bucketed counts for the views heatmap — grouped in SQL rather than pulling
    // raw rows into Java, since a ~90-day window on a high-traffic profile could be
    // thousands of rows we'd otherwise have to bucket ourselves.
    @Query(value = """
            SELECT CAST(timestamp AS DATE) AS day, COUNT(*) AS cnt
            FROM portfolio_views
            WHERE profile_id = :profileId AND timestamp >= :since
            GROUP BY CAST(timestamp AS DATE)
            ORDER BY day ASC
            """, nativeQuery = true)
    List<Object[]> getDailyViewCountsSince(@Param("profileId") Long profileId, @Param("since") LocalDateTime since);
}

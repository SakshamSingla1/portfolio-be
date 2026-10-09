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

    long countDistinctSessionIdByProfileIdAndTimestampBetween(Long profileId, LocalDateTime start, LocalDateTime end);

    List<PortfolioView> findByProfileIdAndTimestampAfter(Long profileId, LocalDateTime after);

    List<PortfolioView> findTop30ByProfileIdOrderByTimestampDesc(Long profileId);

    // Analytics Dashboard's range-aware "recent views" list — LIMIT pushed into the query
    // (via findTop200...) rather than fetching the whole range and sorting/limiting in Java.
    List<PortfolioView> findTop200ByProfileIdAndTimestampBetweenOrderByTimestampDesc(
            Long profileId, LocalDateTime start, LocalDateTime end);

    // Day-bucketed counts, grouped in SQL rather than pulling raw rows into Java — used for
    // both the fixed 90-day heatmap (since=90 days ago, until=now) and the Analytics
    // Dashboard's range-aware trend chart (since/until = the selected range).
    @Query(value = """
            SELECT CAST(timestamp AS DATE) AS day, COUNT(*) AS cnt
            FROM portfolio_views
            WHERE profile_id = :profileId AND timestamp >= :since AND timestamp <= :until
            GROUP BY CAST(timestamp AS DATE)
            ORDER BY day ASC
            """, nativeQuery = true)
    List<Object[]> getDailyViewCountsBetween(@Param("profileId") Long profileId,
                                              @Param("since") LocalDateTime since,
                                              @Param("until") LocalDateTime until);

    @Query(value = """
            SELECT COALESCE(device, 'DESKTOP') AS k, COUNT(*) AS cnt
            FROM portfolio_views
            WHERE profile_id = :profileId AND timestamp >= :start AND timestamp <= :end
            GROUP BY COALESCE(device, 'DESKTOP')
            """, nativeQuery = true)
    List<Object[]> getDeviceBreakdownBetween(@Param("profileId") Long profileId,
                                              @Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    @Query(value = """
            SELECT browser AS k, COUNT(*) AS cnt
            FROM portfolio_views
            WHERE profile_id = :profileId AND timestamp >= :start AND timestamp <= :end
                  AND browser IS NOT NULL AND browser <> ''
            GROUP BY browser
            """, nativeQuery = true)
    List<Object[]> getBrowserBreakdownBetween(@Param("profileId") Long profileId,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    @Query(value = """
            SELECT country AS k, COUNT(*) AS cnt
            FROM portfolio_views
            WHERE profile_id = :profileId AND timestamp >= :start AND timestamp <= :end
                  AND country IS NOT NULL AND country <> ''
            GROUP BY country
            """, nativeQuery = true)
    List<Object[]> getLocationBreakdownBetween(@Param("profileId") Long profileId,
                                                @Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);

    // referrer_domain is populated at write time (trackView) directly from the cleaned
    // referrer; grouping on it in SQL avoids re-deriving domains in Java over a potentially
    // large row set. Rows predating that column's population fall back to 'Direct'.
    @Query(value = """
            SELECT COALESCE(referrer_domain, 'Direct') AS k, COUNT(*) AS cnt
            FROM portfolio_views
            WHERE profile_id = :profileId AND timestamp >= :start AND timestamp <= :end
            GROUP BY COALESCE(referrer_domain, 'Direct')
            """, nativeQuery = true)
    List<Object[]> getReferrerBreakdownBetween(@Param("profileId") Long profileId,
                                                @Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);
}

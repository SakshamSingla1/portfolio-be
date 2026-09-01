-- portfolio_views is an unbounded-growth analytics table (a row per visit). It's
-- always queried by profile_id together with a timestamp predicate or ORDER BY:
--   countByProfileIdAndTimestampBetween, findByProfileIdAndTimestampAfter,
--   findTop30ByProfileIdOrderByTimestampDesc, getDailyViewCountsSince (native, GROUP BY date).
-- Only single-column indexes on profile_id and timestamp existed, forcing a bitmap-AND
-- (or a separate sort) instead of a single ordered index scan. A composite index serves
-- all of the above directly, including the ORDER BY ... DESC LIMIT 30 lookup.
CREATE INDEX IF NOT EXISTS idx_portfolio_views_profile_timestamp ON portfolio_views(profile_id, timestamp DESC);

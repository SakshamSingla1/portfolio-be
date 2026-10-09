-- resume_downloads is queried by profile_id together with a downloadedAt range for the
-- new ranged/comparison analytics endpoint (countByProfileIdAndDownloadedAtBetween).
-- Only a single-column index on profile_id existed (V19), forcing a filter scan over
-- that profile's rows for the date bound. Mirrors idx_portfolio_views_profile_timestamp (V64).
CREATE INDEX IF NOT EXISTS idx_resume_downloads_profile_downloaded_at ON resume_downloads(profile_id, downloaded_at DESC);

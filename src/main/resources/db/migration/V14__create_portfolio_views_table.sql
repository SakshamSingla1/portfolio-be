-- ============================================================
-- Portfolio Views: analytics — who visited which portfolio
-- ============================================================
CREATE TABLE portfolio_views (
    id           BIGSERIAL    PRIMARY KEY,
    profile_id   BIGINT,

    session_id   VARCHAR(255),
    device       VARCHAR(255),
    referrer     TEXT,
    timestamp    TIMESTAMP(6),

    -- visitor metadata
    browser      VARCHAR(255),
    os           VARCHAR(255),
    language     VARCHAR(255),
    timezone     VARCHAR(255),
    country      VARCHAR(255),
    city         VARCHAR(255),
    country_code VARCHAR(10),

    created_at   TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT       NOT NULL DEFAULT 1,
    updated_by   BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_portfolio_views_profile_id ON portfolio_views(profile_id);
CREATE INDEX IF NOT EXISTS idx_portfolio_views_timestamp  ON portfolio_views(timestamp);

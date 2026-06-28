-- ============================================================
-- Portfolio Views: analytics — who visited which portfolio
-- ============================================================
CREATE TABLE portfolio_views (
    id          BIGSERIAL    PRIMARY KEY,
    profile_id  BIGINT       NOT NULL,

    ip_address  VARCHAR(255),
    user_agent  TEXT,
    referrer    TEXT,
    country     VARCHAR(255),
    city        VARCHAR(255),
    device_type VARCHAR(255),

    viewed_at   TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL DEFAULT 1,
    updated_by  BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_portfolio_views_profile_id ON portfolio_views(profile_id);
CREATE INDEX IF NOT EXISTS idx_portfolio_views_viewed_at  ON portfolio_views(viewed_at);

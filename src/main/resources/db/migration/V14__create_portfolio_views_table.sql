CREATE TABLE portfolio_views (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    updated_by BIGINT,
    timestamp TIMESTAMP(6),
    device VARCHAR(255),
    profile_id BIGINT,
    referrer VARCHAR(255),
    session_id VARCHAR(255),
    browser      VARCHAR(50),
    os           VARCHAR(50),
    language     VARCHAR(50),
    timezone     VARCHAR(100),
    country      VARCHAR(100),
    city         VARCHAR(100),
    country_code VARCHAR(10)
);

CREATE INDEX IF NOT EXISTS idx_portfolio_views_profile_id ON portfolio_views(profile_id);
CREATE INDEX IF NOT EXISTS idx_portfolio_views_timestamp ON portfolio_views(timestamp);

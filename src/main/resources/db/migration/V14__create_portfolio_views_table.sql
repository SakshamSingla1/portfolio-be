CREATE TABLE portfolio_views (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP(6),
    device VARCHAR(255),
    profile_id BIGINT,
    referrer VARCHAR(255),
    session_id VARCHAR(255)
);

CREATE TABLE portfolio_exports (
    id             BIGSERIAL PRIMARY KEY,
    profile_id     BIGINT        NOT NULL REFERENCES profiles(id),
    type           VARCHAR(10)   NOT NULL DEFAULT 'PDF',
    file_url       VARCHAR(1000),
    generated_at   TIMESTAMP     NOT NULL,
    expires_at     TIMESTAMP     NOT NULL,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP
);
CREATE INDEX idx_portfolio_exports_profile_id ON portfolio_exports(profile_id);

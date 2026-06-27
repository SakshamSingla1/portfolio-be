CREATE TABLE logos (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    name VARCHAR(255),
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_logos_name ON logos(name);

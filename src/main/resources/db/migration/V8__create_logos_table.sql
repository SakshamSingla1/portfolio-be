-- ============================================================
-- Logos: skill/technology icon library
-- ============================================================
CREATE TABLE logos (
    id         BIGSERIAL    PRIMARY KEY,

    name       VARCHAR(255),

    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT       NOT NULL DEFAULT 1,
    updated_by BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_logos_name ON logos(name);

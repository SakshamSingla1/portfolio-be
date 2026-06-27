CREATE TABLE color_themes (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    palette TEXT,
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    theme_name VARCHAR(255),
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_color_themes_status ON color_themes(status);

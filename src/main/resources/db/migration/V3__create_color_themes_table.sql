CREATE TABLE color_themes (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    palette TEXT,
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    theme_name VARCHAR(255),
    updated_by VARCHAR(255)
);

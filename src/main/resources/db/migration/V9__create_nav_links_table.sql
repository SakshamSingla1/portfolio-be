CREATE TABLE nav_links (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    icon VARCHAR(255),
    name VARCHAR(255),
    nav_group VARCHAR(255),
    nav_index VARCHAR(255),
    path VARCHAR(255),
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    updated_by VARCHAR(255)
);

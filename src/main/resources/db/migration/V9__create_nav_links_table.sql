CREATE TABLE nav_links (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    icon VARCHAR(255),
    name VARCHAR(255),
    nav_group VARCHAR(255),
    nav_index VARCHAR(255),
    path VARCHAR(255),
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_nav_links_status ON nav_links(status);
CREATE INDEX IF NOT EXISTS idx_nav_links_nav_group ON nav_links(nav_group);

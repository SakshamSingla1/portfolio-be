CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    description TEXT,
    name VARCHAR(255) UNIQUE,
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'DELETED')),
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_roles_status ON roles(status);

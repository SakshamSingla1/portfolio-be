CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    name VARCHAR(255) UNIQUE,
    updated_by BIGINT
);

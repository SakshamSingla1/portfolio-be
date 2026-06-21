CREATE TABLE logos (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    name VARCHAR(255),
    updated_by VARCHAR(255),
    url VARCHAR(255)
);

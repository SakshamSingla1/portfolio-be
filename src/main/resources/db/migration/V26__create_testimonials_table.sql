CREATE TABLE testimonials (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    company VARCHAR(255),
    created_by BIGINT,
    linked_in_url VARCHAR(255),
    message TEXT,
    name VARCHAR(255),
    profile_id BIGINT,
    role VARCHAR(255),
    sort_order VARCHAR(255),
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_testimonials_profile_id ON testimonials(profile_id);

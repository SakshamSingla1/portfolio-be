CREATE TABLE achievements (
    id BIGSERIAL PRIMARY KEY,
    achieved_at DATE,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    description TEXT,
    issuer VARCHAR(255),
    profile_id BIGINT,
    proof_public_id VARCHAR(255),
    proof_url VARCHAR(255),
    sort_order VARCHAR(255),
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    title VARCHAR(255),
    updated_by VARCHAR(255)
);

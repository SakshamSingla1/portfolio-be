CREATE TABLE resumes (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    file_name VARCHAR(255),
    file_url VARCHAR(255),
    profile_id BIGINT,
    public_id VARCHAR(255),
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    updated_by VARCHAR(255)
);

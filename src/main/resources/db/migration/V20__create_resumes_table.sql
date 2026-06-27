CREATE TABLE resumes (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    profile_id BIGINT,
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_resumes_profile_id ON resumes(profile_id);

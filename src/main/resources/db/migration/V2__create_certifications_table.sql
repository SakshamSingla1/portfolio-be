CREATE TABLE certifications (
    id BIGSERIAL PRIMARY KEY,
    expiry_date DATE,
    issue_date DATE,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    credential_id VARCHAR(255),
    issuer VARCHAR(255),
    profile_id BIGINT,
    sort_order VARCHAR(255),
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    title VARCHAR(255),
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_certifications_profile_id ON certifications(profile_id);

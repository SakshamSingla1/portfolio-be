CREATE TABLE achievements (
    id BIGSERIAL PRIMARY KEY,
    achieved_at DATE,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    description TEXT,
    issuer VARCHAR(255),
    profile_id BIGINT,
    sort_order VARCHAR(255),
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    title VARCHAR(255),
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_achievements_profile_id ON achievements(profile_id);

-- ============================================================
-- Resumes: uploaded resume files per profile
-- ============================================================
CREATE TABLE resumes (
    id           BIGSERIAL    PRIMARY KEY,
    profile_id   BIGINT       NOT NULL,

    file_name    VARCHAR(255),
    file_url     TEXT,
    public_id    TEXT,
    mime_type    VARCHAR(255),
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,

    status       VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                              CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED')),

    created_at   TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT       NOT NULL DEFAULT 1,
    updated_by   BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_resumes_profile_id ON resumes(profile_id);
CREATE INDEX IF NOT EXISTS idx_resumes_status     ON resumes(status);

-- ============================================================
-- Certifications: professional certificates and credentials
-- ============================================================
CREATE TABLE certifications (
    id            BIGSERIAL    PRIMARY KEY,
    profile_id    BIGINT       NOT NULL,

    title         VARCHAR(255),
    issuer        VARCHAR(255),
    credential_id VARCHAR(255),
    issue_date    DATE,
    expiry_date   DATE,
    sort_order    VARCHAR(255),

    status        VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                               CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED')),

    created_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT       NOT NULL DEFAULT 1,
    updated_by    BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_certifications_profile_id ON certifications(profile_id);
CREATE INDEX IF NOT EXISTS idx_certifications_status     ON certifications(status);

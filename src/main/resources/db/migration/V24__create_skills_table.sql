-- ============================================================
-- Skills: technical and soft skills listed on the portfolio
-- ============================================================
CREATE TABLE skills (
    id            BIGSERIAL    PRIMARY KEY,
    profile_id    BIGINT       NOT NULL,

    name          VARCHAR(255),
    category      VARCHAR(255),
    proficiency   VARCHAR(255) CHECK (proficiency IN ('BEGINNER','INTERMEDIATE','ADVANCED','EXPERT')),
    logo_id       BIGINT,
    sort_order    INTEGER      NOT NULL DEFAULT 0,

    status        VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                               CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED')),

    created_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT       NOT NULL DEFAULT 1,
    updated_by    BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_skills_profile_id ON skills(profile_id);
CREATE INDEX IF NOT EXISTS idx_skills_status     ON skills(status);

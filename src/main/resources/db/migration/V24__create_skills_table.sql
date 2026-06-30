-- ============================================================
-- Skills: technical and soft skills listed on the portfolio
-- ============================================================
CREATE TABLE skills (
    id            BIGSERIAL    PRIMARY KEY,
    profile_id    BIGINT       NOT NULL,

    logo          TEXT,
    logo_id       BIGINT,
    logo_name     VARCHAR(255),
    level         VARCHAR(255),
    category      VARCHAR(255),

    created_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT       NOT NULL DEFAULT 1,
    updated_by    BIGINT       NOT NULL DEFAULT 1,

    CONSTRAINT uk_skill_profile_logo UNIQUE (profile_id, logo_id)
);

CREATE INDEX IF NOT EXISTS idx_skills_profile_id ON skills(profile_id);

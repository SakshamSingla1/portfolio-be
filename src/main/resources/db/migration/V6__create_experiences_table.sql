-- ============================================================
-- Experiences: work history and professional roles
-- ============================================================
CREATE TABLE experiences (
    id                BIGSERIAL    PRIMARY KEY,
    profile_id        BIGINT       NOT NULL,

    job_title         VARCHAR(255),
    company_name      VARCHAR(255),
    location          VARCHAR(255),
    employment_status VARCHAR(255) CHECK (employment_status IN (
                          'CURRENT','PREVIOUS','INTERNSHIP','CONTRACT','FREELANCE'
                      )),
    description       TEXT,
    skill_ids         TEXT,
    start_date        DATE,
    end_date          DATE,

    created_at        TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by        BIGINT       NOT NULL DEFAULT 1,
    updated_by        BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_experiences_profile_id ON experiences(profile_id);

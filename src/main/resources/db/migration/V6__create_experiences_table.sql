CREATE TABLE experiences (
    id BIGSERIAL PRIMARY KEY,
    end_date DATE,
    start_date DATE,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    company_name VARCHAR(255),
    created_by BIGINT,
    description TEXT,
    employment_status VARCHAR(255) CHECK (employment_status IN ('CURRENT', 'PREVIOUS', 'INTERNSHIP', 'CONTRACT', 'FREELANCE')),
    job_title VARCHAR(255),
    location VARCHAR(255),
    profile_id BIGINT,
    skill_ids TEXT,
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_experiences_profile_id ON experiences(profile_id);

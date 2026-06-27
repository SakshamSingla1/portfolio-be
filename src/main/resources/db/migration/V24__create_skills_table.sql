CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    category VARCHAR(255) CHECK (category IN ('FRONTEND', 'BACKEND', 'PROGRAMMING', 'TOOL', 'DATABASE', 'DEVOPS', 'TESTING', 'MOBILE', 'CLOUD', 'SECURITY', 'DATA_SCIENCE', 'UI_UX', 'SOFT_SKILLS', 'OTHER')),
    created_by BIGINT,
    level VARCHAR(255) CHECK (level IN ('Beginner', 'Intermediate', 'Advanced', 'Expert')),
    logo TEXT,
    logo_id BIGINT,
    logo_name VARCHAR(255),
    profile_id BIGINT,
    updated_by BIGINT,
    CONSTRAINT uk_skill_profile_logo UNIQUE (profile_id, logo_id)
);

CREATE INDEX IF NOT EXISTS idx_skills_profile_id ON skills(profile_id);
CREATE INDEX IF NOT EXISTS idx_skills_logo_id ON skills(logo_id);
CREATE INDEX IF NOT EXISTS idx_skills_category ON skills(category);

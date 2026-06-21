CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    category VARCHAR(255) CHECK (category IN ('FRONTEND', 'BACKEND', 'PROGRAMMING', 'TOOL', 'DATABASE', 'DEVOPS', 'TESTING', 'MOBILE', 'CLOUD', 'SECURITY', 'DATA_SCIENCE', 'UI_UX', 'SOFT_SKILLS', 'OTHER')),
    created_by VARCHAR(255),
    level VARCHAR(255) CHECK (level IN ('Beginner', 'Intermediate', 'Advanced', 'Expert')),
    logo TEXT,
    logo_id BIGINT,
    logo_name VARCHAR(255),
    profile_id BIGINT,
    updated_by VARCHAR(255),
    CONSTRAINT uk_skill_profile_logo UNIQUE (profile_id, logo_id)
);

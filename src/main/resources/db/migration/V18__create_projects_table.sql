CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    project_end_date DATE,
    project_start_date DATE,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    github_repositories TEXT,
    profile_id BIGINT,
    project_description TEXT,
    project_link VARCHAR(255),
    project_name VARCHAR(255),
    skill_ids TEXT,
    updated_by BIGINT,
    work_status VARCHAR(255) CHECK (work_status IN ('CURRENT', 'COMPLETED'))
);

CREATE INDEX IF NOT EXISTS idx_projects_profile_id ON projects(profile_id);

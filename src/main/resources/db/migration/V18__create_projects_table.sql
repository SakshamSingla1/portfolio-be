CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    project_end_date DATE,
    project_start_date DATE,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by VARCHAR(255),
    github_repositories TEXT,
    profile_id BIGINT,
    project_description TEXT,
    project_link VARCHAR(255),
    project_name VARCHAR(255),
    skill_ids TEXT,
    updated_by VARCHAR(255),
    work_status VARCHAR(255) CHECK (work_status IN ('CURRENT', 'COMPLETED'))
);

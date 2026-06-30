-- ============================================================
-- Projects: portfolio project showcase entries
-- ============================================================
CREATE TABLE projects (
    id                  BIGSERIAL    PRIMARY KEY,
    profile_id          BIGINT       NOT NULL,

    project_name        VARCHAR(255),
    project_description TEXT,
    github_repositories TEXT,
    project_link        VARCHAR(255),
    project_start_date  DATE,
    project_end_date    DATE,
    work_status         VARCHAR(255),
    skill_ids           TEXT,

    created_at          TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          BIGINT       NOT NULL DEFAULT 1,
    updated_by          BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_projects_profile_id ON projects(profile_id);

-- ============================================================
-- Projects: portfolio project showcase entries
-- ============================================================
CREATE TABLE projects (
    id           BIGSERIAL    PRIMARY KEY,
    profile_id   BIGINT       NOT NULL,

    title        VARCHAR(255),
    description  TEXT,
    tech_stack   TEXT,
    project_url  TEXT,
    github_url   TEXT,
    sort_order   INTEGER      NOT NULL DEFAULT 0,

    status       VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                              CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED')),

    created_at   TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT       NOT NULL DEFAULT 1,
    updated_by   BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_projects_profile_id ON projects(profile_id);
CREATE INDEX IF NOT EXISTS idx_projects_status     ON projects(status);

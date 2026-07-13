CREATE TABLE github_integrations (
    id                  BIGSERIAL PRIMARY KEY,
    profile_id          BIGINT NOT NULL UNIQUE REFERENCES profiles(id),
    github_username     VARCHAR(100) NOT NULL,
    access_token        TEXT NOT NULL,
    token_scope         VARCHAR(200),
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    last_synced_at      TIMESTAMP,
    cached_public_repos INT NOT NULL DEFAULT 0,
    cached_followers    INT NOT NULL DEFAULT 0,
    cached_total_stars  INT NOT NULL DEFAULT 0,
    cached_external_prs INT,
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP
);

CREATE TABLE github_repos (
    id              BIGSERIAL PRIMARY KEY,
    integration_id  BIGINT NOT NULL REFERENCES github_integrations(id) ON DELETE CASCADE,
    github_id       BIGINT NOT NULL,
    name            VARCHAR(100) NOT NULL,
    full_name       VARCHAR(200),
    description     TEXT,
    url             VARCHAR(500),
    homepage        VARCHAR(500),
    language        VARCHAR(50),
    stars           INT NOT NULL DEFAULT 0,
    forks           INT NOT NULL DEFAULT 0,
    is_pinned       BOOLEAN NOT NULL DEFAULT FALSE,
    is_visible      BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order      INT NOT NULL DEFAULT 0,
    synced_at       TIMESTAMP,
    UNIQUE (integration_id, github_id)
);

CREATE INDEX idx_github_repos_integration_id ON github_repos(integration_id);

INSERT INTO nav_links (nav_index, name, path, icon, nav_group, status) VALUES
('26', 'GITHUB_INTEGRATION', '/github-integration', 'GITHUB_INTEGRATION', 'CONFIGURATION', 'ACTIVE');

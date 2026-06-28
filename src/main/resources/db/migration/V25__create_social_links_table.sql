-- ============================================================
-- Social Links: external profile links (GitHub, LinkedIn, PORTFOLIO, etc.)
-- ============================================================
CREATE TABLE social_links (
    id         BIGSERIAL    PRIMARY KEY,
    profile_id BIGINT       NOT NULL,

    platform   VARCHAR(255) CHECK (platform IN (
                   'GITHUB','LINKEDIN','TWITTER','INSTAGRAM','FACEBOOK','YOUTUBE',
                   'DRIBBBLE','BEHANCE','MEDIUM','DEV_TO','HASHNODE','STACKOVERFLOW',
                   'PORTFOLIO','WEBSITE','OTHER'
               )),
    url        TEXT,
    sort_order VARCHAR(255),

    status     VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                            CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED')),

    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT       NOT NULL DEFAULT 1,
    updated_by BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_social_links_profile_id ON social_links(profile_id);
CREATE INDEX IF NOT EXISTS idx_social_links_platform   ON social_links(platform);

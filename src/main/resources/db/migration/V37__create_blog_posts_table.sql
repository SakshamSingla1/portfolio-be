-- ============================================================
-- Blog Posts: articles authored by a portfolio profile
-- Cover images stored in file_assets (resource_type = 'BLOG_POST')
-- slug is unique per profile — two users may share the same slug
-- ============================================================
CREATE TABLE blog_posts (
    id             BIGSERIAL    PRIMARY KEY,
    profile_id     BIGINT       NOT NULL,

    title          VARCHAR(255) NOT NULL,
    slug           VARCHAR(255) NOT NULL,
    content        TEXT,
    excerpt        VARCHAR(500),

    status         VARCHAR(20)  NOT NULL DEFAULT 'DRAFT'
                                CHECK (status IN ('DRAFT','PUBLISHED','ARCHIVED')),

    published_at   TIMESTAMP(6),
    view_count     INT          NOT NULL DEFAULT 0,
    read_time_mins INT,

    created_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT       NOT NULL DEFAULT 1,
    updated_by     BIGINT       NOT NULL DEFAULT 1,

    CONSTRAINT blog_posts_profile_slug_unique UNIQUE (profile_id, slug)
);

CREATE INDEX IF NOT EXISTS idx_blog_posts_profile_id   ON blog_posts (profile_id);
CREATE INDEX IF NOT EXISTS idx_blog_posts_status       ON blog_posts (status);
CREATE INDEX IF NOT EXISTS idx_blog_posts_published_at ON blog_posts (published_at DESC);

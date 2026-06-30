-- ============================================================
-- SEO Meta: per-profile search engine optimisation settings
-- ============================================================
CREATE TABLE seo_meta (
    id             BIGSERIAL    PRIMARY KEY,
    profile_id     BIGINT       NOT NULL,

    page_key       VARCHAR(255),
    title          VARCHAR(255),
    description    TEXT,
    keywords       TEXT,
    og_title       VARCHAR(255),
    og_description TEXT,
    og_image_url   TEXT,
    canonical_url  TEXT,

    indexable      BOOLEAN      NOT NULL DEFAULT TRUE,
    follow_links   BOOLEAN      NOT NULL DEFAULT TRUE,

    status         VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                                CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED')),

    created_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT       NOT NULL DEFAULT 1,
    updated_by     BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_seo_meta_profile_id ON seo_meta(profile_id);

-- ============================================================
-- SEO Meta: per-profile search engine optimisation settings
-- ============================================================
CREATE TABLE seo_meta (
    id              BIGSERIAL    PRIMARY KEY,
    profile_id      BIGINT       NOT NULL,

    meta_title      VARCHAR(255),
    meta_description TEXT,
    meta_keywords   TEXT,
    og_image        TEXT,
    canonical_url   TEXT,

    follow_links    BOOLEAN      NOT NULL DEFAULT TRUE,
    indexable       BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at      TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT       NOT NULL DEFAULT 1,
    updated_by      BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_seo_meta_profile_id ON seo_meta(profile_id);

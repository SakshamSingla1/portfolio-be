CREATE TABLE seo_meta (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    updated_by BIGINT,
    follow_links BOOLEAN,
    indexable BOOLEAN,

    canonical_url VARCHAR(255),
    description TEXT,
    keywords TEXT,
    og_description TEXT,
    og_image_url VARCHAR(255),
    og_title VARCHAR(255),
    page_key VARCHAR(255) CHECK (page_key IN ('HOME', 'EDUCATION')),
    profile_id BIGINT,
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    title VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_seo_meta_profile_id ON seo_meta(profile_id);
CREATE INDEX IF NOT EXISTS idx_seo_meta_page_key ON seo_meta(page_key);

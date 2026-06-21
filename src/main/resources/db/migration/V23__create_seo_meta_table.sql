CREATE TABLE seo_meta (
    id BIGSERIAL PRIMARY KEY,
    follow_links BOOLEAN,
    indexable BOOLEAN,
    updated_at TIMESTAMP(6),
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

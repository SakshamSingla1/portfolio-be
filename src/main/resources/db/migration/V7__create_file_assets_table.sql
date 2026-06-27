CREATE TABLE file_assets (
    id BIGSERIAL PRIMARY KEY,
    is_primary BOOLEAN,
    sort_order INTEGER NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    validity_from TIMESTAMP(6),
    validity_to TIMESTAMP(6),
    created_by BIGINT,
    updated_by BIGINT,
    creator_name VARCHAR(255),
    location VARCHAR(255),
    meta_data TEXT,
    mime_type VARCHAR(255),
    path TEXT,
    platform VARCHAR(255),
    public_id TEXT,
    resource_id VARCHAR(255),
    resource_type VARCHAR(255) CHECK (resource_type IN ('PROFILE', 'PROJECT', 'ACHIEVEMENT', 'TESTIMONIAL', 'CERTIFICATION', 'PLATFORM', 'LOGO', 'RESUME', 'BANNER'))
);

CREATE INDEX IF NOT EXISTS idx_file_assets_resource ON file_assets(resource_id, resource_type);
CREATE INDEX IF NOT EXISTS idx_file_assets_public_id ON file_assets(public_id);
CREATE INDEX IF NOT EXISTS idx_file_assets_path ON file_assets(path);

CREATE TABLE file_assets (
    id BIGSERIAL PRIMARY KEY,
    is_primary BOOLEAN,
    sort_order INTEGER NOT NULL,
    created_at TIMESTAMP(6),
    validity_from TIMESTAMP(6),
    validity_to TIMESTAMP(6),
    created_by VARCHAR(255),
    creator_name VARCHAR(255),
    location VARCHAR(255),
    meta_data TEXT,
    mime_type VARCHAR(255),
    path VARCHAR(255),
    platform VARCHAR(255),
    public_id VARCHAR(255),
    resource_id VARCHAR(255),
    resource_type VARCHAR(255) CHECK (resource_type IN ('PROFILE', 'PROJECT', 'ACHIEVEMENT', 'TESTIMONIAL', 'CERTIFICATION', 'PLATFORM', 'LOGO', 'RESUME', 'BANNER'))
);

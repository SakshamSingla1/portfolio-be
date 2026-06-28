-- ============================================================
-- File Assets: Cloudinary uploads (images, banners, resumes)
-- ============================================================
CREATE TABLE file_assets (
    id            BIGSERIAL    PRIMARY KEY,
    resource_id   VARCHAR(255),
    resource_type VARCHAR(255) CHECK (resource_type IN (
                      'PROFILE','PROJECT','ACHIEVEMENT','TESTIMONIAL',
                      'CERTIFICATION','PLATFORM','LOGO','RESUME','BANNER'
                  )),

    path          TEXT,
    public_id     TEXT,
    platform      VARCHAR(255),
    mime_type     VARCHAR(255),
    meta_data     TEXT,
    location      VARCHAR(255),
    creator_name  VARCHAR(255),

    is_primary    BOOLEAN      NOT NULL DEFAULT FALSE,
    sort_order    INTEGER      NOT NULL DEFAULT 0,

    validity_from TIMESTAMP(6),
    validity_to   TIMESTAMP(6),

    created_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT       NOT NULL DEFAULT 1,
    updated_by    BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_file_assets_resource  ON file_assets(resource_id, resource_type);
CREATE INDEX IF NOT EXISTS idx_file_assets_public_id ON file_assets(public_id);
CREATE INDEX IF NOT EXISTS idx_file_assets_path      ON file_assets(path);

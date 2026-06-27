CREATE TABLE profile_theme_mapping (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    profile_id BIGINT UNIQUE,
    theme_id BIGINT,
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_profile_theme_mapping_theme_id ON profile_theme_mapping(theme_id);

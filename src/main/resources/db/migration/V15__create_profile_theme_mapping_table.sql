-- ============================================================
-- Profile Theme Mapping: which color theme each profile uses
-- ============================================================
CREATE TABLE profile_theme_mapping (
    id         BIGSERIAL PRIMARY KEY,
    profile_id BIGINT    NOT NULL,
    theme_id   BIGINT    NOT NULL,

    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT       NOT NULL DEFAULT 1,
    updated_by BIGINT       NOT NULL DEFAULT 1,

    CONSTRAINT uq_profile_theme UNIQUE (profile_id)
);

CREATE INDEX IF NOT EXISTS idx_profile_theme_mapping_profile_id ON profile_theme_mapping(profile_id);
CREATE INDEX IF NOT EXISTS idx_profile_theme_mapping_theme_id   ON profile_theme_mapping(theme_id);

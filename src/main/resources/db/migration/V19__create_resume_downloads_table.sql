-- ============================================================
-- Resume Downloads: tracks each time a visitor downloads a resume
-- ============================================================
CREATE TABLE resume_downloads (
    id           BIGSERIAL    PRIMARY KEY,
    profile_id   BIGINT       NOT NULL,
    resume_id    BIGINT,

    ip_address   VARCHAR(255),
    user_agent   TEXT,
    country      VARCHAR(255),
    downloaded_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_at   TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT       NOT NULL DEFAULT 1,
    updated_by   BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_resume_downloads_profile_id ON resume_downloads(profile_id);

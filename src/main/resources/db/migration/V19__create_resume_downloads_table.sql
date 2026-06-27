CREATE TABLE resume_downloads (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    updated_by BIGINT,
    downloaded_at TIMESTAMP(6),
    profile_id BIGINT,
    resume_id BIGINT
);

CREATE INDEX IF NOT EXISTS idx_resume_downloads_profile_id ON resume_downloads(profile_id);
CREATE INDEX IF NOT EXISTS idx_resume_downloads_resume_id ON resume_downloads(resume_id);

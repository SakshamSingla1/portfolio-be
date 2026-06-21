CREATE TABLE resume_downloads (
    id BIGSERIAL PRIMARY KEY,
    downloaded_at TIMESTAMP(6),
    profile_id BIGINT,
    resume_id BIGINT
);

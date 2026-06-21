CREATE TABLE project_images (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT,
    project_id BIGINT,
    public_id VARCHAR(255),
    url VARCHAR(255)
);

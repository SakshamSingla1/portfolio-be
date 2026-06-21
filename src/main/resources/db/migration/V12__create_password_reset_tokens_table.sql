CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    expiry_date TIMESTAMP(6),
    profile_id BIGINT,
    token VARCHAR(255) UNIQUE
);

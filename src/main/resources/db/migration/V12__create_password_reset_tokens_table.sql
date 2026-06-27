CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    updated_by BIGINT,
    expiry_date TIMESTAMP(6),
    profile_id BIGINT,
    token VARCHAR(255) UNIQUE
);

CREATE INDEX IF NOT EXISTS idx_password_reset_profile_id ON password_reset_tokens(profile_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_expiry_date ON password_reset_tokens(expiry_date);

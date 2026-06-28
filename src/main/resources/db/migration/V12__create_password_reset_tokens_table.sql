-- ============================================================
-- Password Reset Tokens: time-limited tokens for password resets
-- ============================================================
CREATE TABLE password_reset_tokens (
    id          BIGSERIAL    PRIMARY KEY,
    profile_id  BIGINT       NOT NULL,

    token       VARCHAR(512) NOT NULL UNIQUE,
    expiry_date TIMESTAMP    NOT NULL,

    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL DEFAULT 1,
    updated_by  BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_profile_id ON password_reset_tokens(profile_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_token      ON password_reset_tokens(token);

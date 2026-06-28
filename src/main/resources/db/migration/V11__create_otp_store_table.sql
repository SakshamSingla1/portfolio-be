-- ============================================================
-- OTP Store: one-time passwords for email/phone verification
-- ============================================================
CREATE TABLE otp_store (
    id          BIGSERIAL    PRIMARY KEY,
    profile_id  BIGINT       NOT NULL,

    otp         VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP    NOT NULL,

    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL DEFAULT 1,
    updated_by  BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_otp_store_profile_id ON otp_store(profile_id);

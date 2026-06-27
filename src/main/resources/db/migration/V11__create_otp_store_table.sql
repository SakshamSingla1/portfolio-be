CREATE TABLE otp_store (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    updated_by BIGINT,
    expiry_date TIMESTAMP(6),
    otp VARCHAR(255),
    profile_id BIGINT
);

CREATE INDEX IF NOT EXISTS idx_otp_store_profile_id ON otp_store(profile_id);
CREATE INDEX IF NOT EXISTS idx_otp_store_expiry_date ON otp_store(expiry_date);

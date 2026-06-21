CREATE TABLE otp_store (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    expiry_date TIMESTAMP(6),
    otp VARCHAR(255),
    profile_id BIGINT
);

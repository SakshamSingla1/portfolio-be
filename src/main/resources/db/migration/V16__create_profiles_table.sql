CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    about_me TEXT,
    created_by BIGINT,
    email VARCHAR(255) UNIQUE,
    email_verified VARCHAR(255) CHECK (email_verified IN ('PENDING', 'VERIFIED')),
    full_name VARCHAR(255),
    location VARCHAR(255),
    password VARCHAR(255),
    phone VARCHAR(255) UNIQUE,
    phone_verified VARCHAR(255) CHECK (phone_verified IN ('PENDING', 'VERIFIED')),
    role_id BIGINT,
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    title VARCHAR(255),
    updated_by BIGINT,
    user_name VARCHAR(255) UNIQUE,
    is_two_factor_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    totp_secret VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_profiles_status ON profiles(status);
CREATE INDEX IF NOT EXISTS idx_profiles_role_id ON profiles(role_id);

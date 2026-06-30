-- ============================================================
-- Profiles: core user/portfolio accounts (admin and public)
-- ============================================================
CREATE TABLE profiles (
    id                    BIGSERIAL    PRIMARY KEY,

    full_name             VARCHAR(255),
    user_name             VARCHAR(255) UNIQUE,
    title                 VARCHAR(255),
    about_me              TEXT,
    email                 VARCHAR(255) UNIQUE,
    phone                 VARCHAR(255) UNIQUE,
    location              VARCHAR(255),
    password              VARCHAR(255),
    role_id               BIGINT,

    email_verified        VARCHAR(255) NOT NULL DEFAULT 'PENDING'
                                       CHECK (email_verified IN ('PENDING','VERIFIED','FAILED')),
    phone_verified        VARCHAR(255) NOT NULL DEFAULT 'PENDING'
                                       CHECK (phone_verified IN ('PENDING','VERIFIED','FAILED')),

    is_two_factor_enabled BOOLEAN      NOT NULL DEFAULT FALSE,
    totp_secret           VARCHAR(255),

    status                VARCHAR(255) NOT NULL DEFAULT 'INACTIVE'
                                       CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED')),

    created_at            TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by            BIGINT       NOT NULL DEFAULT 1,
    updated_by            BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_profiles_email     ON profiles(email);
CREATE INDEX IF NOT EXISTS idx_profiles_user_name ON profiles(user_name);
CREATE INDEX IF NOT EXISTS idx_profiles_status    ON profiles(status);

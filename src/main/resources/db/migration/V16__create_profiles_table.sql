-- ============================================================
-- Profiles: core user/portfolio accounts (admin and public)
-- ============================================================
CREATE TABLE profiles (
    id             BIGSERIAL    PRIMARY KEY,

    full_name      VARCHAR(255),
    user_name      VARCHAR(255) UNIQUE,
    email          VARCHAR(255) UNIQUE,
    phone          VARCHAR(255) UNIQUE,
    password       VARCHAR(255),
    role_id        BIGINT,

    bio            TEXT,
    tag_line       TEXT,
    address        VARCHAR(255),
    date_of_birth  DATE,

    email_verified VARCHAR(255) NOT NULL DEFAULT 'PENDING'
                                CHECK (email_verified IN ('PENDING','VERIFIED','FAILED')),
    phone_verified VARCHAR(255) NOT NULL DEFAULT 'PENDING'
                                CHECK (phone_verified IN ('PENDING','VERIFIED','FAILED')),

    two_factor_enabled BOOLEAN  NOT NULL DEFAULT FALSE,

    status         VARCHAR(255) NOT NULL DEFAULT 'INACTIVE'
                                CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED')),

    created_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT       NOT NULL DEFAULT 1,
    updated_by     BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_profiles_email     ON profiles(email);
CREATE INDEX IF NOT EXISTS idx_profiles_user_name ON profiles(user_name);
CREATE INDEX IF NOT EXISTS idx_profiles_status    ON profiles(status);

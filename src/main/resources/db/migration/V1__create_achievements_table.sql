-- ============================================================
-- Achievements: user accomplishments, awards, recognitions
-- ============================================================
CREATE TABLE achievements (
    id          BIGSERIAL    PRIMARY KEY,
    profile_id  BIGINT       NOT NULL,

    title       VARCHAR(255),
    issuer      VARCHAR(255),
    description TEXT,
    achieved_at DATE,
    sort_order  VARCHAR(255),

    status      VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                             CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED')),

    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL DEFAULT 1,
    updated_by  BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_achievements_profile_id ON achievements(profile_id);
CREATE INDEX IF NOT EXISTS idx_achievements_status     ON achievements(status);

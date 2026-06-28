-- ============================================================
-- Testimonials: recommendations and reviews from colleagues/clients
-- ============================================================
CREATE TABLE testimonials (
    id           BIGSERIAL    PRIMARY KEY,
    profile_id   BIGINT       NOT NULL,

    reviewer_name  VARCHAR(255),
    reviewer_title VARCHAR(255),
    reviewer_company VARCHAR(255),
    content      TEXT,
    rating       INTEGER,
    sort_order   INTEGER      NOT NULL DEFAULT 0,

    status       VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                              CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED')),

    created_at   TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT       NOT NULL DEFAULT 1,
    updated_by   BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_testimonials_profile_id ON testimonials(profile_id);
CREATE INDEX IF NOT EXISTS idx_testimonials_status     ON testimonials(status);

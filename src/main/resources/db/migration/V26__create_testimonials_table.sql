-- ============================================================
-- Testimonials: recommendations and reviews from colleagues/clients
-- ============================================================
CREATE TABLE testimonials (
    id             BIGSERIAL    PRIMARY KEY,
    profile_id     BIGINT       NOT NULL,

    name           VARCHAR(255),
    role           VARCHAR(255),
    company        VARCHAR(255),
    message        TEXT,
    linked_in_url  VARCHAR(255),
    sort_order     VARCHAR(255),

    status         VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                                CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED')),

    created_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT       NOT NULL DEFAULT 1,
    updated_by     BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_testimonials_profile_id ON testimonials(profile_id);
CREATE INDEX IF NOT EXISTS idx_testimonials_status     ON testimonials(status);

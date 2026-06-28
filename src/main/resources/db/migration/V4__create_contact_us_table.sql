-- ============================================================
-- Contact Us: visitor inquiries submitted through portfolio
-- ============================================================
CREATE TABLE contact_us (
    id            BIGSERIAL    PRIMARY KEY,
    profile_id    BIGINT       NOT NULL,

    name          VARCHAR(255),
    email         VARCHAR(255),
    phone         VARCHAR(255),
    message       TEXT,

    status        VARCHAR(255) NOT NULL DEFAULT 'UNREAD'
                               CHECK (status IN ('UNREAD','READ','REPLIED','ARCHIVED')),

    reply_message TEXT,
    replied_at    TIMESTAMP,

    created_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT       NOT NULL DEFAULT 1,
    updated_by    BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_contact_us_profile_id ON contact_us(profile_id);
CREATE INDEX IF NOT EXISTS idx_contact_us_status     ON contact_us(status);

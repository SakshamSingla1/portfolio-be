CREATE TABLE contact_us (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    updated_by BIGINT,
    email VARCHAR(255),
    message TEXT,
    name VARCHAR(255),
    phone VARCHAR(255),
    profile_id BIGINT,
    status VARCHAR(255) CHECK (status IN ('UNREAD', 'READ')),
    reply_message TEXT,
    replied_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_contact_us_profile_id ON contact_us(profile_id);
CREATE INDEX IF NOT EXISTS idx_contact_us_status ON contact_us(status);

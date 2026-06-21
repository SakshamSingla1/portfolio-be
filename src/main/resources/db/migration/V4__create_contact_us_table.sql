CREATE TABLE contact_us (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    email VARCHAR(255),
    message TEXT,
    name VARCHAR(255),
    phone VARCHAR(255),
    profile_id BIGINT,
    status VARCHAR(255) CHECK (status IN ('UNREAD', 'READ'))
);

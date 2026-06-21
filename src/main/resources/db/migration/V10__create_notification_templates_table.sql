CREATE TABLE notification_templates (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    body TEXT,
    created_by VARCHAR(255),
    name VARCHAR(255),
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    subject VARCHAR(255),
    type VARCHAR(255) CHECK (type IN ('EMAIL', 'SMS')),
    updated_by VARCHAR(255)
);

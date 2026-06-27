CREATE TABLE notification_templates (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    body TEXT,
    created_by BIGINT,
    name VARCHAR(255),
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    subject VARCHAR(255),
    type VARCHAR(255) CHECK (type IN ('EMAIL', 'SMS', 'NOTIFICATION')),
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_notification_templates_type ON notification_templates(type);
CREATE INDEX IF NOT EXISTS idx_notification_templates_status ON notification_templates(status);

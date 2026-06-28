CREATE TABLE notification_template_variables (
    id                BIGSERIAL    PRIMARY KEY,
    variable_name     VARCHAR(255),
    html_content      TEXT,
    whatsapp_variable BIGINT,
    template_id       BIGINT,
    created_at        TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by        BIGINT       NOT NULL DEFAULT 1,
    updated_by        BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_ntv_variable_name ON notification_template_variables(variable_name);
CREATE INDEX IF NOT EXISTS idx_ntv_template_id   ON notification_template_variables(template_id);

-- OTP-VERIFICATION variables
INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'fullName',      '{{fullName}}',      id FROM notification_templates WHERE template = 'OTP-VERIFICATION';
INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'otp',           '{{otp}}',           id FROM notification_templates WHERE template = 'OTP-VERIFICATION';
INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'expiryMinutes', '{{expiryMinutes}}', id FROM notification_templates WHERE template = 'OTP-VERIFICATION';

-- FORGOT-PASSWORD-TOKEN variables
INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'name',      '{{name}}',      id FROM notification_templates WHERE template = 'FORGOT-PASSWORD-TOKEN';
INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'resetLink', '{{resetLink}}', id FROM notification_templates WHERE template = 'FORGOT-PASSWORD-TOKEN';

-- CONTACT-US variables
INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'profileName', '{{profileName}}', id FROM notification_templates WHERE template = 'CONTACT-US';
INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'senderName',  '{{senderName}}',  id FROM notification_templates WHERE template = 'CONTACT-US';
INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'senderEmail', '{{senderEmail}}', id FROM notification_templates WHERE template = 'CONTACT-US';
INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'senderPhone', '{{senderPhone}}', id FROM notification_templates WHERE template = 'CONTACT-US';
INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'message',     '{{message}}',     id FROM notification_templates WHERE template = 'CONTACT-US';

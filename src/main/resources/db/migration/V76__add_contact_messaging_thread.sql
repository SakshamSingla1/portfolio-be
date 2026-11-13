ALTER TABLE contact_us ADD COLUMN reply_token VARCHAR(64);
CREATE UNIQUE INDEX idx_contact_us_reply_token ON contact_us(reply_token) WHERE reply_token IS NOT NULL;

CREATE TABLE contact_messages (
    id BIGSERIAL PRIMARY KEY,
    contact_us_id BIGINT NOT NULL REFERENCES contact_us(id),
    sender_type VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

CREATE INDEX idx_contact_messages_contact_us_id ON contact_messages(contact_us_id);

-- Adds a "Continue the Conversation" link to the existing CONTACT-REPLY email (sent to the
-- visitor when the owner replies) pointing at the new token-based thread page in portfolio-main,
-- so the visitor can reply back in-app instead of the conversation being a dead end.
UPDATE notification_templates SET
    message_body = REPLACE(
        message_body,
        '<p style="color:#6b7280;font-size:13px;line-height:1.6;white-space:pre-wrap;margin:0">{{originalMessage}}</p></td></tr>',
        '<p style="color:#6b7280;font-size:13px;line-height:1.6;white-space:pre-wrap;margin:0">{{originalMessage}}</p><div style="text-align:center;margin-top:24px"><a href="{{threadUrl}}" style="display:inline-block;background:#059669;color:#ffffff;text-decoration:none;padding:12px 28px;border-radius:10px;font-weight:700;font-size:14px">Continue the Conversation</a></div></td></tr>'
    )
WHERE template = 'CONTACT-REPLY';

INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'threadUrl', '{{threadUrl}}', id FROM notification_templates WHERE template = 'CONTACT-REPLY';

-- Notifies the owner by email when a visitor replies in the thread (parallel to CONTACT-US,
-- which notifies them of the initial message) -- links to the admin inbox directly since the
-- owner is already authenticated there, unlike the visitor's token-based thread link.
INSERT INTO notification_templates (template, subject, message_body, is_email, is_sms, is_whatsapp) VALUES
(
    'CONTACT-THREAD-REPLY',
    'New reply from {{contactName}} — PortfoliosBuilder',
    '<!DOCTYPE html><html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"></head><body style="margin:0;padding:0;background:#f3f4f6;font-family:-apple-system,Segoe UI,Roboto,sans-serif"><table width="100%" cellpadding="0" cellspacing="0" style="padding:40px 16px"><tr><td align="center"><table width="560" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 1px 4px rgba(0,0,0,.08)"><tr><td style="background:linear-gradient(135deg,#059669,#047857);padding:32px;text-align:center"><p style="color:#d1fae5;font-size:11px;letter-spacing:2px;margin:0 0 8px;text-transform:uppercase;font-weight:700">PortfoliosBuilder</p><h1 style="color:#fff;font-size:22px;margin:0;font-weight:800">New Reply</h1></td></tr><tr><td style="padding:36px 32px"><p style="color:#374151;font-size:15px;margin:0 0 20px">Hi <strong>{{profileName}}</strong>, <strong>{{contactName}}</strong> replied in your conversation.</p><div style="background:#f9fafb;border-left:4px solid #059669;border-radius:8px;padding:16px 20px;margin:0 0 28px"><p style="color:#374151;font-size:14px;line-height:1.6;white-space:pre-wrap;margin:0">{{message}}</p></div><div style="text-align:center"><a href="https://admin.portfoliosbuilder.com/messages" style="display:inline-block;background:#059669;color:#ffffff;text-decoration:none;padding:13px 30px;border-radius:10px;font-weight:700;font-size:14px">View Conversation</a></div></td></tr><tr><td style="padding:20px 32px;border-top:1px solid #f3f4f6;text-align:center"><p style="color:#9ca3af;font-size:12px;margin:0">Sent via PortfoliosBuilder</p></td></tr></table></td></tr></table></body></html>',
    1, 0, 0
);

INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'profileName', '{{profileName}}', id FROM notification_templates WHERE template = 'CONTACT-THREAD-REPLY';
INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'contactName', '{{contactName}}', id FROM notification_templates WHERE template = 'CONTACT-THREAD-REPLY';
INSERT INTO notification_template_variables (variable_name, html_content, template_id)
SELECT 'message', '{{message}}', id FROM notification_templates WHERE template = 'CONTACT-THREAD-REPLY';

CREATE TABLE notification_templates (
    id                     BIGSERIAL    PRIMARY KEY,
    message                TEXT,
    message_to             VARCHAR(255),
    subject                VARCHAR(255),
    message_body           TEXT,
    email_to               VARCHAR(255),
    email_cc               VARCHAR(255),
    email_bcc              VARCHAR(255),
    email_reply_to         VARCHAR(255),
    template               VARCHAR(255),
    is_sms                 INTEGER      DEFAULT 0,
    is_email               INTEGER      DEFAULT 0,
    is_whatsapp            INTEGER      DEFAULT 0,
    whatsapp_template_name VARCHAR(255),
    whatsapp_template_body TEXT,
    additional_data        TEXT,
    dlt_template_id        VARCHAR(255),
    template_group_id      BIGINT,
    created_at             TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by             BIGINT       NOT NULL DEFAULT 1,
    updated_by             BIGINT       NOT NULL DEFAULT 1
);

INSERT INTO notification_templates (template, subject, message_body, is_email, is_sms, is_whatsapp) VALUES
(
    'OTP-VERIFICATION',
    'Your Verification Code — PortfoliosBuilder',
    '<!DOCTYPE html><html><body style="font-family:sans-serif;background:#f4f4f4;padding:24px"><div style="max-width:480px;margin:auto;background:#fff;border-radius:8px;padding:32px"><h2 style="color:#D4AF37;margin-top:0">PortfoliosBuilder</h2><p>Hi <strong>{{fullName}}</strong>,</p><p>Your one-time verification code is:</p><div style="font-size:36px;font-weight:bold;letter-spacing:10px;color:#1A1A1A;margin:24px 0;text-align:center">{{otp}}</div><p>This code expires in <strong>{{expiryMinutes}} minutes</strong>. Do not share it with anyone.</p><p style="color:#737373;font-size:12px;margin-bottom:0">If you did not request this, please ignore this email.</p></div></body></html>',
    1, 0, 0
),
(
    'FORGOT-PASSWORD-TOKEN',
    'Reset Your Password — PortfoliosBuilder',
    '<!DOCTYPE html><html><body style="font-family:sans-serif;background:#f4f4f4;padding:24px"><div style="max-width:480px;margin:auto;background:#fff;border-radius:8px;padding:32px"><h2 style="color:#D4AF37;margin-top:0">PortfoliosBuilder</h2><p>Hi <strong>{{name}}</strong>,</p><p>We received a request to reset your password. Click the button below to proceed:</p><div style="text-align:center;margin:24px 0"><a href="{{resetLink}}" style="display:inline-block;padding:12px 28px;background:#D4AF37;color:#fff;text-decoration:none;border-radius:6px;font-weight:bold;font-size:15px">Reset Password</a></div><p style="font-size:13px">If the button does not work, copy and paste this link:<br><a href="{{resetLink}}" style="color:#D4AF37;word-break:break-all">{{resetLink}}</a></p><p style="color:#737373;font-size:12px;margin-bottom:0">This link expires in 30 minutes. If you did not request a password reset, please ignore this email.</p></div></body></html>',
    1, 0, 0
),
(
    'CONTACT-US',
    'New Message from {{senderName}} — PortfoliosBuilder',
    '<!DOCTYPE html><html><body style="font-family:sans-serif;background:#f4f4f4;padding:24px"><div style="max-width:560px;margin:auto;background:#fff;border-radius:8px;padding:32px"><h2 style="color:#D4AF37;margin-top:0">New Portfolio Message</h2><p>Hi <strong>{{profileName}}</strong>, someone reached out via your portfolio.</p><table style="width:100%;border-collapse:collapse;margin:16px 0;font-size:14px"><tr><td style="padding:10px 12px;font-weight:600;color:#525252;background:#f9f9f9;width:110px;border:1px solid #e5e5e5">From</td><td style="padding:10px 12px;border:1px solid #e5e5e5">{{senderName}}</td></tr><tr><td style="padding:10px 12px;font-weight:600;color:#525252;background:#f9f9f9;border:1px solid #e5e5e5">Email</td><td style="padding:10px 12px;border:1px solid #e5e5e5"><a href="mailto:{{senderEmail}}" style="color:#D4AF37">{{senderEmail}}</a></td></tr><tr><td style="padding:10px 12px;font-weight:600;color:#525252;background:#f9f9f9;border:1px solid #e5e5e5">Phone</td><td style="padding:10px 12px;border:1px solid #e5e5e5">{{senderPhone}}</td></tr><tr><td style="padding:10px 12px;font-weight:600;color:#525252;background:#f9f9f9;vertical-align:top;border:1px solid #e5e5e5">Message</td><td style="padding:10px 12px;border:1px solid #e5e5e5">{{message}}</td></tr></table><p style="color:#737373;font-size:12px;margin-bottom:0">Sent via PortfoliosBuilder</p></div></body></html>',
    1, 0, 0
);

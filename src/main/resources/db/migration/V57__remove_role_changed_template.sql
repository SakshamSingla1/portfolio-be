-- Removing the ROLE-CHANGED email template added in V56 per request.

DELETE FROM notification_template_variables
WHERE template_id IN (SELECT id FROM notification_templates WHERE template = 'ROLE-CHANGED');

DELETE FROM notification_templates WHERE template = 'ROLE-CHANGED';

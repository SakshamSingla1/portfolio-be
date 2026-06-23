-- Remove garbage rows caused by frontend field-name casing mismatch
-- (navLinkid vs navLinkId — Jackson deserialized to null on every role save)
DELETE FROM role_permissions WHERE nav_link_id IS NULL OR permission_id IS NULL;

-- Prevent recurrence
ALTER TABLE role_permissions ALTER COLUMN nav_link_id SET NOT NULL;
ALTER TABLE role_permissions ALTER COLUMN permission_id SET NOT NULL;

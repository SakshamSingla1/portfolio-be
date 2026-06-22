ALTER TABLE landing_page_config
    ADD COLUMN created_at  TIMESTAMP(6),
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

ALTER TABLE landing_features
    ADD COLUMN created_at  TIMESTAMP(6),
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

ALTER TABLE landing_faqs
    ADD COLUMN created_at  TIMESTAMP(6),
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

ALTER TABLE landing_how_to_use_steps
    ADD COLUMN created_at  TIMESTAMP(6),
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

ALTER TABLE landing_audience_cards
    ADD COLUMN created_at  TIMESTAMP(6),
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

ALTER TABLE landing_testimonials
    ADD COLUMN created_at  TIMESTAMP(6),
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

ALTER TABLE project_images
    ADD COLUMN created_at  TIMESTAMP(6),
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

ALTER TABLE seo_meta
    ADD COLUMN created_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

ALTER TABLE portfolio_views
    ADD COLUMN created_at  TIMESTAMP(6),
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

ALTER TABLE resume_downloads
    ADD COLUMN created_at  TIMESTAMP(6),
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

ALTER TABLE role_permissions
    ADD COLUMN created_at  TIMESTAMP(6),
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

ALTER TABLE password_reset_tokens
    ADD COLUMN created_at  TIMESTAMP(6),
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

UPDATE landing_page_config    SET created_at = NOW(), updated_at = NOW(), created_by = 2, updated_by = 2;
UPDATE landing_features       SET created_at = NOW(), updated_at = NOW(), created_by = 2, updated_by = 2;
UPDATE landing_faqs           SET created_at = NOW(), updated_at = NOW(), created_by = 2, updated_by = 2;
UPDATE landing_how_to_use_steps SET created_at = NOW(), updated_at = NOW(), created_by = 2, updated_by = 2;
UPDATE landing_audience_cards SET created_at = NOW(), updated_at = NOW(), created_by = 2, updated_by = 2;
UPDATE landing_testimonials   SET created_at = NOW(), updated_at = NOW(), created_by = 2, updated_by = 2;

UPDATE project_images        SET created_at = NOW(), updated_at = NOW(), created_by = 2, updated_by = 2;
UPDATE seo_meta              SET created_at = NOW(),                     created_by = 2, updated_by = 2;
UPDATE portfolio_views       SET created_at = NOW(), updated_at = NOW(), created_by = 2, updated_by = 2;
UPDATE resume_downloads      SET created_at = NOW(), updated_at = NOW(), created_by = 2, updated_by = 2;
UPDATE role_permissions      SET created_at = NOW(), updated_at = NOW(), created_by = 2, updated_by = 2;
UPDATE password_reset_tokens SET created_at = NOW(), updated_at = NOW(), created_by = 2, updated_by = 2;

ALTER TABLE contact_us
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

ALTER TABLE file_assets
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

ALTER TABLE otp_store
    ADD COLUMN updated_at  TIMESTAMP(6),
    ADD COLUMN created_by  BIGINT DEFAULT 2,
    ADD COLUMN updated_by  BIGINT DEFAULT 2;

UPDATE contact_us  SET updated_at = NOW(), created_by = 2, updated_by = 2;
UPDATE file_assets SET updated_at = NOW(),                 updated_by = 2;
UPDATE otp_store   SET updated_at = NOW(), created_by = 2, updated_by = 2;

ALTER TABLE achievements           ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE certifications         ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE color_themes           ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE educations             ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE experiences            ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE file_assets            ALTER COLUMN created_by TYPE BIGINT USING 2;
ALTER TABLE logos                  ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE nav_links              ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE notification_templates ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE permissions            ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE profile_theme_mapping  ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE profiles               ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE projects               ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE resumes                ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE roles                  ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE skills                 ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE social_links           ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;
ALTER TABLE testimonials           ALTER COLUMN created_by TYPE BIGINT USING 2, ALTER COLUMN updated_by TYPE BIGINT USING 2;

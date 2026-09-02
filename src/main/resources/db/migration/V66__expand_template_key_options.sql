ALTER TABLE profiles
    DROP CONSTRAINT IF EXISTS profiles_template_key_check;

ALTER TABLE profiles
    ADD CONSTRAINT profiles_template_key_check
    CHECK (template_key IN ('CLASSIC', 'MODERN', 'MINIMAL', 'BOLD', 'TERMINAL', 'ELEGANT', 'CREATIVE'));

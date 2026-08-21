ALTER TABLE profiles
    ADD COLUMN template_key VARCHAR(20) NOT NULL DEFAULT 'CLASSIC'
    CHECK (template_key IN ('CLASSIC', 'MODERN', 'MINIMAL'));

CREATE INDEX idx_profiles_template_key ON profiles(template_key);

INSERT INTO nav_links (nav_index, name, path, icon, nav_group, status) VALUES
('29', 'PORTFOLIO_TEMPLATES', '/portfolio-templates', 'PORTFOLIO_TEMPLATES', 'BRANDING', 'ACTIVE');

CREATE TABLE help_faqs (
    id          BIGSERIAL    PRIMARY KEY,
    question    TEXT,
    answer      TEXT,
    sort_order  INTEGER      NOT NULL DEFAULT 0,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL DEFAULT 1,
    updated_by  BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX idx_help_faqs_is_active ON help_faqs(is_active, sort_order);

-- Seeded with the FAQ content that was previously hardcoded in HelpPage.tsx,
-- so existing users see the exact same answers now that it's admin-editable.
INSERT INTO help_faqs (question, answer, sort_order, is_active) VALUES
('How do I change the color theme?', 'Go to Color Themes in the sidebar. Pick a preset card and click Apply, or click ''New Theme'' to build a custom palette. Use the Preview Panel to see changes live before committing. Your selection applies instantly to both the admin dashboard and public portfolio.', 0, true),
('What is the difference between Achievements and Certifications?', 'Certifications are formal credentials issued by a third party (AWS, Google, Coursera etc.) with an issuer, date, and verification URL. Achievements are general recognitions — hackathon wins, internal awards, open-source contributions, speaking engagements — that don''t have a formal issuer.', 1, true),
('Can I have multiple resumes uploaded?', 'Yes. Go to Resumes and upload as many versions as you need (e.g., a one-page summary and a detailed multi-page CV). Visitors see all uploaded files and can choose which to download.', 2, true),
('Why don''t I see Users, Roles, or Nav Links in the sidebar?', 'Those are admin-only modules visible only to accounts with the admin role. If you need access, ask an existing admin to elevate your role under Users → Edit User → Role.', 3, true),
('What is the difference between Nav Links and Social Links?', 'Nav Links control the dashboard sidebar navigation — adding, removing, and reordering items (admin only). Social Links manage the public profile URLs (GitHub, LinkedIn, Twitter etc.) that appear on your public portfolio page, editable by all users.', 4, true),
('How do I add project screenshots?', 'Open a project in Add or Edit mode and scroll to the Media section. Use the file uploader to attach images. Supported formats are JPG, PNG, WebP, and SVG. The first uploaded image becomes the project thumbnail on listing pages.', 5, true),
('What file formats are supported for logos?', 'The logo uploader accepts PNG, JPG, WebP, and SVG. SVG is strongly recommended because it renders sharply at any size. Logos are reused across Skills and Projects so you only need to upload each once.', 6, true),
('How do I preview my public portfolio?', 'Click Main Site in the sidebar or navigate to /main-site. This renders the full visitor-facing view using your current data and active theme. No changes are needed to publish — your data is always live.', 7, true),
('What is Landing Page management?', 'Landing Page (under the sidebar) lets you configure the sections on your public-facing landing page — Features, FAQs, How-it-works steps, Target Audience cards, and Testimonials. Each item can be toggled active/inactive, reordered, and edited without touching code.', 8, true),
('How do I switch between light and dark mode?', 'Click the sun/moon toggle in the top navigation bar. Your preference is saved locally. The selected mode applies to both the admin dashboard and — depending on your theme settings — may also follow the visitor''s system preference on the public portfolio.', 9, true),
('Can I use the same logo across multiple skills?', 'Yes. Logos are stored once in the Logos module and referenced by ID across Skills, Projects, and Experience. Updating a logo in one place automatically updates it everywhere it is referenced.', 10, true),
('How do Email Templates and variables work?', 'In Email Templates you write notification bodies using {{variableName}} placeholders. Template Variables (a sub-section of Templates) defines the available variables and their descriptions. The platform substitutes real values at send time — for example {{recipientName}} becomes the actual user''s name.', 11, true);

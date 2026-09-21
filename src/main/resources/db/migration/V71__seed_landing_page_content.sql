-- First-time seed data for the public landing page (portfoliosbuilder.com).
-- These tables existed since V27-V32 but were never populated, so the page
-- silently fell back to hardcoded copy in the frontend. This seeds the CMS
-- with that same tech-agnostic copy so SUPER_ADMIN users have real content
-- to edit from the Landing Management screen instead of empty forms, and
-- the public page and admin screen stay in sync going forward.

INSERT INTO landing_page_config (
    hero_eyebrow, hero_headline_1, hero_headline_2, hero_description,
    hero_primary_cta_text, hero_secondary_cta_text, hero_trust_badges,
    cta_badge_text, cta_headline, cta_description, cta_button_text, cta_trust_points
) VALUES (
    'Build Your Professional Portfolio',
    'Your career story,',
    'beautifully told.',
    'Create a polished, professional portfolio in minutes. Add your experience, projects, and skills — we take care of the design, hosting, and updates.',
    'Get Started Free',
    'See how it works',
    '["Free to start","No code required","Live in minutes","Fully customisable"]',
    'Ready to get started?',
    'Your professional story deserves a great home',
    'Sign up for free and start building. Add your first experience entry, upload a project screenshot, and watch your portfolio come to life — in minutes.',
    'Get Started Free',
    '["Free to start","No credit card required","Cancel anytime"]'
);

INSERT INTO landing_features (icon_name, color_key, title, description, sort_order, is_active) VALUES
('LayoutDashboard', 'teal',   'Easy-to-Use Dashboard',    'Add your experience, skills, projects, and more through a simple, guided dashboard — no coding or design skills needed.', 1, TRUE),
('Globe',           'blue',   'A Polished Public Portfolio', 'Your portfolio is generated automatically from your details — professionally designed, mobile-friendly, and ready to share.', 2, TRUE),
('Palette',         'purple', 'Dynamic Themes',           'Choose from a range of colour themes. Switch your look anytime with a single click — your portfolio updates instantly.', 3, TRUE),
('BarChart2',       'amber',  'Visitor Insights',         'See who is viewing your portfolio — visits, popular sections, and resume downloads — so you know when a recruiter takes notice.', 4, TRUE),
('Cloud',           'cyan',   'Fast, Optimised Images',   'Your photos and project images load quickly everywhere, automatically optimised for every device.', 5, TRUE),
('Lock',            'red',    'Private & Secure',         'Your dashboard is protected behind a secure login, while your portfolio stays public and easy to share with anyone.', 6, TRUE);

INSERT INTO landing_faqs (question, answer, sort_order, is_active) VALUES
('Do I need to know how to code?', 'Not at all. Everything is managed through a simple dashboard — fill in your details, upload images, and click save. Your public portfolio reflects the change immediately.', 1, TRUE),
('Is my data safe?', 'Yes. Your account is protected behind a secure login, and your data is stored securely with regular backups. Only you can edit your portfolio’s content.', 2, TRUE),
('Can I use my own domain?', 'Yes — you can connect your own custom domain so your portfolio lives at an address that’s uniquely yours.', 3, TRUE),
('Can I change my portfolio’s look later?', 'Absolutely. Switch between colour themes anytime from your dashboard — your live portfolio updates instantly, with no downtime.', 4, TRUE),
('What does it cost?', 'You can get started for free. Paid plans unlock extra features like deeper analytics and more customisation, so you can pick what fits.', 5, TRUE),
('Who can see my portfolio?', 'Your portfolio is public by default, so you can share it anywhere — with recruiters, on LinkedIn, or on your résumé. Your dashboard stays private to you.', 6, TRUE);

INSERT INTO landing_how_to_use_steps (step_number, icon_name, color_key, title, bullets, sort_order, is_active) VALUES
('01', 'Shield',   'purple', 'Create your account',   '["Sign up and log in to your personal dashboard","Your session stays active until you log out","Start on the Free plan, or upgrade anytime"]', 1, TRUE),
('02', 'Database', 'teal',   'Build your profile',    '["Fill in your experience, skills, and education","Upload a profile photo and project images","Add projects with live demo links and descriptions","List certifications with credentials and verification links"]', 2, TRUE),
('03', 'Palette',  'blue',   'Customise your look',   '["Choose from a range of colour themes","See your changes reflected instantly on your public page","No design or coding experience needed"]', 3, TRUE),
('04', 'Eye',       'amber', 'Share your portfolio',  '["Get a live link the moment your portfolio is ready","Share it in job applications, on LinkedIn, or in your email signature","Track visits and engagement right from your dashboard"]', 4, TRUE);

-- ============================================================
-- Seed the three default subscription tiers and gate a starting
-- set of nav_links (modules) per tier. Left OUT of every tier on
-- purpose (and therefore always accessible per-role, regardless of
-- plan): NAV_LINKS, SETTINGS, USERS, ROLES_AND_PERMISSIONS, HELP,
-- and the new SUBSCRIPTION_PLANS screen below — these are platform/
-- system screens, not customer-facing portfolio features.
-- ============================================================

INSERT INTO subscription_plans (name, code, description, price_monthly, price_yearly, currency, is_default, sort_order, status) VALUES
('Free',    'FREE',    'Everything needed to get a portfolio live.',                 0.00,  0.00,  'USD', TRUE,  1, 'ACTIVE'),
('Pro',     'PRO',     'Adds credibility and content depth to a portfolio.',         9.00,  90.00, 'USD', FALSE, 2, 'ACTIVE'),
('Premium', 'PREMIUM', 'Adds growth, marketing, and integration features.',          29.00, 290.00,'USD', FALSE, 3, 'ACTIVE');

-- FREE: the essentials to have a live portfolio
INSERT INTO subscription_plan_nav_links (plan_id, nav_link_id)
SELECT (SELECT id FROM subscription_plans WHERE code = 'FREE'), id
FROM nav_links
WHERE name IN ('DASHBOARD', 'PROFILE', 'EXPERIENCE', 'EDUCATION', 'SKILLS', 'PROJECT', 'RESUMES', 'SOCIAL_LINKS');

-- PRO: FREE + credibility/content depth
INSERT INTO subscription_plan_nav_links (plan_id, nav_link_id)
SELECT (SELECT id FROM subscription_plans WHERE code = 'PRO'), id
FROM nav_links
WHERE name IN ('DASHBOARD', 'PROFILE', 'EXPERIENCE', 'EDUCATION', 'SKILLS', 'PROJECT', 'RESUMES', 'SOCIAL_LINKS',
               'CERTIFICATIONS', 'TESTIMONIALS', 'ACHIEVEMENTS', 'PUBLICATIONS', 'LOGOS', 'THEMES', 'MESSAGES');

-- PREMIUM: PRO + growth/marketing + integrations
INSERT INTO subscription_plan_nav_links (plan_id, nav_link_id)
SELECT (SELECT id FROM subscription_plans WHERE code = 'PREMIUM'), id
FROM nav_links
WHERE name IN ('DASHBOARD', 'PROFILE', 'EXPERIENCE', 'EDUCATION', 'SKILLS', 'PROJECT', 'RESUMES', 'SOCIAL_LINKS',
               'CERTIFICATIONS', 'TESTIMONIALS', 'ACHIEVEMENTS', 'PUBLICATIONS', 'LOGOS', 'THEMES', 'MESSAGES',
               'ANALYTICS', 'MAIN_PAGE', 'GITHUB_INTEGRATION', 'TESTIMONIAL_REQUESTS', 'PORTFOLIO_TEMPLATES', 'NOTIFICATIONS');

-- New SUPER_ADMIN-only screen for managing subscription plans. Deliberately left out of
-- subscription_plan_nav_links above so it stays universally accessible (system screen).
INSERT INTO nav_links (nav_index, name, path, icon, nav_group, status) VALUES
('30', 'SUBSCRIPTION_PLANS', '/subscription-plans', 'SUBSCRIPTION_PLANS', 'ADMINISTRATION', 'ACTIVE');

INSERT INTO role_permissions (role_id, nav_link_id, permission_id)
SELECT 1, id, (SELECT id FROM permissions WHERE name = 'FULL_ACCESS')
FROM nav_links WHERE name = 'SUBSCRIPTION_PLANS';

-- Backfill: every existing profile starts on the FREE plan.
INSERT INTO profile_subscriptions (profile_id, plan_id, status, billing_cycle, start_date, auto_renew)
SELECT id, (SELECT id FROM subscription_plans WHERE code = 'FREE'), 'ACTIVE', 'MONTHLY', CURRENT_TIMESTAMP, FALSE
FROM profiles;

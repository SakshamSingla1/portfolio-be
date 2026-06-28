CREATE TABLE nav_links (
    id        BIGSERIAL    PRIMARY KEY,
    nav_index VARCHAR(255),
    name      VARCHAR(255),
    path      VARCHAR(255),
    icon      VARCHAR(255),
    nav_group VARCHAR(255),
    status    VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                           CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED')),
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT       NOT NULL DEFAULT 1,
    updated_by BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_nav_links_status ON nav_links(status);

INSERT INTO nav_links (nav_index, name, path, icon, nav_group, status) VALUES
-- OVERVIEW
('1',  'DASHBOARD',             '/dashboard',          'DASHBOARD',             'OVERVIEW',       'ACTIVE'),
('2',  'ANALYTICS',             '/analytics',          'ANALYTICS',             'OVERVIEW',       'ACTIVE'),
-- PORTFOLIO
('3',  'PROFILE',               '/profile',            'PROFILE',               'PORTFOLIO',      'ACTIVE'),
('4',  'EXPERIENCE',            '/experience',         'EXPERIENCE',            'PORTFOLIO',      'ACTIVE'),
('5',  'EDUCATION',             '/education',          'EDUCATION',             'PORTFOLIO',      'ACTIVE'),
('6',  'SKILLS',                '/skills',             'SKILLS',                'PORTFOLIO',      'ACTIVE'),
('7',  'PROJECT',               '/projects',           'PROJECT',               'PORTFOLIO',      'ACTIVE'),
('8',  'CERTIFICATIONS',        '/certifications',     'CERTIFICATIONS',        'PORTFOLIO',      'ACTIVE'),
('9',  'TESTIMONIALS',          '/testimonials',       'TESTIMONIALS',          'PORTFOLIO',      'ACTIVE'),
('10', 'ACHIEVEMENTS',          '/achievements',       'ACHIEVEMENTS',          'PORTFOLIO',      'ACTIVE'),
('11', 'RESUMES',               '/resumes',            'RESUMES',               'PORTFOLIO',      'ACTIVE'),
('12', 'SOCIAL_LINKS',          '/social-links',       'SOCIAL_LINKS',          'PORTFOLIO',      'ACTIVE'),
-- BRANDING
('13', 'LOGOS',                 '/logos',              'LOGOS',                 'BRANDING',       'ACTIVE'),
('14', 'THEMES',                '/themes',             'THEMES',                'BRANDING',       'ACTIVE'),
-- LANDING
('15', 'MAIN_PAGE',             '/landing-management', 'MAIN_PAGE',             'LANDING',        'ACTIVE'),
-- COMMUNICATION
('16', 'MESSAGES',              '/messages',           'MESSAGES',              'COMMUNICATION',  'ACTIVE'),
('17', 'NOTIFICATIONS',         '/notifications',      'NOTIFICATIONS',         'COMMUNICATION',  'ACTIVE'),
-- CONFIGURATION
('18', 'NAV_LINKS',             '/navlinks',           'NAV_LINKS',             'CONFIGURATION',  'ACTIVE'),
('19', 'SETTINGS',              '/settings',           'SETTINGS',              'CONFIGURATION',  'ACTIVE'),
-- ADMINISTRATION
('20', 'USERS',                 '/users',              'USERS',                 'ADMINISTRATION', 'ACTIVE'),
('21', 'ROLES_AND_PERMISSIONS', '/roles-permissions',  'ROLES_AND_PERMISSIONS', 'ADMINISTRATION', 'ACTIVE'),
-- SUPPORT
('22', 'HELP',                  '/help',               'HELP',                  'SUPPORT',        'ACTIVE');

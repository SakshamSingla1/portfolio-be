CREATE TABLE roles (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(255) UNIQUE,
    description TEXT,
    status      VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                             CHECK (status IN ('ACTIVE','INACTIVE','DELETED')),
    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL DEFAULT 1,
    updated_by  BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_roles_name   ON roles(name);
CREATE INDEX IF NOT EXISTS idx_roles_status ON roles(status);

INSERT INTO roles (name, description, status) VALUES
('SUPER_ADMIN', 'Full unrestricted access to all features and data', 'ACTIVE'),
('ADMIN',       'Portfolio owner — manages their own content and settings', 'ACTIVE');

-- SUPER_ADMIN (id=1) → FULL_ACCESS (id=4) on every nav link
INSERT INTO role_permissions (role_id, nav_link_id, permission_id)
SELECT 1, id, (SELECT id FROM permissions WHERE name = 'FULL_ACCESS')
FROM nav_links;

-- ADMIN (id=2) → READ + WRITE on all nav links except USERS and ROLES_AND_PERMISSIONS
INSERT INTO role_permissions (role_id, nav_link_id, permission_id)
SELECT 2, id, (SELECT id FROM permissions WHERE name = 'READ')
FROM nav_links WHERE name NOT IN ('USERS', 'ROLES_AND_PERMISSIONS');

INSERT INTO role_permissions (role_id, nav_link_id, permission_id)
SELECT 2, id, (SELECT id FROM permissions WHERE name = 'WRITE')
FROM nav_links WHERE name NOT IN ('USERS', 'ROLES_AND_PERMISSIONS');

INSERT INTO role_permissions (role_id, nav_link_id, permission_id)
SELECT 2, id, (SELECT id FROM permissions WHERE name = 'DELETE')
FROM nav_links WHERE name NOT IN ('USERS', 'ROLES_AND_PERMISSIONS');

-- ADMIN → READ only on USERS and ROLES_AND_PERMISSIONS
INSERT INTO role_permissions (role_id, nav_link_id, permission_id)
SELECT 2, id, (SELECT id FROM permissions WHERE name = 'READ')
FROM nav_links WHERE name IN ('USERS', 'ROLES_AND_PERMISSIONS');

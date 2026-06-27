CREATE TABLE role_permissions (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    updated_by BIGINT,
    nav_link_id BIGINT,
    permission_id BIGINT,
    role_id BIGINT
);

CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id ON role_permissions(permission_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_nav_link_id ON role_permissions(nav_link_id);

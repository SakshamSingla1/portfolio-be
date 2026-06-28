CREATE TABLE role_permissions (
    id            BIGSERIAL PRIMARY KEY,
    role_id       BIGINT    NOT NULL,
    nav_link_id   BIGINT,
    permission_id BIGINT    NOT NULL,
    created_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT       NOT NULL DEFAULT 1,
    updated_by    BIGINT       NOT NULL DEFAULT 1,
    CONSTRAINT uq_role_nav_permission UNIQUE (role_id, nav_link_id, permission_id)
);

CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id       ON role_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_nav_link_id   ON role_permissions(nav_link_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id ON role_permissions(permission_id);

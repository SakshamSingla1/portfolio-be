CREATE TABLE role_permissions (
    id BIGSERIAL PRIMARY KEY,
    nav_link_id BIGINT,
    permission_id BIGINT,
    role_id BIGINT
);

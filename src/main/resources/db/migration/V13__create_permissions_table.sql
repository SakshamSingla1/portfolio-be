CREATE TABLE permissions (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(255) UNIQUE,
    description TEXT,
    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL DEFAULT 1,
    updated_by  BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_permissions_name ON permissions(name);

INSERT INTO permissions (name, description) VALUES
('READ',        'View access — can read and list resources'),
('WRITE',       'Create and edit access — can create and update resources'),
('DELETE',      'Delete access — can remove resources'),
('FULL_ACCESS', 'Full unrestricted access — all operations on all resources');

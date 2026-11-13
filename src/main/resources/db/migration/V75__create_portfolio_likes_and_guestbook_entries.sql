CREATE TABLE portfolio_likes (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    visitor_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    CONSTRAINT uk_portfolio_likes_profile_visitor UNIQUE (profile_id, visitor_id)
);

CREATE INDEX idx_portfolio_likes_profile_id ON portfolio_likes(profile_id);

CREATE TABLE guestbook_entries (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    visitor_name VARCHAR(60) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

CREATE INDEX idx_guestbook_entries_profile_id_status ON guestbook_entries(profile_id, status);

-- Registers the owner-facing moderation page's module so it's discoverable/grantable from the
-- Roles & Permissions screen -- matching how every other feature's nav_link was rolled out (e.g.
-- V48's TESTIMONIAL_REQUESTS). No role_permissions row is inserted here; a SUPER_ADMIN still has
-- to grant VIEW/WRITE/DELETE on this module from that screen before any role can use it, same as
-- every nav_link before it.
INSERT INTO nav_links (nav_index, name, path, icon, nav_group, status) VALUES
('31', 'GUESTBOOK', '/guestbook', 'GUESTBOOK', 'COMMUNICATION', 'ACTIVE');

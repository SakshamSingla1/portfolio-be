CREATE TABLE testimonial_requests (
    id               BIGSERIAL PRIMARY KEY,
    profile_id       BIGINT        NOT NULL REFERENCES profiles(id),
    token            VARCHAR(64)   UNIQUE NOT NULL,
    requester_name   VARCHAR(100),
    requester_email  VARCHAR(200),
    expires_at       TIMESTAMP     NOT NULL,
    used_at          TIMESTAMP,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP,
    created_by       BIGINT,
    updated_by       BIGINT
);
CREATE INDEX idx_testimonial_requests_profile_id ON testimonial_requests(profile_id);
CREATE INDEX idx_testimonial_requests_token ON testimonial_requests(token);

INSERT INTO nav_links (nav_index, name, path, icon, nav_group, status) VALUES
('28', 'TESTIMONIAL_REQUESTS', '/testimonial-requests', 'TESTIMONIAL_REQUESTS', 'PORTFOLIO', 'ACTIVE');

CREATE TABLE publications (
    id              BIGSERIAL PRIMARY KEY,
    profile_id      BIGINT       NOT NULL REFERENCES profiles(id),
    title           VARCHAR(255) NOT NULL,
    type            VARCHAR(20)  NOT NULL CHECK (type IN ('PAPER','ARTICLE','TALK','VIDEO','PODCAST')),
    url             VARCHAR(500),
    publisher       VARCHAR(150),
    published_date  DATE,
    description     TEXT,
    co_authors      VARCHAR(300),
    sort_order      INT          NOT NULL DEFAULT 0,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT
);
CREATE INDEX idx_publications_profile_id ON publications(profile_id);

INSERT INTO nav_links (nav_index, name, path, icon, nav_group, status) VALUES
('27', 'PUBLICATIONS', '/publications', 'PUBLICATIONS', 'PORTFOLIO', 'ACTIVE');

-- ============================================================
-- Blog Tags: platform-wide tag vocabulary
-- Tags are shared across all profiles (no profile_id)
-- ============================================================
CREATE TABLE blog_tags (
    id   BIGSERIAL   PRIMARY KEY,
    name VARCHAR(50) NOT NULL,

    CONSTRAINT blog_tags_name_unique UNIQUE (name)
);

CREATE INDEX IF NOT EXISTS idx_blog_tags_name ON blog_tags (name);

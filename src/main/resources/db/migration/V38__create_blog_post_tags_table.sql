-- ============================================================
-- Blog Post Tags: many-to-many join between blog_posts and blog_tags
-- ============================================================
CREATE TABLE blog_post_tags (
    post_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,

    CONSTRAINT blog_post_tags_pkey PRIMARY KEY (post_id, tag_id)
);

CREATE INDEX IF NOT EXISTS idx_blog_post_tags_post_id ON blog_post_tags (post_id);
CREATE INDEX IF NOT EXISTS idx_blog_post_tags_tag_id  ON blog_post_tags (tag_id);

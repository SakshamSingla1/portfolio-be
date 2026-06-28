CREATE TABLE landing_testimonials (
    id             BIGSERIAL    PRIMARY KEY,
    author_name    VARCHAR(255),
    author_role    VARCHAR(255),
    author_company VARCHAR(255),
    avatar_url     TEXT,
    content        TEXT,
    linkedin_url   VARCHAR(255),
    sort_order     INTEGER      NOT NULL DEFAULT 0,
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT       NOT NULL DEFAULT 1,
    updated_by     BIGINT       NOT NULL DEFAULT 1
);

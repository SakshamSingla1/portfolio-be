CREATE TABLE landing_testimonials (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    updated_by BIGINT,
    author_name VARCHAR(255),
    author_role VARCHAR(255),
    author_company VARCHAR(255),
    avatar_url VARCHAR(255),
    content TEXT,
    linkedin_url VARCHAR(255),
    sort_order INT,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_landing_testimonials_active_sort ON landing_testimonials(is_active, sort_order);

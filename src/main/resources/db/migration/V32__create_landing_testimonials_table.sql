CREATE TABLE landing_testimonials (
    id BIGSERIAL PRIMARY KEY,
    author_name VARCHAR(255),
    author_role VARCHAR(255),
    author_company VARCHAR(255),
    avatar_url VARCHAR(255),
    content TEXT,
    linkedin_url VARCHAR(255),
    sort_order INT,
    is_active BOOLEAN DEFAULT TRUE
);

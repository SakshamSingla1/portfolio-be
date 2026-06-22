CREATE TABLE landing_faqs (
    id BIGSERIAL PRIMARY KEY,
    question TEXT,
    answer TEXT,
    sort_order INT,
    is_active BOOLEAN DEFAULT TRUE
);

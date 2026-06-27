CREATE TABLE landing_faqs (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    updated_by BIGINT,
    question TEXT,
    answer TEXT,
    sort_order INT,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_landing_faqs_active_sort ON landing_faqs(is_active, sort_order);

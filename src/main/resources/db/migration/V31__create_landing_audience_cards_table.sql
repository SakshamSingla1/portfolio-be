CREATE TABLE landing_audience_cards (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    updated_by BIGINT,
    icon_name VARCHAR(255),
    color_key VARCHAR(255),
    title VARCHAR(255),
    description TEXT,
    sort_order INT,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_landing_audience_cards_active_sort ON landing_audience_cards(is_active, sort_order);

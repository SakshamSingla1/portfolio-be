CREATE TABLE landing_audience_cards (
    id BIGSERIAL PRIMARY KEY,
    icon_name VARCHAR(255),
    color_key VARCHAR(255),
    title VARCHAR(255),
    description TEXT,
    sort_order INT,
    is_active BOOLEAN DEFAULT TRUE
);

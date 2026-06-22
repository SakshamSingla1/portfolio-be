CREATE TABLE landing_how_to_use_steps (
    id BIGSERIAL PRIMARY KEY,
    step_number VARCHAR(255),
    icon_name VARCHAR(255),
    color_key VARCHAR(255),
    title VARCHAR(255),
    bullets TEXT,
    sort_order INT,
    is_active BOOLEAN DEFAULT TRUE
);

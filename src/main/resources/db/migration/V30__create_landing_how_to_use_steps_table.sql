CREATE TABLE landing_how_to_use_steps (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    updated_by BIGINT,
    step_number VARCHAR(255),
    icon_name VARCHAR(255),
    color_key VARCHAR(255),
    title VARCHAR(255),
    bullets TEXT,
    sort_order INT,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_landing_how_to_use_steps_active_sort ON landing_how_to_use_steps(is_active, sort_order);

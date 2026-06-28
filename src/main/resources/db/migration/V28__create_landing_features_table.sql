CREATE TABLE landing_features (
    id          BIGSERIAL    PRIMARY KEY,
    icon_name   VARCHAR(255),
    color_key   VARCHAR(255),
    title       VARCHAR(255),
    description TEXT,
    sort_order  INTEGER      NOT NULL DEFAULT 0,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL DEFAULT 1,
    updated_by  BIGINT       NOT NULL DEFAULT 1
);

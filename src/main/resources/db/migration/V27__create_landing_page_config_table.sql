CREATE TABLE landing_page_config (
    id                       BIGSERIAL    PRIMARY KEY,
    hero_eyebrow             VARCHAR(255),
    hero_headline_1          VARCHAR(255),
    hero_headline_2          VARCHAR(255),
    hero_description         TEXT,
    hero_primary_cta_text    VARCHAR(255),
    hero_secondary_cta_text  VARCHAR(255),
    hero_trust_badges        TEXT,
    cta_badge_text           VARCHAR(255),
    cta_headline             VARCHAR(255),
    cta_description          TEXT,
    cta_button_text          VARCHAR(255),
    cta_trust_points         TEXT,
    created_at               TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by               BIGINT       NOT NULL DEFAULT 1,
    updated_by               BIGINT       NOT NULL DEFAULT 1
);

CREATE TABLE services (
    id            BIGSERIAL    PRIMARY KEY,
    profile_id    BIGINT       NOT NULL REFERENCES profiles(id),
    title         VARCHAR(100) NOT NULL,
    description   TEXT,
    icon          VARCHAR(50),
    price_range   VARCHAR(50),
    delivery_time VARCHAR(50),
    sort_order    INT          NOT NULL DEFAULT 0,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    created_by    BIGINT,
    updated_by    BIGINT
);

CREATE INDEX idx_services_profile_id ON services(profile_id);

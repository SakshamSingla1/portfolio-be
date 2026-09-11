CREATE TABLE subscription_plans (
    id             BIGSERIAL    PRIMARY KEY,
    name           VARCHAR(255) UNIQUE,
    code           VARCHAR(255) UNIQUE,
    description    TEXT,
    price_monthly  NUMERIC(10,2),
    price_yearly   NUMERIC(10,2),
    currency       VARCHAR(10)  DEFAULT 'USD',
    is_default     BOOLEAN      NOT NULL DEFAULT FALSE,
    sort_order     INTEGER,
    status         VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                                CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED','PENDING')),
    created_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT       NOT NULL DEFAULT 1,
    updated_by     BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_subscription_plans_status ON subscription_plans(status);

CREATE TABLE profile_subscriptions (
    id                  BIGSERIAL    PRIMARY KEY,
    profile_id          BIGINT       NOT NULL UNIQUE,
    plan_id             BIGINT       NOT NULL,
    status              VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                                     CHECK (status IN ('TRIALING','ACTIVE','PAST_DUE','CANCELLED','EXPIRED')),
    billing_cycle       VARCHAR(255) NOT NULL DEFAULT 'MONTHLY'
                                     CHECK (billing_cycle IN ('MONTHLY','YEARLY')),
    start_date          TIMESTAMP(6),
    end_date            TIMESTAMP(6),
    auto_renew          BOOLEAN      NOT NULL DEFAULT FALSE,
    cancelled_at        TIMESTAMP(6),
    external_payment_ref VARCHAR(255),
    created_at          TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          BIGINT       NOT NULL DEFAULT 1,
    updated_by          BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_profile_subscriptions_plan_id ON profile_subscriptions(plan_id);
CREATE INDEX IF NOT EXISTS idx_profile_subscriptions_status  ON profile_subscriptions(status);

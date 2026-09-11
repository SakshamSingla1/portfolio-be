CREATE TABLE subscription_plan_nav_links (
    id          BIGSERIAL PRIMARY KEY,
    plan_id     BIGINT    NOT NULL,
    nav_link_id BIGINT    NOT NULL,
    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL DEFAULT 1,
    updated_by  BIGINT       NOT NULL DEFAULT 1,
    CONSTRAINT uq_plan_nav_link UNIQUE (plan_id, nav_link_id)
);

CREATE INDEX IF NOT EXISTS idx_subscription_plan_nav_links_plan_id     ON subscription_plan_nav_links(plan_id);
CREATE INDEX IF NOT EXISTS idx_subscription_plan_nav_links_nav_link_id ON subscription_plan_nav_links(nav_link_id);

ALTER TABLE portfolio_views
    ADD COLUMN IF NOT EXISTS device_type    VARCHAR(20),
    ADD COLUMN IF NOT EXISTS referrer_domain VARCHAR(200),
    ADD COLUMN IF NOT EXISTS referrer_url    VARCHAR(1000);

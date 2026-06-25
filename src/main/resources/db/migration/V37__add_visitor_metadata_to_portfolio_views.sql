ALTER TABLE portfolio_views
    ADD COLUMN browser      VARCHAR(50),
    ADD COLUMN os           VARCHAR(50),
    ADD COLUMN language     VARCHAR(50),
    ADD COLUMN timezone     VARCHAR(100),
    ADD COLUMN country      VARCHAR(100),
    ADD COLUMN city         VARCHAR(100),
    ADD COLUMN country_code VARCHAR(10);

ALTER TABLE portfolio_exports
    ADD COLUMN IF NOT EXISTS created_by BIGINT,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- V35: Add NOT NULL constraints to permissions.name and roles.name,
--      and change file_assets.resource_id from INTEGER to BIGINT

ALTER TABLE permissions ALTER COLUMN name SET NOT NULL;
ALTER TABLE roles ALTER COLUMN name SET NOT NULL;

ALTER TABLE file_assets ALTER COLUMN resource_id TYPE BIGINT;

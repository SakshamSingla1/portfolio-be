-- ============================================================
-- Profile Completion Snapshots: daily record of a profile's
-- completion % so the dashboard can chart the score trending
-- up over time instead of only showing the current value.
-- ============================================================
CREATE TABLE profile_completion_snapshots (
    id             BIGSERIAL    PRIMARY KEY,
    profile_id     BIGINT       NOT NULL,
    snapshot_date  DATE         NOT NULL,
    percentage     INTEGER      NOT NULL,

    created_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT       NOT NULL DEFAULT 1,
    updated_by     BIGINT       NOT NULL DEFAULT 1,

    CONSTRAINT uq_profile_completion_snapshots_profile_date UNIQUE (profile_id, snapshot_date)
);

CREATE INDEX IF NOT EXISTS idx_profile_completion_snapshots_profile_id ON profile_completion_snapshots(profile_id);

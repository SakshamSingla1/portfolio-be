-- findByProfileIdOrderByCreatedAtDesc (the notification bell/list, hit on effectively
-- every dashboard page load) filters by profile_id and sorts by created_at DESC. The
-- existing idx_notifications_profile_unread covers (profile_id, is_read) only, which
-- doesn't help this ordering. notifications also grows without bound (one row per
-- event per profile), so an unsorted scan gets more expensive over time.
CREATE INDEX IF NOT EXISTS idx_notifications_profile_created_at ON notifications(profile_id, created_at DESC);

-- V33: Fix CHECK constraints to match current enum values in Java code

-- 1. Fix file_assets.resource_type CHECK to include PROFILE_LOGO
ALTER TABLE file_assets DROP CONSTRAINT IF EXISTS file_assets_resource_type_check;
ALTER TABLE file_assets ADD CONSTRAINT file_assets_resource_type_check
    CHECK (resource_type IN ('PROFILE','PROJECT','ACHIEVEMENT','TESTIMONIAL','CERTIFICATION','PLATFORM','LOGO','PROFILE_LOGO','RESUME','BANNER'));

-- 2. Fix social_links.platform CHECK to include all current platform enum values
ALTER TABLE social_links DROP CONSTRAINT IF EXISTS social_links_platform_check;
ALTER TABLE social_links ADD CONSTRAINT social_links_platform_check
    CHECK (platform IN ('GITHUB','LINKEDIN','TWITTER','INSTAGRAM','FACEBOOK','YOUTUBE','DRIBBBLE','BEHANCE','MEDIUM','DEV_TO','HASHNODE','WEBSITE','GITLAB','BITBUCKET','LEETCODE','HACKERRANK','CODECHEF','CODEFORCES','RESUME','X','STACKOVERFLOW','PORTFOLIO','OTHER'));

-- 3. Fix profiles.email_verified and phone_verified CHECK to include FAILED
ALTER TABLE profiles DROP CONSTRAINT IF EXISTS profiles_email_verified_check;
ALTER TABLE profiles DROP CONSTRAINT IF EXISTS profiles_phone_verified_check;
ALTER TABLE profiles ADD CONSTRAINT profiles_email_verified_check
    CHECK (email_verified IN ('PENDING','VERIFIED','FAILED'));
ALTER TABLE profiles ADD CONSTRAINT profiles_phone_verified_check
    CHECK (phone_verified IN ('PENDING','VERIFIED','FAILED'));

-- 4. Fix contact_us.status CHECK to include ARCHIVED
ALTER TABLE contact_us DROP CONSTRAINT IF EXISTS contact_us_status_check;
ALTER TABLE contact_us ADD CONSTRAINT contact_us_status_check
    CHECK (status IN ('UNREAD','READ','REPLIED','ARCHIVED'));

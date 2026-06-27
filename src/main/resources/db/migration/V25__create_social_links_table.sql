CREATE TABLE social_links (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    platform VARCHAR(255) CHECK (platform IN ('GITHUB', 'GITLAB', 'BITBUCKET', 'LINKEDIN', 'STACKOVERFLOW', 'LEETCODE', 'HACKERRANK', 'CODECHEF', 'CODEFORCES', 'PORTFOLIO', 'RESUME', 'TWITTER', 'X', 'INSTAGRAM', 'FACEBOOK', 'OTHER')),
    profile_id BIGINT,
    sort_order VARCHAR(255),
    status VARCHAR(255) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED')),
    updated_by BIGINT,
    url VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_social_links_profile_id ON social_links(profile_id);
CREATE INDEX IF NOT EXISTS idx_social_links_platform_url ON social_links(platform, url);

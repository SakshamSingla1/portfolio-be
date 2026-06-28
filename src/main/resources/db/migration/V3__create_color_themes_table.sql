CREATE TABLE color_themes (
    id         BIGSERIAL    PRIMARY KEY,
    theme_name VARCHAR(255),
    palette    TEXT,
    status     VARCHAR(255) NOT NULL DEFAULT 'ACTIVE'
                            CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','DELETED')),
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT       NOT NULL DEFAULT 1,
    updated_by BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_color_themes_status ON color_themes(status);

INSERT INTO color_themes (theme_name, palette, status) VALUES
(
    'Gold & Charcoal',
    '{"colorGroups":[{"groupName":"primary","colorShades":[{"colorName":"primary50","colorCode":"#FFF9E6"},{"colorName":"primary100","colorCode":"#FFF1BF"},{"colorName":"primary200","colorCode":"#FFE699"},{"colorName":"primary300","colorCode":"#FFD966"},{"colorName":"primary400","colorCode":"#FFCC33"},{"colorName":"primary500","colorCode":"#D4AF37"},{"colorName":"primary600","colorCode":"#B89A2E"},{"colorName":"primary700","colorCode":"#9C8326"},{"colorName":"primary800","colorCode":"#806C1F"},{"colorName":"primary900","colorCode":"#665617"}]},{"groupName":"secondary","colorShades":[{"colorName":"secondary50","colorCode":"#F2F2F2"},{"colorName":"secondary100","colorCode":"#D9D9D9"},{"colorName":"secondary200","colorCode":"#BFBFBF"},{"colorName":"secondary300","colorCode":"#8C8C8C"},{"colorName":"secondary400","colorCode":"#595959"},{"colorName":"secondary500","colorCode":"#2B2B2B"},{"colorName":"secondary600","colorCode":"#242424"},{"colorName":"secondary700","colorCode":"#1E1E1E"},{"colorName":"secondary800","colorCode":"#181818"},{"colorName":"secondary900","colorCode":"#121212"}]},{"groupName":"accent","colorShades":[{"colorName":"accent50","colorCode":"#FFF6D6"},{"colorName":"accent100","colorCode":"#FFE8A3"},{"colorName":"accent200","colorCode":"#FFD970"},{"colorName":"accent300","colorCode":"#FFC93D"},{"colorName":"accent400","colorCode":"#FFBA0A"},{"colorName":"accent500","colorCode":"#FFD700"},{"colorName":"accent600","colorCode":"#E6C200"},{"colorName":"accent700","colorCode":"#BFA100"},{"colorName":"accent800","colorCode":"#998000"},{"colorName":"accent900","colorCode":"#735F00"}]},{"groupName":"neutral","colorShades":[{"colorName":"neutral50","colorCode":"#FFFFFF"},{"colorName":"neutral100","colorCode":"#F5F5F5"},{"colorName":"neutral200","colorCode":"#E5E5E5"},{"colorName":"neutral300","colorCode":"#CCCCCC"},{"colorName":"neutral400","colorCode":"#A3A3A3"},{"colorName":"neutral500","colorCode":"#737373"},{"colorName":"neutral600","colorCode":"#525252"},{"colorName":"neutral700","colorCode":"#404040"},{"colorName":"neutral800","colorCode":"#1A1A1A"},{"colorName":"neutral900","colorCode":"#000000"}]}]}',
    'ACTIVE'
);

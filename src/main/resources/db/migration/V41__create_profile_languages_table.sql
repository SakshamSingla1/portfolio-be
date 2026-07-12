CREATE TABLE profile_languages (
    id            BIGSERIAL PRIMARY KEY,
    profile_id    BIGINT        NOT NULL REFERENCES profiles (id),
    language_name VARCHAR(100)  NOT NULL,
    proficiency   VARCHAR(20)   CHECK (proficiency IN ('NATIVE', 'FLUENT', 'INTERMEDIATE', 'BASIC')),
    sort_order    INT           DEFAULT 0,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    created_by    BIGINT,
    updated_by    BIGINT
);

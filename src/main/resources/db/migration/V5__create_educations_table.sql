-- ============================================================
-- Educations: academic background and qualifications
-- ============================================================
CREATE TABLE educations (
    id             BIGSERIAL    PRIMARY KEY,
    profile_id     BIGINT       NOT NULL,

    institution    VARCHAR(255),
    degree         VARCHAR(255) CHECK (degree IN (
                       'HIGH_SCHOOL','SENIOR_SECONDARY','DIPLOMA','ADVANCED_DIPLOMA',
                       'ASSOCIATE','BACHELORS','BTECH','BE','BSC','BA','BCOM','BCA','BBA',
                       'MASTERS','MTECH','ME','MSC','MA','MCOM','MBA','MCA',
                       'PHD','POST_DOCTORATE','CERTIFICATION','PROFESSIONAL_CERTIFICATE','OTHER'
                   )),
    field_of_study VARCHAR(255),
    location       VARCHAR(255),
    grade          VARCHAR(255),
    description    TEXT,
    start_year     INTEGER,
    end_year       INTEGER,

    created_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT       NOT NULL DEFAULT 1,
    updated_by     BIGINT       NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_educations_profile_id ON educations(profile_id);

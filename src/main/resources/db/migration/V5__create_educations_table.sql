CREATE TABLE educations (
    id BIGSERIAL PRIMARY KEY,
    end_year INTEGER,
    start_year INTEGER,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    created_by BIGINT,
    degree VARCHAR(255) CHECK (degree IN ('HIGH_SCHOOL', 'SENIOR_SECONDARY', 'DIPLOMA', 'ADVANCED_DIPLOMA', 'ASSOCIATE', 'BACHELORS', 'BTECH', 'BE', 'BSC', 'BA', 'BCOM', 'BCA', 'BBA', 'MASTERS', 'MTECH', 'ME', 'MSC', 'MA', 'MCOM', 'MBA', 'MCA', 'PHD', 'POST_DOCTORATE', 'CERTIFICATION', 'PROFESSIONAL_CERTIFICATE', 'OTHER')),
    description TEXT,
    field_of_study VARCHAR(255),
    grade VARCHAR(255),
    institution VARCHAR(255),
    location VARCHAR(255),
    profile_id BIGINT,
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_educations_profile_id ON educations(profile_id);

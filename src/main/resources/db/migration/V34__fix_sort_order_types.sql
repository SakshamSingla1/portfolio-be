ALTER TABLE achievements ALTER COLUMN sort_order TYPE INTEGER USING sort_order::INTEGER;
ALTER TABLE certifications ALTER COLUMN sort_order TYPE INTEGER USING sort_order::INTEGER;
ALTER TABLE social_links ALTER COLUMN sort_order TYPE INTEGER USING sort_order::INTEGER;
ALTER TABLE testimonials ALTER COLUMN sort_order TYPE INTEGER USING sort_order::INTEGER;

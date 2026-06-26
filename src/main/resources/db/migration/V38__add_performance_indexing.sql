-- Performance Indexing for Foreign Keys
CREATE INDEX IF NOT EXISTS idx_achievements_profile_id ON achievements(profile_id);
CREATE INDEX IF NOT EXISTS idx_certifications_profile_id ON certifications(profile_id);
CREATE INDEX IF NOT EXISTS idx_educations_profile_id ON educations(profile_id);
CREATE INDEX IF NOT EXISTS idx_experiences_profile_id ON experiences(profile_id);
CREATE INDEX IF NOT EXISTS idx_projects_profile_id ON projects(profile_id);
CREATE INDEX IF NOT EXISTS idx_resumes_profile_id ON resumes(profile_id);
CREATE INDEX IF NOT EXISTS idx_social_links_profile_id ON social_links(profile_id);
CREATE INDEX IF NOT EXISTS idx_testimonials_profile_id ON testimonials(profile_id);

-- Composite Index for File Asset Queries
CREATE INDEX IF NOT EXISTS idx_file_assets_resource ON file_assets(resource_id, resource_type);

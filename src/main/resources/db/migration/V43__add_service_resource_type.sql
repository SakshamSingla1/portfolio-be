-- Allow 'SERVICE' as a resource_type in file_assets (banner images for service offerings)
ALTER TABLE file_assets DROP CONSTRAINT IF EXISTS file_assets_resource_type_check;
ALTER TABLE file_assets ADD CONSTRAINT file_assets_resource_type_check
    CHECK (resource_type IN (
        'PROFILE','PROJECT','ACHIEVEMENT','TESTIMONIAL','CERTIFICATION',
        'PLATFORM','LOGO','PROFILE_LOGO','RESUME','BANNER','BLOG_POST','SERVICE'
    ));

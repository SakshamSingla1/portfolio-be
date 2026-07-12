ALTER TABLE profiles
  ADD COLUMN is_available_for_work  BOOLEAN DEFAULT false,
  ADD COLUMN availability_note      VARCHAR(200),
  ADD COLUMN available_from         DATE;
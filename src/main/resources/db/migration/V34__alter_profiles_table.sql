ALTER TABLE profiles 
ADD COLUMN is_two_factor_enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN totp_secret VARCHAR(255);
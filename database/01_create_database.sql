-- Drop database if exists (careful: this deletes all data!)
DROP DATABASE IF EXISTS actify_db;

-- Create fresh database
CREATE DATABASE actify_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Switch to the new database
\c actify_db;

-- Add comment
COMMENT ON DATABASE actify_db IS 'Actify volunteer management platform database';

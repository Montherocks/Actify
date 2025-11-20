-- ============================================
-- Reset Database Script
-- WARNING: This will delete ALL data!
-- ============================================

-- Terminate all active connections to the database
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'actify_db'
  AND pid <> pg_backend_pid();

-- Drop and recreate database
DROP DATABASE IF EXISTS actify_db;

CREATE DATABASE actify_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

\c actify_db;

-- Now run the table creation and seed scripts
\i 02_create_tables.sql
\i 03_seed_data.sql

\echo 'Database reset completed successfully!'

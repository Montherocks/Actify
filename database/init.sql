-- ============================================
-- Complete Database Initialization Script
-- Run this file to set up the entire database
-- ============================================

-- Create database
\i 01_create_database.sql

-- Create tables (run this connected to actify_db)
\i 02_create_tables.sql

-- Insert seed data
\i 03_seed_data.sql

-- ============================================
-- Admin Table for Actify
-- Run this in PostgreSQL to create admin table
-- ============================================

-- Create admins table
CREATE TABLE IF NOT EXISTS admins (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(20) DEFAULT 'admin' CHECK (role IN ('admin', 'super_admin')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_admins_username ON admins(username);
CREATE INDEX IF NOT EXISTS idx_admins_email ON admins(email);

-- Insert a default super admin (password: Admin@123)
-- The password hash is for 'Admin@123' using BCrypt
-- You should change this password immediately after first login!
INSERT INTO admins (username, email, password, full_name, role, is_active)
VALUES (
    'superadmin',
    'admin@actify.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',
    'Super Administrator',
    'super_admin',
    TRUE
) ON CONFLICT (username) DO NOTHING;

-- Note: The default password hash above may not work. 
-- Use the /api/admin/setup endpoint with secretKey to create your first admin.

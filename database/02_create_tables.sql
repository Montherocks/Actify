-- ============================================
-- Actify Database Schema
-- PostgreSQL 17
-- ============================================

-- Enable UUID extension (optional, for future use)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- Table: users
-- Stores user profiles for volunteers and organizations
-- ============================================

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    neighborhood VARCHAR(100) NOT NULL,
    interests TEXT,
    volunteer_points INTEGER DEFAULT 0 CHECK (volunteer_points >= 0),
    events_completed INTEGER DEFAULT 0 CHECK (events_completed >= 0),
    volunteer_hours INTEGER DEFAULT 0 CHECK (volunteer_hours >= 0),
    user_type VARCHAR(50) DEFAULT 'volunteer' CHECK (user_type IN ('volunteer', 'organization', 'admin')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add indexes for better query performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_user_type ON users(user_type);
CREATE INDEX idx_users_volunteer_points ON users(volunteer_points DESC);

-- ============================================
-- Table: organizations
-- Stores non-profit organization profiles
-- ============================================

CREATE TABLE organizations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    location VARCHAR(255),
    website VARCHAR(255),
    phone VARCHAR(20),
    logo_url VARCHAR(500),
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_organizations_email ON organizations(email);
CREATE INDEX idx_organizations_verified ON organizations(is_verified);

-- ============================================
-- Table: events
-- Stores volunteer events and opportunities
-- ============================================

CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cause VARCHAR(50) NOT NULL CHECK (cause IN ('environment', 'education', 'health', 'elderly', 'animals', 'community', 'disaster-relief', 'youth', 'other')),
    location VARCHAR(255) NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    date VARCHAR(50) NOT NULL,
    duration_hours INTEGER DEFAULT 2 CHECK (duration_hours > 0),
    points_reward INTEGER NOT NULL CHECK (points_reward > 0),
    max_participants INTEGER,
    current_participants INTEGER DEFAULT 0,
    status VARCHAR(50) DEFAULT 'active' CHECK (status IN ('active', 'pending', 'completed', 'cancelled')),
    organization_id INTEGER REFERENCES organizations(id) ON DELETE SET NULL,
    created_by INTEGER REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_events_cause ON events(cause);
CREATE INDEX idx_events_status ON events(status);
CREATE INDEX idx_events_date ON events(date);
CREATE INDEX idx_events_organization ON events(organization_id);
CREATE INDEX idx_events_location ON events(latitude, longitude);

-- ============================================
-- Table: event_registrations
-- Links users to events they've registered for
-- ============================================

CREATE TABLE event_registrations (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    event_id INTEGER NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    status VARCHAR(50) DEFAULT 'registered' CHECK (status IN ('registered', 'attended', 'cancelled', 'no-show')),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    attendance_confirmed BOOLEAN DEFAULT FALSE,
    feedback TEXT,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    UNIQUE(user_id, event_id)
);

CREATE INDEX idx_event_registrations_user ON event_registrations(user_id);
CREATE INDEX idx_event_registrations_event ON event_registrations(event_id);
CREATE INDEX idx_event_registrations_status ON event_registrations(status);

-- ============================================
-- Table: badges
-- Achievement badges earned by users
-- ============================================

CREATE TABLE badges (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    badge_type VARCHAR(100) NOT NULL CHECK (badge_type IN (
        'first_steps', 'community_hero', 'champion', 
        'green_guardian', 'education_champion', 'health_hero',
        'bronze', 'silver', 'gold', 'platinum'
    )),
    badge_name VARCHAR(100) NOT NULL,
    badge_description TEXT,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_badges_user ON badges(user_id);
CREATE INDEX idx_badges_type ON badges(badge_type);

-- ============================================
-- Table: rewards
-- Rewards available for redemption
-- ============================================

CREATE TABLE rewards (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL CHECK (category IN ('gift-cards', 'merchandise', 'experiences', 'donations')),
    points_required INTEGER NOT NULL CHECK (points_required > 0),
    stock_quantity INTEGER DEFAULT 0 CHECK (stock_quantity >= 0),
    is_available BOOLEAN DEFAULT TRUE,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rewards_category ON rewards(category);
CREATE INDEX idx_rewards_points ON rewards(points_required);
CREATE INDEX idx_rewards_available ON rewards(is_available);

-- ============================================
-- Table: reward_redemptions
-- Tracks reward redemptions by users
-- ============================================

CREATE TABLE reward_redemptions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reward_id INTEGER NOT NULL REFERENCES rewards(id) ON DELETE CASCADE,
    points_spent INTEGER NOT NULL,
    status VARCHAR(50) DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'delivered', 'cancelled')),
    redeemed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_reward_redemptions_user ON reward_redemptions(user_id);
CREATE INDEX idx_reward_redemptions_reward ON reward_redemptions(reward_id);

-- ============================================
-- Table: notifications
-- User notifications
-- ============================================

CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) CHECK (type IN ('event', 'badge', 'reward', 'system')),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(is_read);

-- ============================================
-- Functions and Triggers
-- ============================================

-- Function to update 'updated_at' timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for users table
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Trigger for events table
CREATE TRIGGER update_events_updated_at BEFORE UPDATE ON events
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Function to update event participant count
CREATE OR REPLACE FUNCTION update_event_participant_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' AND NEW.status = 'registered' THEN
        UPDATE events 
        SET current_participants = current_participants + 1 
        WHERE id = NEW.event_id;
    ELSIF TG_OP = 'DELETE' AND OLD.status = 'registered' THEN
        UPDATE events 
        SET current_participants = current_participants - 1 
        WHERE id = OLD.event_id;
    ELSIF TG_OP = 'UPDATE' AND OLD.status = 'registered' AND NEW.status = 'cancelled' THEN
        UPDATE events 
        SET current_participants = current_participants - 1 
        WHERE id = NEW.event_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for event registrations
CREATE TRIGGER update_event_participants 
    AFTER INSERT OR UPDATE OR DELETE ON event_registrations
    FOR EACH ROW EXECUTE FUNCTION update_event_participant_count();

-- ============================================
-- Comments
-- ============================================

COMMENT ON TABLE users IS 'User profiles for volunteers and organizations';
COMMENT ON TABLE organizations IS 'Non-profit organization profiles';
COMMENT ON TABLE events IS 'Volunteer events and opportunities';
COMMENT ON TABLE event_registrations IS 'User event registrations and attendance';
COMMENT ON TABLE badges IS 'Achievement badges earned by users';
COMMENT ON TABLE rewards IS 'Rewards available for point redemption';
COMMENT ON TABLE reward_redemptions IS 'User reward redemption history';
COMMENT ON TABLE notifications IS 'User notifications';

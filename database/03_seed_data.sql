-- ============================================
-- Actify Seed Data
-- Sample data for testing and development
-- ============================================

-- ============================================
-- Organizations
-- ============================================

INSERT INTO organizations (name, email, description, location, website, phone, is_verified) VALUES
('Green Earth Initiative', 'contact@greenearth.org', 'Environmental conservation and sustainability organization', 'New York, NY', 'https://greenearth.org', '+1 (555) 100-0001', TRUE),
('Education For All', 'info@educationforall.org', 'Providing quality education to underserved communities', 'San Francisco, CA', 'https://educationforall.org', '+1 (555) 100-0002', TRUE),
('Senior Care Network', 'help@seniorcare.org', 'Supporting elderly care and companionship programs', 'Chicago, IL', 'https://seniorcare.org', '+1 (555) 100-0003', TRUE),
('Community Health Hub', 'contact@healthhub.org', 'Promoting health and wellness in local communities', 'Los Angeles, CA', 'https://healthhub.org', '+1 (555) 100-0004', TRUE),
('Animal Rescue Alliance', 'rescue@animalalliance.org', 'Rescuing and rehabilitating animals in need', 'Austin, TX', 'https://animalalliance.org', '+1 (555) 100-0005', TRUE);

-- ============================================
-- Users (Demo Accounts)
-- Password for all users: demo123 (BCrypt hashed)
-- ============================================

INSERT INTO users (first_name, last_name, email, password, phone, country, city, neighborhood, interests, volunteer_points, events_completed, volunteer_hours, user_type) VALUES
('Mike', 'Rodriguez', 'demo@actify.app', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/lLm', '+1 (555) 111-2222', 'USA', 'New York', 'Manhattan', 'environment,education,health', 3200, 52, 260, 'volunteer'),
('Alex', 'Johnson', 'alex.johnson@actify.app', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/lLm', '+1 (555) 123-4567', 'USA', 'San Francisco', 'Mission District', 'environment,education,health', 1450, 28, 140, 'volunteer'),
('Sarah', 'Chen', 'sarah.chen@actify.app', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/lLm', '+1 (555) 333-4444', 'USA', 'Los Angeles', 'Downtown', 'elderly,animals,community', 2450, 45, 225, 'volunteer'),
('Emma', 'Wilson', 'emma.wilson@actify.app', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/lLm', '+1 (555) 555-6666', 'USA', 'Chicago', 'Loop', 'environment,health', 2100, 38, 190, 'volunteer'),
('David', 'Lee', 'david.lee@actify.app', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/lLm', '+1 (555) 777-8888', 'USA', 'Austin', 'Downtown', 'animals,community,youth', 1800, 32, 160, 'volunteer');

-- ============================================
-- Events
-- ============================================

INSERT INTO events (name, description, cause, location, latitude, longitude, date, duration_hours, points_reward, max_participants, current_participants, status, organization_id) VALUES
-- Active Events
('Park Cleanup Drive', 'Join us for a community park cleanup! Help us remove litter, plant flowers, and restore the beauty of Central Park. All supplies provided. Great for families and groups!', 'environment', 'Central Park, New York, NY', 40.785091, -73.968285, 'Jan 15, 2025', 3, 50, 30, 0, 'active', 1),
('Community Teaching Workshop', 'Share your knowledge! Teach coding, math, or entrepreneurship to local youth aged 12-18. No teaching experience required - just passion and patience.', 'education', 'Community Center, San Francisco, CA', 40.758896, -73.985130, 'Jan 22, 2025', 4, 75, 15, 0, 'active', 2),
('Tree Planting Initiative', 'Combat climate change one tree at a time! Join our urban reforestation project. We will plant 100+ native trees in Prospect Park. Tools and training provided.', 'environment', 'Prospect Park, Brooklyn, NY', 40.661256, -73.969880, 'Jan 29, 2025', 5, 60, 40, 0, 'active', 1),
('Senior Center Support', 'Brighten someones day! Spend quality time with seniors - play games, share stories, help with technology, or just be a friendly companion.', 'elderly', 'Senior Care Center, Manhattan, NY', 40.750000, -73.970000, 'Feb 5, 2025', 3, 45, 20, 0, 'active', 3),
('Beach Cleanup & Restoration', 'Save our oceans! Clean up Rockaway Beach and help restore the coastal habitat. Join us for a day of environmental action by the sea.', 'environment', 'Rockaway Beach, Queens, NY', 40.575533, -73.821181, 'Feb 12, 2025', 4, 70, 50, 0, 'active', 1),
('Health Awareness Fair', 'Help organize and run a community health fair! Assist with blood pressure screenings, healthy cooking demos, and fitness activities.', 'health', 'Community Health Hub, Los Angeles, CA', 34.052235, -118.243683, 'Feb 18, 2025', 6, 80, 25, 0, 'active', 4),
('Animal Shelter Volunteer Day', 'Love animals? Spend the day caring for rescued dogs and cats. Help with feeding, grooming, walking, and socializing our furry friends.', 'animals', 'Animal Rescue Center, Austin, TX', 30.267153, -97.743057, 'Feb 25, 2025', 4, 55, 15, 0, 'active', 5),
('Youth Mentorship Program', 'Be a role model! Mentor local youth through homework help, career guidance, and life skills coaching. Weekly commitment preferred.', 'youth', 'Youth Center, Chicago, IL', 41.878113, -87.629799, 'Mar 3, 2025', 2, 40, 12, 0, 'active', 2),

-- Pending Events
('Community Garden Build', 'Help build raised garden beds for our new community garden. Carpentry skills helpful but not required!', 'community', 'Harlem, New York, NY', 40.810000, -73.948000, 'Mar 10, 2025', 5, 65, 20, 0, 'pending', 1),
('Food Bank Distribution', 'Assist with sorting, packing, and distributing food to families in need. Help fight hunger in our community.', 'community', 'Food Bank, Brooklyn, NY', 40.678178, -73.944158, 'Mar 15, 2025', 3, 50, 30, 0, 'pending', 4),

-- Completed Events
('Holiday Gift Wrapping', 'Helped wrap gifts for underprivileged children during the holiday season', 'youth', 'Community Center, Manhattan, NY', 40.758000, -73.985000, 'Dec 20, 2024', 4, 55, 25, 25, 'completed', 2),
('Winter Coat Drive', 'Collected and distributed winter coats to homeless individuals', 'community', 'Shelter, Queens, NY', 40.728157, -73.794350, 'Dec 15, 2024', 3, 45, 20, 20, 'completed', 3);

-- ============================================
-- Event Registrations (for completed events)
-- ============================================

INSERT INTO event_registrations (user_id, event_id, status, attendance_confirmed, rating) VALUES
(1, 11, 'attended', TRUE, 5),
(1, 12, 'attended', TRUE, 5),
(2, 11, 'attended', TRUE, 4),
(3, 11, 'attended', TRUE, 5),
(3, 12, 'attended', TRUE, 4),
(4, 12, 'attended', TRUE, 5);

-- ============================================
-- Badges
-- ============================================

INSERT INTO badges (user_id, badge_type, badge_name, badge_description) VALUES
-- Mike's badges
(1, 'first_steps', 'First Steps', 'Completed your first volunteer event'),
(1, 'community_hero', 'Community Hero', 'Completed 10 volunteer events'),
(1, 'champion', 'Champion', 'Completed 50 volunteer events'),
(1, 'green_guardian', 'Green Guardian', 'Completed 5 environmental events'),
(1, 'gold', 'Gold Tier', 'Earned 3000+ volunteer points'),

-- Alex's badges
(2, 'first_steps', 'First Steps', 'Completed your first volunteer event'),
(2, 'community_hero', 'Community Hero', 'Completed 10 volunteer events'),
(2, 'green_guardian', 'Green Guardian', 'Completed 5 environmental events'),
(2, 'silver', 'Silver Tier', 'Earned 1000+ volunteer points'),

-- Sarah's badges
(3, 'first_steps', 'First Steps', 'Completed your first volunteer event'),
(3, 'community_hero', 'Community Hero', 'Completed 10 volunteer events'),
(3, 'champion', 'Champion', 'Completed 50 volunteer events'),
(3, 'silver', 'Silver Tier', 'Earned 1000+ volunteer points'),

-- Emma's badges
(4, 'first_steps', 'First Steps', 'Completed your first volunteer event'),
(4, 'community_hero', 'Community Hero', 'Completed 10 volunteer events'),
(4, 'green_guardian', 'Green Guardian', 'Completed 5 environmental events'),
(4, 'silver', 'Silver Tier', 'Earned 1000+ volunteer points');

-- ============================================
-- Rewards
-- ============================================

INSERT INTO rewards (title, description, category, points_required, stock_quantity, is_available, image_url) VALUES
-- Gift Cards
('$10 Amazon Gift Card', 'Redeem your points for a $10 Amazon gift card', 'gift-cards', 500, 100, TRUE, NULL),
('$25 Starbucks Gift Card', 'Enjoy your favorite coffee with a $25 Starbucks gift card', 'gift-cards', 1000, 75, TRUE, NULL),
('$50 Target Gift Card', 'Shop for essentials with a $50 Target gift card', 'gift-cards', 2000, 50, TRUE, NULL),
('$100 Visa Gift Card', 'Universal spending power with a $100 Visa gift card', 'gift-cards', 4000, 25, TRUE, NULL),

-- Merchandise
('Actify T-Shirt', 'Official Actify volunteer t-shirt in your size', 'merchandise', 300, 200, TRUE, NULL),
('Actify Water Bottle', 'Eco-friendly stainless steel water bottle', 'merchandise', 400, 150, TRUE, NULL),
('Actify Tote Bag', 'Reusable canvas tote bag perfect for shopping', 'merchandise', 250, 300, TRUE, NULL),
('Actify Hoodie', 'Comfortable hoodie with Actify logo', 'merchandise', 800, 100, TRUE, NULL),

-- Experiences
('Museum Day Pass', 'Free admission to local museums for a day', 'experiences', 600, 50, TRUE, NULL),
('Movie Theater Tickets (2)', 'Two tickets to your local movie theater', 'experiences', 750, 40, TRUE, NULL),
('Yoga Class Pack (5)', 'Five sessions at a partner yoga studio', 'experiences', 1200, 30, TRUE, NULL),
('Concert Tickets (2)', 'Two general admission tickets to local concerts', 'experiences', 1500, 20, TRUE, NULL),

-- Donations
('Plant 10 Trees', 'We will plant 10 trees in your name', 'donations', 500, 999, TRUE, NULL),
('Feed 50 Families', 'Provide meals for 50 families in need', 'donations', 1000, 999, TRUE, NULL),
('Build a Well', 'Help build a well in a developing country', 'donations', 5000, 999, TRUE, NULL),
('School Supplies for 100 Kids', 'Provide school supplies for 100 children', 'donations', 2000, 999, TRUE, NULL);

-- ============================================
-- Sample Reward Redemptions
-- ============================================

INSERT INTO reward_redemptions (user_id, reward_id, points_spent, status) VALUES
(1, 1, 500, 'delivered'),
(1, 5, 300, 'delivered'),
(2, 7, 250, 'delivered'),
(3, 2, 1000, 'approved');

-- ============================================
-- Notifications
-- ============================================

INSERT INTO notifications (user_id, title, message, type, is_read) VALUES
(1, 'New Event Available!', 'Park Cleanup Drive is now open for registration', 'event', FALSE),
(1, 'Badge Earned!', 'Congratulations! You have earned the Champion badge', 'badge', TRUE),
(2, 'Reward Redeemed', 'Your Actify Tote Bag is on its way!', 'reward', FALSE),
(3, 'Event Reminder', 'You are registered for Beach Cleanup tomorrow', 'event', FALSE),
(4, 'New Event Available!', 'Tree Planting Initiative is now open for registration', 'event', FALSE);

-- ============================================
-- Verification Queries
-- ============================================

-- Verify all tables have data
DO $$
DECLARE
    table_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO table_count FROM users;
    RAISE NOTICE 'Users: %', table_count;
    
    SELECT COUNT(*) INTO table_count FROM organizations;
    RAISE NOTICE 'Organizations: %', table_count;
    
    SELECT COUNT(*) INTO table_count FROM events;
    RAISE NOTICE 'Events: %', table_count;
    
    SELECT COUNT(*) INTO table_count FROM badges;
    RAISE NOTICE 'Badges: %', table_count;
    
    SELECT COUNT(*) INTO table_count FROM rewards;
    RAISE NOTICE 'Rewards: %', table_count;
    
    RAISE NOTICE 'Database seeding completed successfully!';
END $$;

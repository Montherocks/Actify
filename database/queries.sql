-- ============================================
-- Useful Database Queries
-- Common queries for development and testing
-- ============================================

-- ============================================
-- User Statistics
-- ============================================

-- Get all users with their stats
SELECT 
    id,
    CONCAT(first_name, ' ', last_name) as name,
    email,
    volunteer_points,
    events_completed,
    volunteer_hours,
    user_type
FROM users
ORDER BY volunteer_points DESC;

-- Get top 10 volunteers by points
SELECT 
    CONCAT(first_name, ' ', last_name) as name,
    email,
    volunteer_points,
    events_completed,
    volunteer_hours
FROM users
WHERE user_type = 'volunteer'
ORDER BY volunteer_points DESC
LIMIT 10;

-- ============================================
-- Event Statistics
-- ============================================

-- Get all active events
SELECT 
    id,
    name,
    cause,
    location,
    date,
    points_reward,
    current_participants,
    max_participants,
    status
FROM events
WHERE status = 'active'
ORDER BY date;

-- Get events by organization
SELECT 
    e.name as event_name,
    e.cause,
    e.date,
    e.points_reward,
    o.name as organization_name
FROM events e
LEFT JOIN organizations o ON e.organization_id = o.id
ORDER BY e.date;

-- Get event participation summary
SELECT 
    e.name,
    e.current_participants,
    e.max_participants,
    ROUND((e.current_participants::DECIMAL / NULLIF(e.max_participants, 0)) * 100, 2) as fill_percentage,
    e.status
FROM events e
WHERE e.max_participants IS NOT NULL
ORDER BY fill_percentage DESC;

-- ============================================
-- Registration Statistics
-- ============================================

-- Get user's registered events
SELECT 
    u.first_name || ' ' || u.last_name as user_name,
    e.name as event_name,
    e.date,
    er.status,
    er.attendance_confirmed
FROM event_registrations er
JOIN users u ON er.user_id = u.id
JOIN events e ON er.event_id = e.id
WHERE u.id = 1  -- Change user ID as needed
ORDER BY e.date;

-- Get event registrations count
SELECT 
    e.name,
    COUNT(er.id) as total_registrations,
    COUNT(CASE WHEN er.status = 'attended' THEN 1 END) as attended,
    COUNT(CASE WHEN er.status = 'registered' THEN 1 END) as registered,
    COUNT(CASE WHEN er.status = 'cancelled' THEN 1 END) as cancelled
FROM events e
LEFT JOIN event_registrations er ON e.id = er.event_id
GROUP BY e.id, e.name
ORDER BY total_registrations DESC;

-- ============================================
-- Badge Statistics
-- ============================================

-- Get all badges earned by user
SELECT 
    u.first_name || ' ' || u.last_name as user_name,
    b.badge_name,
    b.badge_description,
    b.earned_at
FROM badges b
JOIN users u ON b.user_id = u.id
WHERE u.id = 1  -- Change user ID as needed
ORDER BY b.earned_at DESC;

-- Get badge distribution
SELECT 
    badge_type,
    badge_name,
    COUNT(*) as times_earned
FROM badges
GROUP BY badge_type, badge_name
ORDER BY times_earned DESC;

-- Get users with most badges
SELECT 
    u.first_name || ' ' || u.last_name as user_name,
    COUNT(b.id) as badge_count
FROM users u
LEFT JOIN badges b ON u.id = b.user_id
GROUP BY u.id, u.first_name, u.last_name
ORDER BY badge_count DESC;

-- ============================================
-- Reward Statistics
-- ============================================

-- Get all available rewards
SELECT 
    title,
    category,
    points_required,
    stock_quantity,
    is_available
FROM rewards
WHERE is_available = TRUE
ORDER BY points_required;

-- Get user's reward redemption history
SELECT 
    u.first_name || ' ' || u.last_name as user_name,
    r.title as reward_title,
    rr.points_spent,
    rr.status,
    rr.redeemed_at
FROM reward_redemptions rr
JOIN users u ON rr.user_id = u.id
JOIN rewards r ON rr.reward_id = r.id
WHERE u.id = 1  -- Change user ID as needed
ORDER BY rr.redeemed_at DESC;

-- Get most popular rewards
SELECT 
    r.title,
    r.category,
    r.points_required,
    COUNT(rr.id) as redemption_count
FROM rewards r
LEFT JOIN reward_redemptions rr ON r.id = rr.reward_id
GROUP BY r.id, r.title, r.category, r.points_required
ORDER BY redemption_count DESC;

-- ============================================
-- Organization Statistics
-- ============================================

-- Get organizations with their event count
SELECT 
    o.name,
    o.email,
    o.location,
    o.is_verified,
    COUNT(e.id) as total_events,
    COUNT(CASE WHEN e.status = 'active' THEN 1 END) as active_events
FROM organizations o
LEFT JOIN events e ON o.id = e.organization_id
GROUP BY o.id, o.name, o.email, o.location, o.is_verified
ORDER BY total_events DESC;

-- ============================================
-- Leaderboard Queries
-- ============================================

-- Overall volunteer leaderboard
SELECT 
    ROW_NUMBER() OVER (ORDER BY volunteer_points DESC) as rank,
    first_name || ' ' || last_name as name,
    volunteer_points,
    events_completed,
    volunteer_hours
FROM users
WHERE user_type = 'volunteer'
ORDER BY volunteer_points DESC
LIMIT 20;

-- Monthly leaderboard (based on events completed this month)
SELECT 
    u.first_name || ' ' || u.last_name as name,
    COUNT(er.id) as events_this_month,
    SUM(e.points_reward) as points_earned
FROM users u
JOIN event_registrations er ON u.id = er.user_id
JOIN events e ON er.event_id = e.id
WHERE er.status = 'attended'
  AND er.registration_date >= DATE_TRUNC('month', CURRENT_DATE)
GROUP BY u.id, u.first_name, u.last_name
ORDER BY points_earned DESC
LIMIT 10;

-- ============================================
-- Analytics Queries
-- ============================================

-- Event participation by cause
SELECT 
    cause,
    COUNT(*) as event_count,
    AVG(current_participants) as avg_participants,
    SUM(current_participants) as total_participants
FROM events
GROUP BY cause
ORDER BY total_participants DESC;

-- User engagement metrics
SELECT 
    AVG(volunteer_points) as avg_points,
    AVG(events_completed) as avg_events,
    AVG(volunteer_hours) as avg_hours,
    MAX(volunteer_points) as max_points,
    MIN(volunteer_points) as min_points
FROM users
WHERE user_type = 'volunteer';

-- Recent activity summary
SELECT 
    'Total Users' as metric,
    COUNT(*)::TEXT as value
FROM users
UNION ALL
SELECT 
    'Total Events',
    COUNT(*)::TEXT
FROM events
UNION ALL
SELECT 
    'Active Events',
    COUNT(*)::TEXT
FROM events WHERE status = 'active'
UNION ALL
SELECT 
    'Total Registrations',
    COUNT(*)::TEXT
FROM event_registrations
UNION ALL
SELECT 
    'Total Badges Earned',
    COUNT(*)::TEXT
FROM badges;

-- ============================================
-- Data Validation Queries
-- ============================================

-- Check for users without any registrations
SELECT 
    first_name || ' ' || last_name as name,
    email,
    volunteer_points
FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM event_registrations er WHERE er.user_id = u.id
)
AND user_type = 'volunteer';

-- Check for events without any registrations
SELECT 
    name,
    date,
    status,
    max_participants
FROM events e
WHERE NOT EXISTS (
    SELECT 1 FROM event_registrations er WHERE er.event_id = e.id
)
AND status = 'active';

-- Check for orphaned registrations (events or users deleted)
SELECT * FROM event_registrations
WHERE user_id NOT IN (SELECT id FROM users)
   OR event_id NOT IN (SELECT id FROM events);

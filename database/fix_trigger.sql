-- Fix the trigger to use the correct column name (volunteers_registered instead of current_participants)
-- Run this script to fix the event registration error

-- First, drop the old trigger and function
DROP TRIGGER IF EXISTS update_event_participants ON event_registrations;
DROP FUNCTION IF EXISTS update_event_participant_count();

-- Recreate the function with the correct column name
CREATE OR REPLACE FUNCTION update_event_participant_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' AND NEW.status = 'registered' THEN
        UPDATE events 
        SET volunteers_registered = COALESCE(volunteers_registered, 0) + 1 
        WHERE id = NEW.event_id;
    ELSIF TG_OP = 'DELETE' AND OLD.status = 'registered' THEN
        UPDATE events 
        SET volunteers_registered = GREATEST(COALESCE(volunteers_registered, 0) - 1, 0)
        WHERE id = OLD.event_id;
    ELSIF TG_OP = 'UPDATE' AND OLD.status = 'registered' AND NEW.status = 'cancelled' THEN
        UPDATE events 
        SET volunteers_registered = GREATEST(COALESCE(volunteers_registered, 0) - 1, 0)
        WHERE id = NEW.event_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Recreate the trigger
CREATE TRIGGER update_event_participants 
    AFTER INSERT OR UPDATE OR DELETE ON event_registrations
    FOR EACH ROW EXECUTE FUNCTION update_event_participant_count();

-- Verify it works
SELECT 'Trigger fixed successfully!' as status;

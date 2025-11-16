-- Update flight dates to future dates (2026-12-15)
-- Run this if you need to update existing data: psql -d flight_search_db -f update-flight-dates.sql

UPDATE flights 
SET departure_time = '2026-12-15 06:00:00', 
    arrival_time = '2026-12-15 08:30:00' 
WHERE flight_id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11';

UPDATE flights 
SET departure_time = '2026-12-15 10:00:00', 
    arrival_time = '2026-12-15 12:30:00' 
WHERE flight_id = 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12';

UPDATE flights 
SET departure_time = '2026-12-15 14:00:00', 
    arrival_time = '2026-12-15 16:00:00' 
WHERE flight_id = 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13';

SELECT 'Flight dates updated successfully!' AS status;
SELECT flight_id, carrier, flight_number, departure_airport, arrival_airport, departure_time 
FROM flights 
ORDER BY departure_time;

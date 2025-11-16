-- Sample flight data for testing
-- Note: This will auto-execute if spring.jpa.hibernate.ddl-auto is set to create or create-drop

-- Sample flights (using dates far in future to avoid expiry)
INSERT INTO flights (flight_id, carrier, flight_number, departure_airport, arrival_airport, departure_time, arrival_time, equipment, is_active) 
VALUES 
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'INDIGO', '6E-123', 'BLR', 'DEL', '2026-12-15 06:00:00', '2026-12-15 08:30:00', 'A320', true),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'AIR_INDIA', 'AI-456', 'BLR', 'DEL', '2026-12-15 10:00:00', '2026-12-15 12:30:00', 'B737', true),
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'VISTARA', 'UK-789', 'DEL', 'BOM', '2026-12-15 14:00:00', '2026-12-15 16:00:00', 'A321', true);

-- Sample seat inventory
INSERT INTO seat_inventory (flight_id, fare_class, cabin_class, total_seats, available_seats, price, version) 
VALUES 
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Y', 'ECONOMY', 180, 150, 5000.00, 0),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'J', 'BUSINESS', 20, 15, 15000.00, 0),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Y', 'ECONOMY', 160, 120, 5500.00, 0),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'J', 'BUSINESS', 16, 10, 16000.00, 0),
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Y', 'ECONOMY', 170, 140, 4500.00, 0);

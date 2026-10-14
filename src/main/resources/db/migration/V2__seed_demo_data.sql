-- V2__seed_demo_data.sql
-- Flyway Migration Script: Seed initial demo data for stations, chargers, users, and settings

-- Insert Initial Users & Admins (Passwords: Admin@12345 and User@12345)
INSERT INTO `users` (`id`, `full_name`, `email`, `phone`, `password_hash`, `role`, `is_active`) VALUES
(1, 'System Administrator', 'admin@evcharge.com', '+919876543210', '$2a$10$dY9fGvV6Z4FjP9X3rK2kO.zYQ5dM0N7rJ8lF1gB3eC5sE9hL1mK6a', 'ROLE_ADMIN', TRUE),
(2, 'Rahul Sharma', 'rahul.ev@gmail.com', '+919847012345', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ROLE_USER', TRUE),
(3, 'Ananya Nair', 'ananya.nair@gmail.com', '+919847067890', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ROLE_USER', TRUE),
(4, 'Mohammed Farhan', 'farhan.m@gmail.com', '+919847112233', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ROLE_USER', TRUE)
ON DUPLICATE KEY UPDATE `email` = `email`;

-- Insert User Vehicles
INSERT INTO `user_vehicles` (`id`, `user_id`, `registration_number`, `make`, `model`, `vehicle_type`, `preferred_connector`) VALUES
(1, 2, 'KL-13-AQ-4521', 'Tata', 'Nexon EV MAX', '4-Wheeler SUV', 'CCS2'),
(2, 2, 'KL-13-BT-8900', 'Ather', '450X Gen 3', '2-Wheeler Scooter', 'Type 2'),
(3, 3, 'KL-07-CD-3312', 'MG', 'ZS EV Exclusive', '4-Wheeler SUV', 'CCS2'),
(4, 4, 'KL-11-ER-7744', 'Hyundai', 'Ioniq 5', '4-Wheeler SUV', 'CCS2')
ON DUPLICATE KEY UPDATE `registration_number` = `registration_number`;

-- Insert Demo Charging Stations (Kannur, Taliparamba, Thalassery, Kozhikode, Kochi, Bengaluru, Chennai, Coimbatore)
INSERT INTO `stations` (`id`, `name`, `description`, `address`, `area`, `city`, `state`, `postal_code`, `latitude`, `longitude`, `contact_number`, `opening_time`, `closing_time`, `operating_days`, `parking_information`, `status`, `is_demo`) VALUES
(1, '[DEMO] EV Charge Caltex Hub', 'High-speed dual CCS2 charger situated conveniently near Caltex Junction.', 'Near Caltex Junction, NH 66, Kannur', 'Caltex', 'Kannur', 'Kerala', '670002', 11.874477, 75.370369, '+914972701100', '00:00:00', '23:59:59', 'All Days', 'Dedicated 4-bay EV parking with canopy', 'ACTIVE', TRUE),
(2, '[DEMO] GreenDrive Thottada Station', 'AC fast and DC rapid charging station near Kannur South and Government Polytechnic.', 'Thottada Beach Road, NH 66, Kannur', 'Thottada', 'Kannur', 'Kerala', '670007', 11.832450, 75.409820, '+914972723450', '06:00:00', '23:00:00', 'All Days', '3 parking slots with CCTV monitoring', 'ACTIVE', TRUE),
(3, '[DEMO] Highway Volt Taliparamba', 'Ultra-fast DC charging station on NH 66 connecting Kannur to Payyanur and Kasaragod.', 'National Highway 66, Near Bus Stand, Taliparamba', 'National Highway', 'Taliparamba', 'Kerala', '670141', 12.043740, 75.358290, '+914602203344', '00:00:00', '23:59:59', 'All Days', 'Spacious parking beside food court', 'ACTIVE', TRUE),
(4, '[DEMO] Taliparamba Town FastPoint', 'Fast AC and DC dual charger located adjacent to municipal shopping complex.', 'Court Road, Taliparamba Town', 'Town Center', 'Taliparamba', 'Kerala', '670141', 12.051200, 75.362100, '+914602209988', '07:00:00', '22:00:00', 'Mon - Sat', '2 covered parking slots', 'ACTIVE', TRUE),
(5, '[DEMO] Malabar EV Plaza Thalassery', 'Commercial DC fast charger situated near New Bus Stand Thalassery.', 'New Bus Stand Complex, Thalassery', 'New Bus Stand', 'Thalassery', 'Kerala', '670101', 11.751230, 75.492540, '+914902324411', '05:00:00', '23:30:00', 'All Days', '4 EV charging bays with lighting', 'ACTIVE', TRUE),
(6, '[DEMO] SeaBreeze Charging Hub Thalassery', 'Scenic EV charging point near Thalassery Sea Bridge and Overbury’s Folly.', 'Beach Road, Near Overbury’s Folly, Thalassery', 'Beach Road', 'Thalassery', 'Kerala', '670101', 11.745600, 75.485100, '+914902341200', '06:00:00', '22:00:00', 'All Days', 'Reserved parking for EV tourists', 'ACTIVE', TRUE),
(7, '[DEMO] Calicut Cyberpark EV Station', 'High power multi-connector hub serving IT professionals and bypass commuters.', 'Cyberpark SEZ, Nellikkode, Kozhikode', 'Cyberpark Bypass', 'Kozhikode', 'Kerala', '673016', 11.272180, 75.834520, '+914952431100', '00:00:00', '23:59:59', 'All Days', '6 dedicated parking bays', 'ACTIVE', TRUE),
(8, '[DEMO] Mavoor Road Rapid Charge', 'City center high-speed EV charging station near KSRTC bus terminal.', 'Opposite KSRTC Terminal, Mavoor Road, Kozhikode', 'Mavoor Road', 'Kozhikode', 'Kerala', '673004', 11.258750, 75.780410, '+914952721990', '00:00:00', '23:59:59', 'All Days', 'Covered 3-car parking area', 'ACTIVE', TRUE),
(9, '[DEMO] Kozhikode Beachfront Charging Hub', 'Eco-friendly solar assisted charging station on the historic Kozhikode Beach.', 'Beach Promenade Road, Near Lighthouse, Kozhikode', 'Beach Road', 'Kozhikode', 'Kerala', '673032', 11.262500, 75.768100, '+914952365510', '06:00:00', '23:00:00', 'All Days', '4 slots overlooking the sea', 'ACTIVE', TRUE),
(10, '[DEMO] Lulu Mall Mega EV Hub Edappally', 'State-of-the-art multi-gun charging station located at Lulu International Shopping Mall.', 'Lulu Mall Basement 2, Edappally Toll Junction, Kochi', 'Edappally', 'Kochi', 'Kerala', '682024', 10.027580, 76.308050, '+914842727700', '09:00:00', '23:00:00', 'All Days', '8 premium EV bays with valet assistance', 'ACTIVE', TRUE),
(11, '[DEMO] Infopark SmartCharge Kakkanad', 'Fast DC dual charger located right at Infopark Phase 1 entrance.', 'Infopark Expressway, Kakkanad, Kochi', 'Kakkanad', 'Kochi', 'Kerala', '682042', 10.010530, 76.363620, '+914844001234', '00:00:00', '23:59:59', 'All Days', '5 designated bays with 24/7 security', 'ACTIVE', TRUE),
(12, '[DEMO] Marine Drive EcoStation Kochi', 'Convenient city center AC and DC charging facility along Shanmugham Road.', 'Shanmugham Road, Marine Drive, Kochi', 'Marine Drive', 'Kochi', 'Kerala', '682031', 9.979800, 76.276200, '+914842381900', '06:00:00', '23:00:00', 'All Days', '3 slots with scenic promenade access', 'ACTIVE', TRUE),
(13, '[DEMO] Indiranagar Metro EV Point', 'Premium DC fast charger adjacent to 100 Feet Road and Metro Station.', '100 Feet Road, HAL 2nd Stage, Indiranagar, Bengaluru', 'Indiranagar', 'Bengaluru', 'Karnataka', '560038', 12.971891, 77.641151, '+918025201122', '00:00:00', '23:59:59', 'All Days', '4 bays with quick restaurant access', 'ACTIVE', TRUE),
(14, '[DEMO] Electronic City Expressway Hub', 'High capacity 150kW fast charger located on Hosur Main Road.', 'Phase 1, Hosur Main Road, Electronic City, Bengaluru', 'Electronic City', 'Bengaluru', 'Karnataka', '560100', 12.839900, 77.677000, '+918028520099', '00:00:00', '23:59:59', 'All Days', '6 slots with driver rest lounge', 'ACTIVE', TRUE),
(15, '[DEMO] OMR Cyber Corridor Station', 'High power DC station located on Rajiv Gandhi Salai (OMR IT Expressway).', 'Rajiv Gandhi Salai, Thoraipakkam, Chennai', 'OMR Expressway', 'Chennai', 'Tamil Nadu', '600097', 12.935200, 80.231200, '+914424961122', '00:00:00', '23:59:59', 'All Days', '4 rapid charging slots', 'ACTIVE', TRUE),
(16, '[DEMO] Coimbatore Avinashi Highway Hub', 'High power highway charging station located on Avinashi Road.', 'Avinashi Road, Near Airport Junction, Coimbatore', 'Avinashi Road', 'Coimbatore', 'Tamil Nadu', '641014', 11.033100, 77.037800, '+914222591144', '00:00:00', '23:59:59', 'All Days', '5 bays with coffee shop & clean restrooms', 'ACTIVE', TRUE)
ON DUPLICATE KEY UPDATE `name` = `name`;

-- Insert Amenities
INSERT INTO `station_amenities` (`id`, `station_id`, `amenity_name`) VALUES
(1, 1, '24/7 Security'), (2, 1, 'Free Wi-Fi'), (3, 1, 'Restroom'), (4, 1, 'EV Waiting Lounge'),
(5, 2, 'Restroom'), (6, 2, 'Tea & Snacks Kiosk'), (7, 2, 'CCTV'),
(8, 3, 'Food Court'), (9, 3, 'Restroom'), (10, 3, 'Convenience Store'), (11, 3, 'ATM'),
(12, 4, 'Restroom'), (13, 4, 'Shopping Arcade'),
(14, 5, 'Restroom'), (15, 5, 'Café'), (16, 5, 'Waiting Room'),
(17, 6, 'Tourist Information Desk'), (18, 6, 'Café'), (19, 6, 'Restroom'),
(20, 7, 'Cafeteria'), (21, 7, 'Restroom'), (22, 7, 'Free Wi-Fi'), (23, 7, 'Work Desks'),
(24, 8, 'Waiting Area'), (25, 8, 'Restroom'), (26, 8, 'Drinking Water'),
(27, 9, 'Beach Promenade Access'), (28, 9, 'Café'), (29, 9, 'Restroom'),
(30, 10, 'Shopping Mall Access'), (31, 10, 'Food Court'), (32, 10, 'Multiplex Cinema'), (33, 10, 'Restroom'), (34, 10, 'Wi-Fi'),
(35, 11, 'IT Park Security'), (36, 11, 'Café'), (37, 11, 'Restroom'), (38, 11, 'Work Station'),
(39, 12, 'Marine Walkway'), (40, 12, 'Restroom'), (41, 12, 'Tea Stalls'),
(42, 13, 'Cafés & Dining'), (43, 13, 'Restroom'), (44, 13, 'High Speed Wi-Fi'),
(45, 14, 'Driver Rest Lounge'), (46, 14, 'Shower & Restroom'), (47, 14, '24/7 Diner'),
(48, 15, 'Food Court'), (49, 15, 'Restroom'), (50, 15, 'Wi-Fi'),
(51, 16, 'Highway Diner'), (52, 16, 'Air Conditioned Lounge'), (53, 16, 'Clean Restrooms')
ON DUPLICATE KEY UPDATE `amenity_name` = `amenity_name`;

-- Insert Chargers
INSERT INTO `chargers` (`id`, `station_id`, `charger_identifier`, `charger_type`, `connector_type`, `power_kw`, `price_per_kwh`, `availability_status`, `operational_status`, `description`) VALUES
(1, 1, 'KNR-CLX-DC01', 'DC Fast Charging', 'CCS2', 60.00, 18.50, 'AVAILABLE', 'OPERATIONAL', '60kW Dual Gun CCS2 Rapid Charger Gun A'),
(2, 1, 'KNR-CLX-DC02', 'DC Fast Charging', 'CCS2', 60.00, 18.50, 'AVAILABLE', 'OPERATIONAL', '60kW Dual Gun CCS2 Rapid Charger Gun B'),
(3, 1, 'KNR-CLX-AC01', 'AC Fast Charging', 'Type 2', 22.00, 14.00, 'AVAILABLE', 'OPERATIONAL', '22kW Three-Phase AC Fast Charger'),
(4, 2, 'KNR-THT-DC01', 'DC Fast Charging', 'CCS2', 50.00, 17.50, 'AVAILABLE', 'OPERATIONAL', '50kW Single Gun CCS2 Fast Charger'),
(5, 2, 'KNR-THT-AC01', 'AC Fast Charging', 'Type 2', 7.40, 13.00, 'AVAILABLE', 'OPERATIONAL', '7.4kW Single Phase AC Wallbox'),
(6, 3, 'TLP-HWY-DC01', 'DC Fast Charging', 'CCS2', 120.00, 21.00, 'AVAILABLE', 'OPERATIONAL', '120kW Ultra-Fast CCS2 Gun A'),
(7, 3, 'TLP-HWY-DC02', 'DC Fast Charging', 'CCS2', 120.00, 21.00, 'OCCUPIED', 'OPERATIONAL', '120kW Ultra-Fast CCS2 Gun B'),
(8, 3, 'TLP-HWY-CH01', 'DC Fast Charging', 'CHAdeMO', 50.00, 18.00, 'AVAILABLE', 'OPERATIONAL', '50kW CHAdeMO Fast Charger for compatible EVs'),
(9, 4, 'TLP-TWN-DC01', 'DC Fast Charging', 'CCS2', 50.00, 17.50, 'AVAILABLE', 'OPERATIONAL', '50kW Rapid DC Charger'),
(10, 4, 'TLP-TWN-AC01', 'AC Fast Charging', 'Type 2', 22.00, 14.50, 'AVAILABLE', 'OPERATIONAL', '22kW Fast AC Charger'),
(11, 5, 'THL-NBS-DC01', 'DC Fast Charging', 'CCS2', 60.00, 18.00, 'AVAILABLE', 'OPERATIONAL', '60kW Dual Gun Fast Charger A'),
(12, 5, 'THL-NBS-DC02', 'DC Fast Charging', 'CCS2', 60.00, 18.00, 'AVAILABLE', 'OPERATIONAL', '60kW Dual Gun Fast Charger B'),
(13, 6, 'THL-BCH-AC01', 'AC Fast Charging', 'Type 2', 22.00, 14.00, 'AVAILABLE', 'OPERATIONAL', '22kW AC Destination Charger 1'),
(14, 6, 'THL-BCH-AC02', 'AC Fast Charging', 'Type 2', 22.00, 14.00, 'AVAILABLE', 'OPERATIONAL', '22kW AC Destination Charger 2'),
(15, 7, 'CCJ-CYB-DC01', 'DC Fast Charging', 'CCS2', 120.00, 20.00, 'AVAILABLE', 'OPERATIONAL', '120kW HyperCharger Point A'),
(16, 7, 'CCJ-CYB-DC02', 'DC Fast Charging', 'CCS2', 120.00, 20.00, 'AVAILABLE', 'OPERATIONAL', '120kW HyperCharger Point B'),
(17, 7, 'CCJ-CYB-AC01', 'AC Fast Charging', 'Type 2', 22.00, 14.00, 'AVAILABLE', 'OPERATIONAL', '22kW AC Smart Charger'),
(18, 8, 'CCJ-MVR-DC01', 'DC Fast Charging', 'CCS2', 60.00, 18.50, 'AVAILABLE', 'OPERATIONAL', '60kW Rapid DC Charger Gun A'),
(19, 8, 'CCJ-MVR-DC02', 'DC Fast Charging', 'CCS2', 60.00, 18.50, 'AVAILABLE', 'OPERATIONAL', '60kW Rapid DC Charger Gun B'),
(20, 9, 'CCJ-BCH-AC01', 'AC Fast Charging', 'Type 2', 22.00, 14.00, 'AVAILABLE', 'OPERATIONAL', 'Solar Powered AC Fast Charger'),
(21, 9, 'CCJ-BCH-DC01', 'DC Fast Charging', 'CCS2', 50.00, 17.50, 'AVAILABLE', 'OPERATIONAL', '50kW Rapid Coastal DC Charger'),
(22, 10, 'COK-LLU-DC01', 'DC Fast Charging', 'CCS2', 150.00, 22.00, 'AVAILABLE', 'OPERATIONAL', '150kW Ultra Rapid CCS2 Gun 1'),
(23, 10, 'COK-LLU-DC02', 'DC Fast Charging', 'CCS2', 150.00, 22.00, 'AVAILABLE', 'OPERATIONAL', '150kW Ultra Rapid CCS2 Gun 2'),
(24, 10, 'COK-LLU-DC03', 'DC Fast Charging', 'CHAdeMO', 50.00, 18.50, 'AVAILABLE', 'OPERATIONAL', '50kW Fast CHAdeMO Unit'),
(25, 10, 'COK-LLU-AC01', 'AC Fast Charging', 'Type 2', 22.00, 15.00, 'AVAILABLE', 'OPERATIONAL', '22kW Dual AC Destination Gun'),
(26, 11, 'COK-INF-DC01', 'DC Fast Charging', 'CCS2', 120.00, 20.00, 'AVAILABLE', 'OPERATIONAL', '120kW SuperFast Tech Hub Charger 1'),
(27, 11, 'COK-INF-DC02', 'DC Fast Charging', 'CCS2', 120.00, 20.00, 'AVAILABLE', 'OPERATIONAL', '120kW SuperFast Tech Hub Charger 2'),
(28, 11, 'COK-INF-AC01', 'AC Fast Charging', 'Type 2', 22.00, 14.50, 'AVAILABLE', 'OPERATIONAL', '22kW AC Workday Charger'),
(29, 12, 'COK-MRN-DC01', 'DC Fast Charging', 'CCS2', 50.00, 18.00, 'AVAILABLE', 'OPERATIONAL', '50kW Waterfront Fast Charger'),
(30, 12, 'COK-MRN-AC01', 'AC Fast Charging', 'Type 2', 22.00, 14.00, 'AVAILABLE', 'OPERATIONAL', '22kW City AC Fast Charger'),
(31, 13, 'BLR-IDR-DC01', 'DC Fast Charging', 'CCS2', 60.00, 19.50, 'AVAILABLE', 'OPERATIONAL', '60kW Metro Point Fast DC'),
(32, 13, 'BLR-IDR-AC01', 'AC Fast Charging', 'Type 2', 22.00, 15.00, 'AVAILABLE', 'OPERATIONAL', '22kW Fast AC Point'),
(33, 14, 'BLR-ELC-DC01', 'DC Fast Charging', 'CCS2', 150.00, 22.50, 'AVAILABLE', 'OPERATIONAL', '150kW Expressway Ultra Charger Gun A'),
(34, 14, 'BLR-ELC-DC02', 'DC Fast Charging', 'CCS2', 150.00, 22.50, 'AVAILABLE', 'OPERATIONAL', '150kW Expressway Ultra Charger Gun B'),
(35, 15, 'MAA-OMR-DC01', 'DC Fast Charging', 'CCS2', 60.00, 19.00, 'AVAILABLE', 'OPERATIONAL', '60kW IT Corridor Fast Charger'),
(36, 15, 'MAA-OMR-AC01', 'AC Fast Charging', 'Type 2', 22.00, 14.50, 'AVAILABLE', 'OPERATIONAL', '22kW AC Fast Charger'),
(37, 16, 'CJB-AVN-DC01', 'DC Fast Charging', 'CCS2', 120.00, 20.50, 'AVAILABLE', 'OPERATIONAL', '120kW Highway Rapid Charger')
ON DUPLICATE KEY UPDATE `charger_identifier` = `charger_identifier`;

-- Insert Bookings
INSERT INTO `bookings` (`id`, `booking_reference`, `user_id`, `station_id`, `charger_id`, `booking_date`, `start_time`, `end_time`, `duration_minutes`, `estimated_energy_kwh`, `estimated_amount`, `booking_status`, `payment_status`, `created_at`) VALUES
(1, 'EVC-20261013-0001', 2, 1, 1, '2026-10-14', '10:00:00', '11:00:00', 60, 30.00, 555.00, 'CONFIRMED', 'PAID', '2026-10-13 08:30:00'),
(2, 'EVC-20261013-0002', 3, 10, 22, '2026-10-14', '14:00:00', '14:45:00', 45, 45.00, 990.00, 'CONFIRMED', 'PAID', '2026-10-13 09:15:00'),
(3, 'EVC-20261013-0003', 4, 7, 15, '2026-10-15', '09:00:00', '10:00:00', 60, 40.00, 800.00, 'PENDING_PAYMENT', 'PENDING', '2026-10-13 10:00:00'),
(4, 'EVC-20261012-0004', 2, 3, 6, '2026-10-12', '11:00:00', '11:40:00', 40, 35.00, 735.00, 'COMPLETED', 'PAID', '2026-10-12 10:20:00'),
(5, 'EVC-20261011-0005', 3, 5, 11, '2026-10-11', '16:00:00', '17:00:00', 60, 25.00, 450.00, 'CANCELLED', 'REFUNDED', '2026-10-11 14:00:00')
ON DUPLICATE KEY UPDATE `booking_reference` = `booking_reference`;

-- Insert Payments
INSERT INTO `payments` (`id`, `payment_reference`, `booking_id`, `user_id`, `amount`, `currency`, `payment_method`, `payment_status`, `is_demo`, `paid_at`, `created_at`) VALUES
(1, 'PAY-DEMO-20261013-0001', 1, 2, 555.00, 'INR', 'DEMO', 'SUCCESS', TRUE, '2026-10-13 08:32:15', '2026-10-13 08:30:10'),
(2, 'PAY-DEMO-20261013-0002', 2, 3, 990.00, 'INR', 'DEMO', 'SUCCESS', TRUE, '2026-10-13 09:16:40', '2026-10-13 09:15:20'),
(3, 'PAY-DEMO-20261012-0004', 4, 2, 735.00, 'INR', 'DEMO', 'SUCCESS', TRUE, '2026-10-12 10:22:05', '2026-10-12 10:20:15'),
(4, 'PAY-DEMO-20261011-0005', 5, 3, 450.00, 'INR', 'DEMO', 'REFUNDED', TRUE, '2026-10-11 14:05:00', '2026-10-11 14:00:30')
ON DUPLICATE KEY UPDATE `payment_reference` = `payment_reference`;

-- Insert Charging Sessions
INSERT INTO `charging_sessions` (`id`, `session_reference`, `booking_id`, `user_id`, `charger_id`, `session_status`, `start_time`, `end_time`, `energy_consumed_kwh`, `initial_soc_percent`, `final_soc_percent`, `final_amount`, `notes`, `is_simulated`) VALUES
(1, 'SES-20261012-0001', 4, 2, 6, 'COMPLETED', '2026-10-12 11:02:10', '2026-10-12 11:41:30', 36.40, 22, 85, 764.40, 'Simulated demonstration charging session completed smoothly.', TRUE),
(2, 'SES-20261014-0002', 1, 2, 1, 'SCHEDULED', NULL, NULL, 0.00, 20, 80, 0.00, 'Scheduled upcoming charging session.', TRUE)
ON DUPLICATE KEY UPDATE `session_reference` = `session_reference`;

-- Insert Settings
INSERT INTO `application_settings` (`setting_key`, `setting_value`, `description`) VALUES
('CANCELLATION_WINDOW_HOURS', '2', 'Number of hours before slot start time allowed for full refund cancellation'),
('PAYMENT_TIMEOUT_MINUTES', '15', 'Timeout in minutes before an unpaid booking expires'),
('DEFAULT_PAYMENT_MODE', 'DEMO', 'Default active payment mode (DEMO or REAL)'),
('SYSTEM_TAX_PERCENTAGE', '0.00', 'Applicable GST/tax rate (included in price)'),
('SUPPORT_PHONE', '+91 98765 43210', 'Customer care telephone line'),
('SUPPORT_EMAIL', 'support@evcharge.com', 'Customer support email address')
ON DUPLICATE KEY UPDATE `setting_value` = `setting_value`;

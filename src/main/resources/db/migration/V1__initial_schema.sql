-- V1__initial_schema.sql
-- Flyway Migration Script: Initial Schema for EV Charging Station Management System

CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `full_name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(150) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `role` VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `uk_users_email` UNIQUE (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_users_role` ON `users` (`role`);
CREATE INDEX `idx_users_is_active` ON `users` (`is_active`);

CREATE TABLE IF NOT EXISTS `user_vehicles` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `registration_number` VARCHAR(50) NOT NULL,
  `make` VARCHAR(50) NOT NULL,
  `model` VARCHAR(100) NOT NULL,
  `vehicle_type` VARCHAR(50) NOT NULL,
  `preferred_connector` VARCHAR(50) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_vehicles_user` FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `uk_vehicles_reg_no` UNIQUE (`registration_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_vehicles_user_id` ON `user_vehicles` (`user_id`);

CREATE TABLE IF NOT EXISTS `stations` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(150) NOT NULL,
  `description` TEXT,
  `address` VARCHAR(255) NOT NULL,
  `area` VARCHAR(100) NOT NULL,
  `city` VARCHAR(100) NOT NULL,
  `state` VARCHAR(100) NOT NULL,
  `postal_code` VARCHAR(20) NOT NULL,
  `latitude` DECIMAL(10, 7) NOT NULL,
  `longitude` DECIMAL(10, 7) NOT NULL,
  `contact_number` VARCHAR(30) NOT NULL,
  `opening_time` TIME NOT NULL DEFAULT '00:00:00',
  `closing_time` TIME NOT NULL DEFAULT '23:59:59',
  `operating_days` VARCHAR(100) NOT NULL DEFAULT 'All Days',
  `parking_information` VARCHAR(255),
  `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  `is_demo` BOOLEAN NOT NULL DEFAULT FALSE,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_stations_city` ON `stations` (`city`);
CREATE INDEX `idx_stations_area` ON `stations` (`area`);
CREATE INDEX `idx_stations_status` ON `stations` (`status`);
CREATE INDEX `idx_stations_lat_lng` ON `stations` (`latitude`, `longitude`);

CREATE TABLE IF NOT EXISTS `station_amenities` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `station_id` BIGINT NOT NULL,
  `amenity_name` VARCHAR(100) NOT NULL,
  CONSTRAINT `fk_amenities_station` FOREIGN KEY (`station_id`)
    REFERENCES `stations` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_amenities_station_id` ON `station_amenities` (`station_id`);

CREATE TABLE IF NOT EXISTS `chargers` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `station_id` BIGINT NOT NULL,
  `charger_identifier` VARCHAR(50) NOT NULL,
  `charger_type` VARCHAR(50) NOT NULL,
  `connector_type` VARCHAR(50) NOT NULL,
  `power_kw` DECIMAL(6, 2) NOT NULL,
  `price_per_kwh` DECIMAL(8, 2) NOT NULL,
  `availability_status` VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
  `operational_status` VARCHAR(30) NOT NULL DEFAULT 'OPERATIONAL',
  `description` VARCHAR(255),
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_chargers_station` FOREIGN KEY (`station_id`)
    REFERENCES `stations` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_chargers_station_id` ON `chargers` (`station_id`);
CREATE INDEX `idx_chargers_connector` ON `chargers` (`connector_type`);
CREATE INDEX `idx_chargers_type` ON `chargers` (`charger_type`);
CREATE INDEX `idx_chargers_avail` ON `chargers` (`availability_status`);

CREATE TABLE IF NOT EXISTS `bookings` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `booking_reference` VARCHAR(50) NOT NULL,
  `user_id` BIGINT NOT NULL,
  `station_id` BIGINT NOT NULL,
  `charger_id` BIGINT NOT NULL,
  `booking_date` DATE NOT NULL,
  `start_time` TIME NOT NULL,
  `end_time` TIME NOT NULL,
  `duration_minutes` INT NOT NULL,
  `estimated_energy_kwh` DECIMAL(8, 2) NOT NULL,
  `estimated_amount` DECIMAL(10, 2) NOT NULL,
  `booking_status` VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT',
  `payment_status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  `cancellation_reason` VARCHAR(255),
  `cancelled_at` TIMESTAMP NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `uk_bookings_reference` UNIQUE (`booking_reference`),
  CONSTRAINT `fk_bookings_user` FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_bookings_station` FOREIGN KEY (`station_id`)
    REFERENCES `stations` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_bookings_charger` FOREIGN KEY (`charger_id`)
    REFERENCES `chargers` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_bookings_user_id` ON `bookings` (`user_id`);
CREATE INDEX `idx_bookings_charger_slot` ON `bookings` (`charger_id`, `booking_date`, `start_time`, `end_time`);
CREATE INDEX `idx_bookings_status` ON `bookings` (`booking_status`);
CREATE INDEX `idx_bookings_date` ON `bookings` (`booking_date`);

CREATE TABLE IF NOT EXISTS `payments` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `payment_reference` VARCHAR(100) NOT NULL,
  `booking_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `amount` DECIMAL(10, 2) NOT NULL,
  `currency` VARCHAR(10) NOT NULL DEFAULT 'INR',
  `payment_method` VARCHAR(50) NOT NULL DEFAULT 'DEMO',
  `payment_status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  `gateway_order_id` VARCHAR(100),
  `gateway_payment_id` VARCHAR(100),
  `gateway_signature` VARCHAR(255),
  `failure_reason` VARCHAR(255),
  `is_demo` BOOLEAN NOT NULL DEFAULT TRUE,
  `paid_at` TIMESTAMP NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `uk_payments_ref` UNIQUE (`payment_reference`),
  CONSTRAINT `fk_payments_booking` FOREIGN KEY (`booking_id`)
    REFERENCES `bookings` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_payments_user` FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_payments_booking_id` ON `payments` (`booking_id`);
CREATE INDEX `idx_payments_user_id` ON `payments` (`user_id`);
CREATE INDEX `idx_payments_status` ON `payments` (`payment_status`);

CREATE TABLE IF NOT EXISTS `charging_sessions` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `session_reference` VARCHAR(50) NOT NULL,
  `booking_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `charger_id` BIGINT NOT NULL,
  `session_status` VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
  `start_time` TIMESTAMP NULL,
  `end_time` TIMESTAMP NULL,
  `energy_consumed_kwh` DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
  `initial_soc_percent` INT DEFAULT 20,
  `final_soc_percent` INT DEFAULT 80,
  `final_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  `notes` TEXT,
  `is_simulated` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `uk_sessions_ref` UNIQUE (`session_reference`),
  CONSTRAINT `uk_sessions_booking` UNIQUE (`booking_id`),
  CONSTRAINT `fk_sessions_booking` FOREIGN KEY (`booking_id`)
    REFERENCES `bookings` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_sessions_user` FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_sessions_charger` FOREIGN KEY (`charger_id`)
    REFERENCES `chargers` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_sessions_user_id` ON `charging_sessions` (`user_id`);
CREATE INDEX `idx_sessions_status` ON `charging_sessions` (`session_status`);

CREATE TABLE IF NOT EXISTS `booking_status_history` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `booking_id` BIGINT NOT NULL,
  `previous_status` VARCHAR(30),
  `new_status` VARCHAR(30) NOT NULL,
  `changed_by_user_id` BIGINT,
  `remarks` VARCHAR(255),
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_history_booking` FOREIGN KEY (`booking_id`)
    REFERENCES `bookings` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_history_user` FOREIGN KEY (`changed_by_user_id`)
    REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_history_booking_id` ON `booking_status_history` (`booking_id`);

CREATE TABLE IF NOT EXISTS `admin_audit_logs` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `admin_user_id` BIGINT NOT NULL,
  `action` VARCHAR(100) NOT NULL,
  `entity_type` VARCHAR(50) NOT NULL,
  `entity_id` VARCHAR(50),
  `details` TEXT,
  `ip_address` VARCHAR(50),
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_audit_admin` FOREIGN KEY (`admin_user_id`)
    REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX `idx_audit_admin_id` ON `admin_audit_logs` (`admin_user_id`);
CREATE INDEX `idx_audit_action` ON `admin_audit_logs` (`action`);

CREATE TABLE IF NOT EXISTS `application_settings` (
  `setting_key` VARCHAR(100) PRIMARY KEY,
  `setting_value` TEXT NOT NULL,
  `description` VARCHAR(255),
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

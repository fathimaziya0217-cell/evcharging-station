-- schema-test.sql for in-memory H2 tests

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  phone VARCHAR(20) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_vehicles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  registration_number VARCHAR(50) NOT NULL UNIQUE,
  make VARCHAR(50) NOT NULL,
  model VARCHAR(100) NOT NULL,
  vehicle_type VARCHAR(50) NOT NULL,
  preferred_connector VARCHAR(50) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS stations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  description TEXT,
  address VARCHAR(255) NOT NULL,
  area VARCHAR(100) NOT NULL,
  city VARCHAR(100) NOT NULL,
  state VARCHAR(100) NOT NULL,
  postal_code VARCHAR(20) NOT NULL,
  latitude DECIMAL(10, 7) NOT NULL,
  longitude DECIMAL(10, 7) NOT NULL,
  contact_number VARCHAR(30) NOT NULL,
  opening_time TIME DEFAULT '00:00:00',
  closing_time TIME DEFAULT '23:59:59',
  operating_days VARCHAR(100) DEFAULT 'All Days',
  parking_information VARCHAR(255),
  status VARCHAR(30) DEFAULT 'ACTIVE',
  is_demo BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS station_amenities (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  station_id BIGINT NOT NULL,
  amenity_name VARCHAR(100) NOT NULL,
  FOREIGN KEY (station_id) REFERENCES stations(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chargers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  station_id BIGINT NOT NULL,
  charger_identifier VARCHAR(50) NOT NULL,
  charger_type VARCHAR(50) NOT NULL,
  connector_type VARCHAR(50) NOT NULL,
  power_kw DECIMAL(6, 2) NOT NULL,
  price_per_kwh DECIMAL(8, 2) NOT NULL,
  availability_status VARCHAR(30) DEFAULT 'AVAILABLE',
  operational_status VARCHAR(30) DEFAULT 'OPERATIONAL',
  description VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (station_id) REFERENCES stations(id)
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  booking_reference VARCHAR(50) NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,
  station_id BIGINT NOT NULL,
  charger_id BIGINT NOT NULL,
  booking_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  duration_minutes INT NOT NULL,
  estimated_energy_kwh DECIMAL(8, 2) NOT NULL,
  estimated_amount DECIMAL(10, 2) NOT NULL,
  booking_status VARCHAR(30) DEFAULT 'PENDING_PAYMENT',
  payment_status VARCHAR(30) DEFAULT 'PENDING',
  cancellation_reason VARCHAR(255),
  cancelled_at TIMESTAMP NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (station_id) REFERENCES stations(id),
  FOREIGN KEY (charger_id) REFERENCES chargers(id)
);

CREATE TABLE IF NOT EXISTS payments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  payment_reference VARCHAR(100) NOT NULL UNIQUE,
  booking_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  amount DECIMAL(10, 2) NOT NULL,
  currency VARCHAR(10) DEFAULT 'INR',
  payment_method VARCHAR(50) DEFAULT 'DEMO',
  payment_status VARCHAR(30) DEFAULT 'PENDING',
  gateway_order_id VARCHAR(100),
  gateway_payment_id VARCHAR(100),
  gateway_signature VARCHAR(255),
  failure_reason VARCHAR(255),
  is_demo BOOLEAN DEFAULT TRUE,
  paid_at TIMESTAMP NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (booking_id) REFERENCES bookings(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS charging_sessions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_reference VARCHAR(50) NOT NULL UNIQUE,
  booking_id BIGINT NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,
  charger_id BIGINT NOT NULL,
  session_status VARCHAR(30) DEFAULT 'SCHEDULED',
  start_time TIMESTAMP NULL,
  end_time TIMESTAMP NULL,
  energy_consumed_kwh DECIMAL(8, 2) DEFAULT 0.00,
  initial_soc_percent INT DEFAULT 20,
  final_soc_percent INT DEFAULT 80,
  final_amount DECIMAL(10, 2) DEFAULT 0.00,
  notes TEXT,
  is_simulated BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (booking_id) REFERENCES bookings(id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (charger_id) REFERENCES chargers(id)
);

CREATE TABLE IF NOT EXISTS booking_status_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  booking_id BIGINT NOT NULL,
  previous_status VARCHAR(30),
  new_status VARCHAR(30) NOT NULL,
  changed_by_user_id BIGINT,
  remarks VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS admin_audit_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  admin_user_id BIGINT NOT NULL,
  action VARCHAR(100) NOT NULL,
  entity_type VARCHAR(50) NOT NULL,
  entity_id VARCHAR(50),
  details TEXT,
  ip_address VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (admin_user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS application_settings (
  setting_key VARCHAR(100) PRIMARY KEY,
  setting_value TEXT NOT NULL,
  description VARCHAR(255),
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- EV Charging Station Management System
-- Database User Creation Script: database/create-app-user.sql
-- ============================================================================

-- Create the database if it doesn't already exist
CREATE DATABASE IF NOT EXISTS `ev_charging_db`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- Create an application-specific MySQL user (avoiding root account usage)
-- In production, replace 'ev_secure_password_123' with a strong random password!
CREATE USER IF NOT EXISTS 'ev_app'@'localhost' IDENTIFIED BY 'ev_secure_password_123';
CREATE USER IF NOT EXISTS 'ev_app'@'%' IDENTIFIED BY 'ev_secure_password_123';

-- Grant required DDL and DML privileges on the application database
GRANT ALL PRIVILEGES ON `ev_charging_db`.* TO 'ev_app'@'localhost';
GRANT ALL PRIVILEGES ON `ev_charging_db`.* TO 'ev_app'@'%';

-- Apply privileges
FLUSH PRIVILEGES;

-- Verification query
SHOW GRANTS FOR 'ev_app'@'localhost';

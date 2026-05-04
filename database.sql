-- SportFinder Database Schema
-- Created: May 4, 2026

-- ─────────────────────────────────────────────
-- Create Database
-- ─────────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS sportfinder;
USE sportfinder;

-- ─────────────────────────────────────────────
-- Table: users
-- ─────────────────────────────────────────────
CREATE TABLE users (
  id INT(11) NOT NULL AUTO_INCREMENT,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role ENUM('Trainee', 'coach', 'court owner') NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY login_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ─────────────────────────────────────────────
-- Table: user_profiles
-- ─────────────────────────────────────────────
CREATE TABLE user_profiles (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  bio TEXT NOT NULL,
  location VARCHAR(150) NOT NULL,
  avatar VARCHAR(255) NOT NULL,
  joined_date DATE NOT NULL,
  PRIMARY KEY (id),
  KEY user_id (user_id),
  CONSTRAINT user_profiles_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ─────────────────────────────────────────────
-- Table: sports
-- ─────────────────────────────────────────────
CREATE TABLE sports (
  id INT(11) NOT NULL AUTO_INCREMENT,
  sport_name VARCHAR(50) NOT NULL UNIQUE,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY sport_name (sport_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ─────────────────────────────────────────────
-- Table: courts
-- ─────────────────────────────────────────────
CREATE TABLE courts (
  id INT(11) NOT NULL AUTO_INCREMENT,
  owner_id INT(11) NOT NULL,
  sport_id INT(11) NOT NULL,
  name VARCHAR(150) NOT NULL,
  court_type ENUM('indoor', 'outdoor') NOT NULL,
  price_per_hour DECIMAL(10,2) NOT NULL,
  address VARCHAR(255) NOT NULL,
  city VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  image_url VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY owner_id (owner_id),
  KEY sport_id (sport_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ─────────────────────────────────────────────
-- Table: checkboxes_incourt_creation (Amenities)
-- ─────────────────────────────────────────────
CREATE TABLE checkboxes_incourt_creation (
  id INT(11) NOT NULL AUTO_INCREMENT,
  amenity_name VARCHAR(50) NOT NULL UNIQUE,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY amenity_name (amenity_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ─────────────────────────────────────────────
-- Table: court_amenities
-- ─────────────────────────────────────────────
CREATE TABLE court_amenities (
  id INT(11) NOT NULL AUTO_INCREMENT,
  court_id INT(11) NOT NULL,
  amenity_id INT(11) NOT NULL,
  PRIMARY KEY (id),
  KEY court_id (court_id),
  KEY amenity_id (amenity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ─────────────────────────────────────────────
-- Table: court_schedule
-- ─────────────────────────────────────────────
CREATE TABLE court_schedule (
  id INT(11) NOT NULL AUTO_INCREMENT,
  court_id INT(11) NOT NULL,
  day_of_week ENUM('Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun') NOT NULL,
  open_time TIME NOT NULL,
  close_time TIME NOT NULL,
  is_available TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  KEY court_id (court_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ─────────────────────────────────────────────
-- Table: court_bookings
-- ─────────────────────────────────────────────
CREATE TABLE court_bookings (
  id INT(11) NOT NULL AUTO_INCREMENT,
  court_id INT(11) NOT NULL,
  trainee_id INT(11) NOT NULL,
  booking_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  duration_hours DECIMAL(4,2) NOT NULL,
  players_count INT(11),
  total_price DECIMAL(10,2) NOT NULL,
  contact_phone VARCHAR(20),
  notes TEXT,
  status ENUM('Pending', 'Accepted', 'Declined', 'Cancelled', 'Modified') NOT NULL DEFAULT 'Pending',
  attendance_confirmed TINYINT(1) NOT NULL DEFAULT 0,
  confirmed_by INT(11),
  confirmed_at TIMESTAMP NULL DEFAULT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY court_id (court_id),
  KEY trainee_id (trainee_id),
  KEY idx_court_bookings_attendance_confirmed (attendance_confirmed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ─────────────────────────────────────────────
-- Table: trainings
-- ─────────────────────────────────────────────
CREATE TABLE trainings (
  id INT(11) NOT NULL AUTO_INCREMENT,
  coach_id INT(11) NOT NULL,
  sport_id INT(11) NOT NULL,
  name VARCHAR(150) NOT NULL,
  level ENUM('Beginner', 'Intermediate', 'Advanced', 'Professional', 'All Levels') NOT NULL,
  specialty VARCHAR(150),
  price_per_hour DECIMAL(10,2) NOT NULL,
  years_experience INT(11),
  description TEXT,
  goals TEXT,
  min_sessions INT(11),
  location VARCHAR(150),
  equipment TEXT,
  requirements TEXT,
  certifications TEXT,
  achievements TEXT,
  image_url VARCHAR(255),
  status ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY coach_id (coach_id),
  KEY sport_id (sport_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ─────────────────────────────────────────────
-- Table: training_availability
-- ─────────────────────────────────────────────
CREATE TABLE training_availability (
  id INT(11) NOT NULL AUTO_INCREMENT,
  training_id INT(11) NOT NULL,
  day_of_week ENUM('Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun') NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  is_available TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  KEY training_id (training_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ─────────────────────────────────────────────
-- Table: training_bookings
-- ─────────────────────────────────────────────
CREATE TABLE training_bookings (
  id INT(11) NOT NULL AUTO_INCREMENT,
  training_id INT(11) NOT NULL,
  trainee_id INT(11) NOT NULL,
  preferred_date DATE NOT NULL,
  preferred_time TIME NOT NULL,
  sessions_count INT(11),
  level_requested VARCHAR(50),
  price_per_session DECIMAL(10,2),
  total_price DECIMAL(10,2),
  message TEXT,
  status ENUM('Pending', 'Accepted', 'Declined', 'Cancelled', 'Modified') NOT NULL DEFAULT 'Pending',
  attendance_confirmed TINYINT(1) NOT NULL DEFAULT 0,
  confirmed_by INT(11),
  confirmed_at TIMESTAMP NULL DEFAULT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  coach_note TEXT,
  PRIMARY KEY (id),
  KEY training_id (training_id),
  KEY trainee_id (trainee_id),
  KEY idx_training_bookings_attendance_confirmed (attendance_confirmed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ─────────────────────────────────────────────
-- Insert Sample Data: Users
-- ─────────────────────────────────────────────
INSERT INTO users (id, full_name, email, password, role, is_active) VALUES
(1, 'Admin User', 'admin@sportfinder.com', '$2y$10$pXF7MHnD8UhtvpLO4bt7e.ejb4LJ40w82KQemQNSHf8k12LqaeNaK', 'coach', 1),
(2, 'John Smith', 'john@example.com', '$2y$10$pXF7MHnD8UhtvpLO4bt7e.ejb4LJ40w82KQemQNSHf8k12LqaeNaK', 'coach', 1),
(3, 'Sarah Johnson', 'sarah@example.com', '$2y$10$pXF7MHnD8UhtvpLO4bt7e.ejb4LJ40w82KQemQNSHf8k12LqaeNaK', 'court owner', 1),
(4, 'Mike Davisaaaaa', 'mike@example.com', '$2y$10$pXF7MHnD8UhtvpLO4bt7e.ejb4LJ40w82KQemQNSHf8k12LqaeNaK', 'Trainee', 1),
(5, 'Emily Wilson', 'emily@example.com', '$2y$10$pXF7MHnD8UhtvpLO4bt7e.ejb4LJ40w82KQemQNSHf8k12LqaeNaK', 'Trainee', 1),
(8, 'mohamed hassan', 'mohamed3534@gmail.com', '$2y$10$3o9NSmCW2GeGVVGYXqulgetZFNXTd9so8i0PNwlFpDTdrRP3fsN4y', 'Trainee', 1);

-- ─────────────────────────────────────────────
-- Insert Sample Data: User Profiles
-- ─────────────────────────────────────────────
INSERT INTO user_profiles (id, user_id, phone, bio, location, avatar, joined_date) VALUES
(1, 1, '+1234567890', 'Professional Tennis Coach', 'New York, NY', '/images/avatar1.jpg', '2025-01-01'),
(2, 2, '+1234567891', 'Basketball Trainer', 'Los Angeles, CA', '/images/avatar2.jpg', '2025-02-15'),
(3, 3, '+1234567892', 'Court Owner', 'Chicago, IL', '/images/avatar3.jpg', '2025-03-10'),
(4, 4, '+1234567893', 'Tennis Enthusiast', 'Boston, MA', '/images/avatar4.jpg', '2025-04-05'),
(5, 5, '+1234567894', 'Fitness & Sports Lover', 'Miami, FL', '/images/avatar5.jpg', '2025-04-10'),
(7, 8, '', '', '', '/images/default-avatar.jpg', '2026-04-30');

-- ─────────────────────────────────────────────
-- Insert Sample Data: Sports
-- ─────────────────────────────────────────────
INSERT INTO sports (id, sport_name, is_active) VALUES
(1, 'Tennis', 1),
(2, 'Basketball', 1),
(3, 'Football', 1),
(4, 'Badminton', 1),
(5, 'Squash', 1),
(6, 'Volleyball', 1);

-- ─────────────────────────────────────────────
-- Insert Sample Data: Amenities
-- ─────────────────────────────────────────────
INSERT INTO checkboxes_incourt_creation (id, amenity_name, is_active) VALUES
(1, 'Parking', 1),
(2, 'Lights', 1),
(3, 'Equipment Rental', 1),
(4, 'Locker Room', 1),
(5, 'Cafeteria', 1),
(6, 'WiFi', 1);

-- ─────────────────────────────────────────────
-- Insert Sample Data: Courts
-- ─────────────────────────────────────────────
INSERT INTO courts (id, owner_id, sport_id, name, court_type, price_per_hour, address, city, description, image_url) VALUES
(1, 3, 4, 'Central Tennis Complex', 'indoor', 61.00, '123 Main St', 'New York', 'Premium tennis facility with professional coaching', 'https://blog.tui-blue.com/wp-content/uploads/tui-blue-crystal-bay-resort-tennis.jpg'),
(2, 3, 2, 'Downtown Basketball Arena', 'outdoor', 35.00, '456 Sports Ave', 'Los Angeles', 'Professional basketball courts with lighting', 'https://bharatnatural.co/cdn/shop/articles/Fiba-Approval-Solid-Birch-Maple-Wooden-Plywood-Sport-Flooring-for-Basketball-Court.webp?v=1740402862&width=750'),
(3, 3, 3, 'City Football Field', 'outdoor', 45.00, '789 Green Park', 'Chicago', 'Large soccer field available for rent', 'https://d26itsb5vlqdeq.cloudfront.net/image/98CE1963-0E26-F9B2-D4713C65A9683442');

-- ─────────────────────────────────────────────
-- Insert Sample Data: Court Amenities
-- ─────────────────────────────────────────────
INSERT INTO court_amenities (id, court_id, amenity_id) VALUES
(7, 2, 1), (8, 2, 2), (9, 2, 5), (10, 2, 6),
(11, 3, 1), (12, 3, 2), (13, 3, 5),
(20, 1, 5), (21, 1, 3), (22, 1, 2), (23, 1, 4), (24, 1, 1), (25, 1, 6);

-- ─────────────────────────────────────────────
-- Insert Sample Data: Court Schedule
-- ─────────────────────────────────────────────
INSERT INTO court_schedule (id, court_id, day_of_week, open_time, close_time, is_available) VALUES
-- Court 2 (Downtown Basketball Arena)
(8, 2, 'Mon', '08:00:00', '20:00:00', 1),
(9, 2, 'Tue', '08:00:00', '20:00:00', 1),
(10, 2, 'Wed', '08:00:00', '20:00:00', 1),
(11, 2, 'Thu', '08:00:00', '20:00:00', 1),
(12, 2, 'Fri', '08:00:00', '21:00:00', 1),
(13, 2, 'Sat', '09:00:00', '22:00:00', 1),
(14, 2, 'Sun', '09:00:00', '20:00:00', 1),
-- Court 1 (Central Tennis Complex)
(22, 1, 'Mon', '06:00:00', '22:00:00', 1),
(23, 1, 'Tue', '06:00:00', '22:00:00', 1),
(24, 1, 'Wed', '06:00:00', '22:00:00', 1),
(25, 1, 'Thu', '06:00:00', '22:00:00', 1),
(26, 1, 'Fri', '06:00:00', '22:00:00', 1),
(27, 1, 'Sat', '06:00:00', '22:00:00', 1),
(28, 1, 'Sun', '06:00:00', '22:00:00', 1);

-- ─────────────────────────────────────────────
-- Insert Sample Data: Court Bookings
-- ─────────────────────────────────────────────
INSERT INTO court_bookings (id, court_id, trainee_id, booking_date, start_time, end_time, duration_hours, players_count, total_price, contact_phone, status) VALUES
(1, 2, 4, '2026-04-23', '09:39:00', '11:39:00', 2.00, 4, 70.00, '+01011111111', 'Pending'),
(2, 1, 4, '2026-04-30', '07:55:00', '13:55:00', 6.00, 4, 366.00, '+01011111111', 'Pending');

-- ─────────────────────────────────────────────
-- Insert Sample Data: Trainings
-- ─────────────────────────────────────────────
INSERT INTO trainings (id, coach_id, sport_id, name, level, specialty, price_per_hour, years_experience, description, goals, min_sessions, location, equipment, requirements, certifications, achievements, status) VALUES
(1, 2, 1, 'Professional Tennis Training', 'Intermediate', 'Competitive Play', 60.00, 15, 'Advanced tennis training for competitive players', 'Improve serve and court strategy', 4, 'Central Park', 'Rackets, Tennis Balls', 'Basic tennis knowledge', 'ATP Certified', 'Multiple Grand Slam appearances', 'Active'),
(2, 2, 2, 'Basketball Skills Mastery', 'Beginner', 'Offensive Skills', 45.00, 10, 'Learn fundamental basketball techniques', 'Build strong basketball foundation', 3, 'Downtown Arena', 'Basketballs, Cones', 'None', 'NBA Coach Certified', 'Trained 50+ players', 'Active'),
(3, 1, 1, 'Weekend Tennis Bootcamp', 'All Levels', 'Fitness & Tennis', 55.00, 12, 'Intensive weekend tennis training', 'Build endurance and technique', 2, 'Sports Complex', 'All equipment provided', 'Enthusiasm required', 'Professional Coach', 'High success rate', 'Active'),
(4, 1, 4, 'training', 'Intermediate', 'training', 30.00, 2, 'training training training training training training', 'training training training training training', 1, 'training', 'training', 'training', 'training', 'training', 'Active');

-- ─────────────────────────────────────────────
-- Insert Sample Data: Training Availability
-- ─────────────────────────────────────────────
INSERT INTO training_availability (id, training_id, day_of_week, start_time, end_time, is_available) VALUES
(1, 1, 'Mon', '15:00:00', '18:00:00', 1),
(2, 1, 'Wed', '15:00:00', '18:00:00', 1),
(3, 1, 'Fri', '15:00:00', '18:00:00', 1),
(4, 2, 'Tue', '10:00:00', '12:00:00', 1),
(5, 2, 'Thu', '10:00:00', '12:00:00', 1),
(6, 2, 'Sat', '14:00:00', '16:00:00', 1),
(7, 3, 'Sat', '09:00:00', '12:00:00', 1),
(8, 3, 'Sun', '09:00:00', '12:00:00', 1),
(9, 4, 'Tue', '18:17:00', '20:17:00', 1),
(10, 4, 'Wed', '18:17:00', '20:17:00', 1);

-- ─────────────────────────────────────────────
-- Insert Sample Data: Training Bookings
-- ─────────────────────────────────────────────
INSERT INTO training_bookings (id, training_id, trainee_id, preferred_date, preferred_time, message, status) VALUES
(1, 1, 4, '2026-04-29', '01:29:00', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 'Pending'),
(2, 1, 4, '2026-04-29', '01:32:00', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 'Pending');

-- ─────────────────────────────────────────────
-- Alter table AUTO_INCREMENT values
-- ─────────────────────────────────────────────
ALTER TABLE users AUTO_INCREMENT = 9;
ALTER TABLE user_profiles AUTO_INCREMENT = 8;
ALTER TABLE sports AUTO_INCREMENT = 7;
ALTER TABLE courts AUTO_INCREMENT = 4;
ALTER TABLE checkboxes_incourt_creation AUTO_INCREMENT = 7;
ALTER TABLE court_amenities AUTO_INCREMENT = 26;
ALTER TABLE court_schedule AUTO_INCREMENT = 29;
ALTER TABLE court_bookings AUTO_INCREMENT = 3;
ALTER TABLE trainings AUTO_INCREMENT = 5;
ALTER TABLE training_availability AUTO_INCREMENT = 11;
ALTER TABLE training_bookings AUTO_INCREMENT = 3;

-- ─────────────────────────────────────────────
-- End of SQL Script
-- ─────────────────────────────────────────────

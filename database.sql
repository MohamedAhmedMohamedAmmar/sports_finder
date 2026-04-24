-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 23, 2026 at 03:18 AM
-- Server version: 8.4.3
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `project`
--

-- --------------------------------------------------------

--
-- Table structure for table `checkboxes_incourt_creation`
--

CREATE TABLE `checkboxes_incourt_creation` (
  `id` int NOT NULL,
  `amenity_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `is_active` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `checkboxes_incourt_creation`
--

INSERT INTO `checkboxes_incourt_creation` (`id`, `amenity_name`, `is_active`) VALUES
(1, 'Parking', 1),
(2, 'Lights', 1),
(3, 'Equipment Rental', 1),
(4, 'Locker Room', 1),
(5, 'Cafeteria', 1),
(6, 'WiFi', 1);

-- --------------------------------------------------------

--
-- Table structure for table `courts`
--

CREATE TABLE `courts` (
  `id` int NOT NULL,
  `owner_id` int NOT NULL,
  `sport_id` int NOT NULL,
  `name` varchar(150) COLLATE utf8mb4_general_ci NOT NULL,
  `court_type` enum('indoor','outdoor') COLLATE utf8mb4_general_ci NOT NULL,
  `price_per_hour` decimal(10,2) NOT NULL,
  `address` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `city` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci NOT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `courts`
--

INSERT INTO `courts` (`id`, `owner_id`, `sport_id`, `name`, `court_type`, `price_per_hour`, `address`, `city`, `description`, `image_url`, `created_at`) VALUES
(1, 3, 4, 'Central Tennis Complex', 'indoor', 61.00, '123 Main St', 'New York', 'Premium tennis facility with professional coaching', 'http://localhost/images/court1.jpg', '2026-04-22 20:08:38'),
(2, 3, 2, 'Downtown Basketball Arena', 'outdoor', 35.00, '456 Sports Ave', 'Los Angeles', 'Professional basketball courts with lighting', '/images/court2.jpg', '2026-04-22 15:01:13'),
(3, 3, 3, 'City Football Field', 'outdoor', 45.00, '789 Green Park', 'Chicago', 'Large soccer field available for rent', '/images/court3.jpg', '2026-04-22 15:01:13');

-- --------------------------------------------------------

--
-- Table structure for table `court_amenities`
--

CREATE TABLE `court_amenities` (
  `id` int NOT NULL,
  `court_id` int NOT NULL,
  `amenity_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `court_amenities`
--

INSERT INTO `court_amenities` (`id`, `court_id`, `amenity_id`) VALUES
(7, 2, 1),
(8, 2, 2),
(9, 2, 5),
(10, 2, 6),
(11, 3, 1),
(12, 3, 2),
(13, 3, 5),
(20, 1, 5),
(21, 1, 3),
(22, 1, 2),
(23, 1, 4),
(24, 1, 1),
(25, 1, 6);

-- --------------------------------------------------------

--
-- Table structure for table `court_bookings`
--

CREATE TABLE `court_bookings` (
  `id` int NOT NULL,
  `court_id` int NOT NULL,
  `trainee_id` int NOT NULL,
  `booking_date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `duration_hours` decimal(4,2) NOT NULL,
  `players_count` int DEFAULT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `contact_phone` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `notes` text COLLATE utf8mb4_general_ci,
  `status` enum('Pending','Accepted','Declined','Cancelled','Modified') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Pending',
  `attendance_confirmed` tinyint(1) NOT NULL DEFAULT '0',
  `confirmed_by` int DEFAULT NULL,
  `confirmed_at` timestamp NULL DEFAULT NULL,
  `is_reviewed` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `court_bookings`
--

INSERT INTO `court_bookings` (`id`, `court_id`, `trainee_id`, `booking_date`, `start_time`, `end_time`, `duration_hours`, `players_count`, `total_price`, `contact_phone`, `notes`, `status`, `created_at`, `updated_at`) VALUES
(1, 2, 4, '2026-04-23', '09:39:00', '11:39:00', 2.00, 4, 70.00, '+01011111111', '', 'Pending', '2026-04-23 00:39:36', '2026-04-23 00:39:36');

-- --------------------------------------------------------

--
-- Table structure for table `court_reviews`
--

CREATE TABLE `court_reviews` (
  `id` int NOT NULL,
  `court_id` int NOT NULL,
  `trainee_id` int NOT NULL,
  `booking_id` int NOT NULL,
  `rating` int NOT NULL,
  `review_text` text,
  `review_date` date DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_visible` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `court_schedule`
--

CREATE TABLE `court_schedule` (
  `id` int NOT NULL,
  `court_id` int NOT NULL,
  `day_of_week` enum('Mon','Tue','Wed','Thu','Fri','Sat','Sun') COLLATE utf8mb4_general_ci NOT NULL,
  `open_time` time NOT NULL,
  `close_time` time NOT NULL,
  `is_available` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `court_schedule`
--

INSERT INTO `court_schedule` (`id`, `court_id`, `day_of_week`, `open_time`, `close_time`, `is_available`) VALUES
(8, 2, 'Mon', '08:00:00', '20:00:00', 1),
(9, 2, 'Tue', '08:00:00', '20:00:00', 1),
(10, 2, 'Wed', '08:00:00', '20:00:00', 1),
(11, 2, 'Thu', '08:00:00', '20:00:00', 1),
(12, 2, 'Fri', '08:00:00', '21:00:00', 1),
(13, 2, 'Sat', '09:00:00', '22:00:00', 1),
(14, 2, 'Sun', '09:00:00', '20:00:00', 1),
(22, 1, 'Mon', '06:00:00', '22:00:00', 1),
(23, 1, 'Tue', '06:00:00', '22:00:00', 1),
(24, 1, 'Wed', '06:00:00', '22:00:00', 1),
(25, 1, 'Thu', '06:00:00', '22:00:00', 1),
(26, 1, 'Fri', '06:00:00', '22:00:00', 1),
(27, 1, 'Sat', '06:00:00', '22:00:00', 1),
(28, 1, 'Sun', '06:00:00', '22:00:00', 1);

-- --------------------------------------------------------

--
-- Table structure for table `sports`
--

CREATE TABLE `sports` (
  `id` int NOT NULL,
  `sport_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `is_active` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sports`
--

INSERT INTO `sports` (`id`, `sport_name`, `is_active`) VALUES
(1, 'Tennis', 1),
(2, 'Basketball', 1),
(3, 'Football', 1),
(4, 'Badminton', 1),
(5, 'Squash', 1),
(6, 'Volleyball', 1);

-- --------------------------------------------------------

--
-- Table structure for table `trainer_reviews`
--

CREATE TABLE `trainer_reviews` (
  `id` int NOT NULL,
  `training_id` int NOT NULL,
  `trainee_id` int NOT NULL,
  `booking_id` int NOT NULL,
  `rating` int NOT NULL,
  `review_text` text,
  `review_date` date DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_visible` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `trainings`
--

CREATE TABLE `trainings` (
  `id` int NOT NULL,
  `coach_id` int NOT NULL,
  `sport_id` int NOT NULL,
  `name` varchar(150) COLLATE utf8mb4_general_ci NOT NULL,
  `level` enum('Beginner','Intermediate','Advanced','Professional','All Levels') COLLATE utf8mb4_general_ci NOT NULL,
  `specialty` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `price_per_hour` decimal(10,2) NOT NULL,
  `years_experience` int DEFAULT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `goals` text COLLATE utf8mb4_general_ci,
  `min_sessions` int DEFAULT NULL,
  `location` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `equipment` text COLLATE utf8mb4_general_ci,
  `requirements` text COLLATE utf8mb4_general_ci,
  `certifications` text COLLATE utf8mb4_general_ci,
  `achievements` text COLLATE utf8mb4_general_ci,
  `image_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` enum('Active','Inactive') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Active',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `trainings`
--

INSERT INTO `trainings` (`id`, `coach_id`, `sport_id`, `name`, `level`, `specialty`, `price_per_hour`, `years_experience`, `description`, `goals`, `min_sessions`, `location`, `equipment`, `requirements`, `certifications`, `achievements`, `image_url`, `status`, `created_at`) VALUES
(1, 2, 1, 'Professional Tennis Training', 'Intermediate', 'Competitive Play', 60.00, 15, 'Advanced tennis training for competitive players', 'Improve serve and court strategy', 4, 'Central Park', 'Rackets, Tennis Balls', 'Basic tennis knowledge', 'ATP Certified', 'Multiple Grand Slam appearances', NULL, 'Active', '2026-04-22 15:01:13'),
(2, 2, 2, 'Basketball Skills Mastery', 'Beginner', 'Offensive Skills', 45.00, 10, 'Learn fundamental basketball techniques', 'Build strong basketball foundation', 3, 'Downtown Arena', 'Basketballs, Cones', 'None', 'NBA Coach Certified', 'Trained 50+ players', NULL, 'Active', '2026-04-22 15:01:13'),
(3, 1, 1, 'Weekend Tennis Bootcamp', 'All Levels', 'Fitness & Tennis', 55.00, 12, 'Intensive weekend tennis training', 'Build endurance and technique', 2, 'Sports Complex', 'All equipment provided', 'Enthusiasm required', 'Professional Coach', 'High success rate', NULL, 'Active', '2026-04-22 15:01:13'),
(4, 1, 4, 'training', 'Intermediate', 'training', 30.00, 2, 'training training training training training training', 'training training training training training', 1, 'training', 'training', 'training', 'training', 'training', '', 'Active', '2026-04-22 16:17:45');

-- --------------------------------------------------------

--
-- Table structure for table `training_availability`
--

CREATE TABLE `training_availability` (
  `id` int NOT NULL,
  `training_id` int NOT NULL,
  `day_of_week` enum('Mon','Tue','Wed','Thu','Fri','Sat','Sun') COLLATE utf8mb4_general_ci NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `is_available` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `training_availability`
--

INSERT INTO `training_availability` (`id`, `training_id`, `day_of_week`, `start_time`, `end_time`, `is_available`) VALUES
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

-- --------------------------------------------------------

--
-- Table structure for table `training_bookings`
--

CREATE TABLE `training_bookings` (
  `id` int NOT NULL,
  `training_id` int NOT NULL,
  `trainee_id` int NOT NULL,
  `preferred_date` date NOT NULL,
  `preferred_time` time NOT NULL,
  `sessions_count` int DEFAULT NULL,
  `level_requested` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `price_per_session` decimal(10,2) DEFAULT NULL,
  `total_price` decimal(10,2) DEFAULT NULL,
  `message` text COLLATE utf8mb4_general_ci,
  `status` enum('Pending','Accepted','Declined','Cancelled','Modified') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Pending',
  `attendance_confirmed` tinyint(1) NOT NULL DEFAULT '0',
  `confirmed_by` int DEFAULT NULL,
  `confirmed_at` timestamp NULL DEFAULT NULL,
  `is_reviewed` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `coach_note` text COLLATE utf8mb4_general_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `training_bookings`
--

INSERT INTO `training_bookings` (`id`, `training_id`, `trainee_id`, `preferred_date`, `preferred_time`, `sessions_count`, `level_requested`, `price_per_session`, `total_price`, `message`, `status`, `created_at`, `updated_at`, `coach_note`) VALUES
(1, 1, 4, '2026-04-23', '01:27:00', NULL, NULL, NULL, 240.00, NULL, 'Cancelled', '2026-04-22 23:27:55', '2026-04-22 23:42:08', NULL),
(2, 1, 4, '2026-04-23', '01:28:00', NULL, NULL, NULL, 90.00, NULL, 'Accepted', '2026-04-22 23:28:11', '2026-04-22 23:44:02', '');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int NOT NULL,
  `full_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `role` enum('Trainee','coach','court owner','') COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `is_active` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `full_name`, `email`, `password`, `role`, `created_at`, `updated_at`, `is_active`) VALUES
(1, 'Admin User', 'admin@sportfinder.com', '$2y$10$pXF7MHnD8UhtvpLO4bt7e.ejb4LJ40w82KQemQNSHf8k12LqaeNaK', 'coach', '2026-04-22 15:11:40', '0000-00-00 00:00:00', 1),
(2, 'John Smith', 'john@example.com', '$2y$10$pXF7MHnD8UhtvpLO4bt7e.ejb4LJ40w82KQemQNSHf8k12LqaeNaK', 'coach', '2026-04-22 15:11:36', '0000-00-00 00:00:00', 1),
(3, 'Sarah Johnson', 'sarah@example.com', '$2y$10$pXF7MHnD8UhtvpLO4bt7e.ejb4LJ40w82KQemQNSHf8k12LqaeNaK', 'court owner', '2026-04-22 15:11:29', '0000-00-00 00:00:00', 1),
(4, 'Mike Davisaa', 'mike@example.com', '$2y$10$pXF7MHnD8UhtvpLO4bt7e.ejb4LJ40w82KQemQNSHf8k12LqaeNaK', 'Trainee', '2026-04-22 23:35:29', '0000-00-00 00:00:00', 1),
(5, 'Emily Wilson', 'emily@example.com', '$2y$10$pXF7MHnD8UhtvpLO4bt7e.ejb4LJ40w82KQemQNSHf8k12LqaeNaK', 'Trainee', '2026-04-22 15:11:38', '0000-00-00 00:00:00', 1);

-- --------------------------------------------------------

--
-- Table structure for table `user_profiles`
--

CREATE TABLE `user_profiles` (
  `id` int NOT NULL,
  `user.id` int NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `bio` text COLLATE utf8mb4_general_ci NOT NULL,
  `location` varchar(150) COLLATE utf8mb4_general_ci NOT NULL,
  `avatar` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `joined_date` date NOT NULL,
  `rating_avg` decimal(3,2) NOT NULL,
  `reviews_count` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_profiles`
--

INSERT INTO `user_profiles` (`id`, `user.id`, `phone`, `bio`, `location`, `avatar`, `joined_date`, `rating_avg`, `reviews_count`) VALUES
(1, 1, '+1234567890', 'Professional Tennis Coach', 'New York, NY', '/images/avatar1.jpg', '2025-01-01', 4.50, 12),
(2, 2, '+1234567891', 'Basketball Trainer', 'Los Angeles, CA', '/images/avatar2.jpg', '2025-02-15', 4.70, 18),
(3, 3, '+1234567892', 'Court Owner', 'Chicago, IL', '/images/avatar3.jpg', '2025-03-10', 4.60, 15),
(4, 4, '+1234567893', 'Tennis Enthusiast', 'Boston, MA', '/images/avatar4.jpg', '2025-04-05', 4.80, 8),
(5, 5, '+1234567894', 'Fitness & Sports Lover', 'Miami, FL', '/images/avatar5.jpg', '2025-04-10', 4.90, 5);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `checkboxes_incourt_creation`
--
ALTER TABLE `checkboxes_incourt_creation`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `amenity_name` (`amenity_name`);

--
-- Indexes for table `courts`
--
ALTER TABLE `courts`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `court_amenities`
--
ALTER TABLE `court_amenities`
  ADD PRIMARY KEY (`id`),
  ADD KEY `court_id` (`court_id`),
  ADD KEY `amenity_id` (`amenity_id`);

--
-- Indexes for table `court_bookings`
--
ALTER TABLE `court_bookings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `court_id` (`court_id`),
  ADD KEY `trainee_id` (`trainee_id`);

--
-- Indexes for table `court_reviews`
--
ALTER TABLE `court_reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `court_id` (`court_id`),
  ADD KEY `trainee_id` (`trainee_id`);

--
-- Indexes for table `court_schedule`
--
ALTER TABLE `court_schedule`
  ADD PRIMARY KEY (`id`),
  ADD KEY `court_id` (`court_id`);

--
-- Indexes for table `sports`
--
ALTER TABLE `sports`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `sport_name` (`sport_name`);

--
-- Indexes for table `trainer_reviews`
--
ALTER TABLE `trainer_reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `training_id` (`training_id`),
  ADD KEY `trainee_id` (`trainee_id`);

--
-- Indexes for table `trainings`
--
ALTER TABLE `trainings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `coach_id` (`coach_id`),
  ADD KEY `sport_id` (`sport_id`);

--
-- Indexes for table `training_availability`
--
ALTER TABLE `training_availability`
  ADD PRIMARY KEY (`id`),
  ADD KEY `training_id` (`training_id`);

--
-- Indexes for table `training_bookings`
--
ALTER TABLE `training_bookings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `training_id` (`training_id`),
  ADD KEY `trainee_id` (`trainee_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `login email` (`email`);

--
-- Indexes for table `user_profiles`
--
ALTER TABLE `user_profiles`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `checkboxes_incourt_creation`
--
ALTER TABLE `checkboxes_incourt_creation`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `courts`
--
ALTER TABLE `courts`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `court_amenities`
--
ALTER TABLE `court_amenities`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `court_bookings`
--
ALTER TABLE `court_bookings`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `court_reviews`
--
ALTER TABLE `court_reviews`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `court_schedule`
--
ALTER TABLE `court_schedule`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT for table `sports`
--
ALTER TABLE `sports`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `trainer_reviews`
--
ALTER TABLE `trainer_reviews`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `trainings`
--
ALTER TABLE `trainings`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `training_availability`
--
ALTER TABLE `training_availability`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `training_bookings`
--
ALTER TABLE `training_bookings`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `user_profiles`
--
ALTER TABLE `user_profiles`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `court_amenities`
--
ALTER TABLE `court_amenities`
  ADD CONSTRAINT `fk_court_amenities_amenity` FOREIGN KEY (`amenity_id`) REFERENCES `checkboxes_incourt_creation` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_court_amenities_court` FOREIGN KEY (`court_id`) REFERENCES `courts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `court_bookings`
--
ALTER TABLE `court_bookings`
  ADD CONSTRAINT `fk_court_bookings_court` FOREIGN KEY (`court_id`) REFERENCES `courts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_court_bookings_trainee` FOREIGN KEY (`trainee_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `court_reviews`
--
ALTER TABLE `court_reviews`
  ADD CONSTRAINT `fk_court_reviews_court` FOREIGN KEY (`court_id`) REFERENCES `courts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_court_reviews_trainee` FOREIGN KEY (`trainee_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `court_schedule`
--
ALTER TABLE `court_schedule`
  ADD CONSTRAINT `fk_court_schedule_court` FOREIGN KEY (`court_id`) REFERENCES `courts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `trainer_reviews`
--
ALTER TABLE `trainer_reviews`
  ADD CONSTRAINT `fk_trainer_reviews_trainee` FOREIGN KEY (`trainee_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_trainer_reviews_training` FOREIGN KEY (`training_id`) REFERENCES `trainings` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `trainings`
--
ALTER TABLE `trainings`
  ADD CONSTRAINT `fk_trainings_coach` FOREIGN KEY (`coach_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_trainings_sport` FOREIGN KEY (`sport_id`) REFERENCES `sports` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `training_availability`
--
ALTER TABLE `training_availability`
  ADD CONSTRAINT `fk_training_availability_training` FOREIGN KEY (`training_id`) REFERENCES `trainings` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `training_bookings`
--
ALTER TABLE `training_bookings`
  ADD CONSTRAINT `fk_training_bookings_trainee` FOREIGN KEY (`trainee_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_training_bookings_training` FOREIGN KEY (`training_id`) REFERENCES `trainings` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

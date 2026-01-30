-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: event
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `events` (
  `event_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `event_date` datetime DEFAULT NULL,
  `seat_capacity` int DEFAULT NULL,
  `organizer_id` int DEFAULT NULL,
  PRIMARY KEY (`event_id`),
  KEY `organizer_id` (`organizer_id`),
  CONSTRAINT `events_ibfk_1` FOREIGN KEY (`organizer_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (1,'Learn Python Basics', 'Tech', 'Riyadh', '2025-12-15 18:00:00', 50, 2),
(2,'Machine Learning Essentials', 'Tech', 'Jeddah', '2026-01-20 17:00:00', 60, 3),
(3,'Advanced AI Models', 'Tech', 'Dammam', '2026-02-10 19:00:00', 70, 4),
(4,'Frontend Development Bootcamp', 'Tech', 'Qassim', '2026-03-05 16:00:00', 45, 5),

-- WORKSHOPS
(5,'UI/UX Design Workshop', 'Workshop', 'Riyadh', '2025-12-28 15:00:00', 40, 6),
(6,'Pottery Workshop', 'Art', 'Jeddah', '2026-01-12 14:30:00', 25, 2),
(7,'Cup Colouring Workshop', 'Art', 'Qassim', '2026-03-01 13:00:00', 20, 3),

-- COOKING / CULINARY EVENTS
(8,'Cooking Basics', 'Culinary', 'Dammam', '2025-12-22 19:30:00', 30, 4),
(9,'Baking Workshop', 'Culinary', 'Riyadh', '2026-01-18 17:00:00', 25, 5),
(10,'Chocolate Making Class', 'Culinary', 'Jeddah', '2026-02-07 16:00:00', 20, 6),
(11,'Arabic Cuisine Masterclass', 'Cooking', 'Qassim', '2026-03-10 18:00:00', 40, 2),
(12,'Sushi Basics Workshop', 'Culinary', 'Dammam', '2026-01-25 15:00:00', 20, 3),
(13,'Pastry Art Class', 'Culinary', 'Riyadh', '2026-02-14 14:00:00', 25, 4),
(14,'BBQ Skills Training', 'Cooking', 'Jeddah', '2026-03-20 18:00:00', 35, 5),

-- BUSINESS EVENTS
(15,'Startup Basics', 'Business', 'Riyadh', '2026-01-10 17:30:00', 45, 6),
(16,'Marketing Strategies 2026', 'Business', 'Dammam', '2026-02-22 16:00:00', 50, 2),

-- HEALTH EVENTS
(17,'Healthy Cooking Workshop', 'Health', 'Qassim', '2026-01-30 14:30:00', 30, 3),
(18,'Mental Health Awareness', 'Health', 'Jeddah', '2026-03-05 18:00:00', 60, 4);
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registrations`
--

DROP TABLE IF EXISTS `registrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registrations` (
  `registration_id` int NOT NULL AUTO_INCREMENT,
  `event_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`registration_id`),
  KEY `event_id` (`event_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `registrations_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE,
  CONSTRAINT `registrations_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registrations`
--

LOCK TABLES `registrations` WRITE;
/*!40000 ALTER TABLE `registrations` DISABLE KEYS */;
INSERT INTO `registrations` VALUES -- Learn Python Basics
-- Learn Python Basics
(1, 1, 8,  'REGISTERED'),   -- Hossam  (user_id = 8)
(2, 1, 9,  'CANCELLED'),    -- Reem    (user_id = 9)

-- Machine Learning Essentials
(3, 2, 10, 'REGISTERED'),   -- Saud    (user_id = 10)
(4, 2, 11, 'REGISTERED'),   -- Meshal  (user_id = 11)

-- UI/UX Design Workshop
(5, 5, 12, 'CANCELLED'),    -- Jood    (event_id = 5, user_id = 12)
(6, 5, 13, 'REGISTERED'),   -- Raghad  (user_id = 13)

-- Cooking Basics
(7, 8, 14, 'REGISTERED'),   -- Abdullah (event_id = 8, user_id = 14)
(8, 8, 15, 'CANCELLED'),    -- Ali      (user_id = 15)

-- Baking Workshop
(9,  9, 14, 'REGISTERED'),  -- Abdullah (event_id = 9)
(10, 9, 15, 'REGISTERED'),  -- Ali

-- Pottery Workshop
(11, 6, 8, 'CANCELLED'),    -- Hossam   (event_id = 6)

-- Cup Colouring Workshop
(12, 7, 9, 'REGISTERED');   -- Reem     (event_id = 7)

/*!40000 ALTER TABLE `registrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS `tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickets` (
  `ticket_id` int NOT NULL AUTO_INCREMENT,
  `registration_id` int DEFAULT NULL,
  `ticket_code` varchar(200) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ticket_id`),
  KEY `registration_id` (`registration_id`),
  CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`registration_id`) REFERENCES `registrations` (`registration_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tickets`
--

LOCK TABLES `tickets` WRITE;
/*!40000 ALTER TABLE `tickets` DISABLE KEYS */;
INSERT INTO `tickets` VALUES (1,  1,  'TCK-LPB-HOSSAM',    'ACTIVE'),   -- Learn Python Basics - Hossam
(2,  3,  'TCK-MLE-SAUD',      'ACTIVE'),   -- Machine Learning Essentials - Saud
(3,  4,  'TCK-MLE-MESHAL',    'ACTIVE'),   -- Machine Learning Essentials - Meshal
(4,  6,  'TCK-UIUX-RAGHAD',   'ACTIVE'),   -- UI/UX - Raghad
(5,  7,  'TCK-COOK-ABDULLAH', 'ACTIVE'),   -- Cooking Basics - Abdullah
(6,  9,  'TCK-BAKE-ABDULLAH', 'ACTIVE'),   -- Baking Workshop - Abdullah
(7, 10,  'TCK-BAKE-ALI',      'ACTIVE'),   -- Baking Workshop - Ali
(8, 12,  'TCK-CUP-REEM',      'ACTIVE'); 
/*!40000 ALTER TABLE `tickets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password` varchar(200) DEFAULT NULL,
  `role` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Main Admin', 'admin1@gmail.com', 'ad@111', 'ADMIN'),

-- Organizers
(2,'Ahmed ', 'Aorg1@gmail.com', '938$', 'ORGANIZER'),
(3,'Khaled ', 'Aorg1@hotmail.com', '3772@', 'ORGANIZER'),
(4,'Sara ', 'Aorg1@yahoo.com', '872@', 'ORGANIZER'),
(5,'Fahad ', 'Aorg1@outlook.com', '86923#', 'ORGANIZER'),
(6,'Maryam ', 'Aorg1@gmx.com', '873&', 'ORGANIZER'),

-- Attendees
(8,'Hossam Alotaibi', 'hossam@gmail.com', '6747a', 'ATTENDEE'),
(9,'Reem Alqahtani', 'reem@hotmail.com', '79567#', 'ATTENDEE'),
(10,'Saud Alanzi', 'saud@yahoo.com', '23723ds', 'ATTENDEE'),
(11,'Meshal Aldosari', 'meshal@gmail.com', '29279ga', 'ATTENDEE'),
(12,'Jood Alharbi', 'jood@hotmail.com', '68258@', 'ATTENDEE'),
(13,'Raghad Alshahri', 'raghad@yahoo.com', '97317hs', 'ATTENDEE'),
(14,'Abdullah Alsbaei', 'abdullah@gmail.com', '78251#', 'ATTENDEE'),
(15,'Ali Almutairi', 'ali@hotmail.com', 'bks227', 'ATTENDEE');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-01 18:29:03

-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: poom
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alarm`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alarm` (
  `alarm_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `member_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `description` text,
  `title` varchar(255) DEFAULT NULL,
  `alarm_type` enum('CHAT','COMMUNITY','JOURNAL','SHARED_JOURNAL') DEFAULT NULL,
  `alarm_list_ver_content` text,
  `character_type` enum('EMPATHY','SOLUTION') NOT NULL,
  PRIMARY KEY (`alarm_id`),
  KEY `FK53dra8a3h29id86y823i3blxk` (`member_id`),
  CONSTRAINT `FK53dra8a3h29id86y823i3blxk` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chat_message`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
  `chat_message_id` bigint NOT NULL AUTO_INCREMENT,
  `chat_room_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `content` text NOT NULL,
  `media_url` varchar(255) DEFAULT NULL,
  `message_type` enum('IMAGE','TEXT','VOICE') NOT NULL,
  `sender_type` enum('AI','USER') NOT NULL,
  `character_type` enum('EMPATHY','SOLUTION') NOT NULL,
  PRIMARY KEY (`chat_message_id`),
  KEY `FKj52yap2xrm9u0721dct0tjor9` (`chat_room_id`),
  CONSTRAINT `FKj52yap2xrm9u0721dct0tjor9` FOREIGN KEY (`chat_room_id`) REFERENCES `chat_room` (`chat_room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chat_message_image`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message_image` (
  `chat_message_image` bigint NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) DEFAULT NULL,
  `chat_message_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`chat_message_image`),
  KEY `FK8wvhb7xbmb3cs09h2eqieyep0` (`chat_message_id`),
  CONSTRAINT `FK8wvhb7xbmb3cs09h2eqieyep0` FOREIGN KEY (`chat_message_id`) REFERENCES `chat_message` (`chat_message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chat_room`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_room` (
  `chat_room_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `member_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `chat_title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`chat_room_id`),
  KEY `FK6x6htd2o9ba2r2wcrs72qau17` (`member_id`),
  CONSTRAINT `FK6x6htd2o9ba2r2wcrs72qau17` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `couple`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `couple` (
  `couple_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `couple_status` enum('CONNECTED','DISCONNECTED_GRACE_PERIOD') DEFAULT NULL,
  `member_a_id` bigint DEFAULT NULL,
  `member_b_id` bigint DEFAULT NULL,
  PRIMARY KEY (`couple_id`),
  KEY `FKlqrofs4461uq26x1ttmchk270` (`member_a_id`),
  KEY `FKtlmckme2msnqymntc9u6x2rhh` (`member_b_id`),
  CONSTRAINT `FKlqrofs4461uq26x1ttmchk270` FOREIGN KEY (`member_a_id`) REFERENCES `member` (`member_id`),
  CONSTRAINT `FKtlmckme2msnqymntc9u6x2rhh` FOREIGN KEY (`member_b_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `journal`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `journal` (
  `journal_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `content` text,
  `journal_date` date NOT NULL,
  `journal_emotion` enum('ANGER','ANXIETY','HAPPY','LOVE','NEUTRAL','PAIN','PEACEFUL','SAD') NOT NULL,
  `member_id` bigint DEFAULT NULL,
  PRIMARY KEY (`journal_id`),
  KEY `FKjhakmepded1gywo13i51ggqd3` (`member_id`),
  CONSTRAINT `FKjhakmepded1gywo13i51ggqd3` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `journal_image`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `journal_image` (
  `journal_image_id` bigint NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) DEFAULT NULL,
  `journal_id` bigint DEFAULT NULL,
  PRIMARY KEY (`journal_image_id`),
  KEY `FKpgrvsycxjcbe51qves8yxsegd` (`journal_id`),
  CONSTRAINT `FKpgrvsycxjcbe51qves8yxsegd` FOREIGN KEY (`journal_id`) REFERENCES `journal` (`journal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `member`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
  `birth_date` date NOT NULL,
  `child_birth_date` date DEFAULT NULL,
  `daily_alarm_time` time(6) DEFAULT NULL,
  `has_given_birth` bit(1) DEFAULT NULL,
  `push_alarm` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `member_id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `device_token` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `expertise_file` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `birth_relationship` enum('FAMILY','FRIEND','OTHERS','PARTNER') DEFAULT NULL,
  `character_type` enum('EMPATHY','SOLUTION') NOT NULL,
  `chat_mode` enum('TEXT','VOICE') NOT NULL,
  `gender` enum('FEMALE','MALE','OTHERS') NOT NULL,
  `role` enum('ADMIN','USER') NOT NULL,
  `user_type` enum('FAMILY_OR_SUPPORTER','MOTHER','PROFESSIONAL') NOT NULL,
  `last_send_at` datetime(6) DEFAULT NULL,
  `next_send_at` datetime(6) DEFAULT NULL,
  `profile_image` varchar(255) DEFAULT NULL,
  `couple_code` varchar(6) DEFAULT NULL,
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `uk_member_couple_code` (`couple_code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `social`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `social` (
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `provider_id` varchar(255) DEFAULT NULL,
  `social_type` enum('GOOGLE','KAKAO','LOCAL') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnph42s3cmsq3xuga273o7dijq` (`member_id`),
  CONSTRAINT `FKnph42s3cmsq3xuga273o7dijq` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `withdrawal_reason_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `withdrawal_reason_log` (
  `withdrawal_reason_log_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `reason` enum('BUGS','FOUND_OTHER_SERVICE','MISSING_FEATURES','NOT_HELPFUL','OTHER','TOO_DIFFICULT') NOT NULL,
  PRIMARY KEY (`withdrawal_reason_log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'poom'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-06 16:51:23

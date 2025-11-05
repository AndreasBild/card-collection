CREATE DATABASE  IF NOT EXISTS `card_collection` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `card_collection`;
-- MySQL dump 10.13  Distrib 8.0.42, for macos15 (arm64)
--
-- Host: localhost    Database: card_collection
-- ------------------------------------------------------
-- Server version	9.5.0

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'c438073e-4563-11f0-afc9-c39087f22c03:1-259';

--
-- Table structure for table `card`
--

DROP TABLE IF EXISTS `card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `print_run` int NOT NULL,
  `serial_number` int NOT NULL,
  `number` varchar(255) DEFAULT NULL,
  `player_id` bigint DEFAULT NULL,
  `theme_id` bigint DEFAULT NULL,
  `season_id` bigint DEFAULT NULL,
  `variant_id` bigint DEFAULT NULL,
  `rookie_card` bit(1) DEFAULT NULL,
  `game_used_material` bit(1) DEFAULT NULL,
  `autograph` bit(1) DEFAULT NULL,
  `grading_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbyb0u8pl0bms3a11dql17ut0b` (`player_id`),
  KEY `FKrhm60fo96t7r89farfjnmg0n9` (`theme_id`),
  KEY `FK6xhb82f364llei3se8shqvxoa` (`variant_id`),
  KEY `FK_card_season` (`season_id`),
  KEY `fk_card_grading` (`grading_id`),
  CONSTRAINT `FK6xhb82f364llei3se8shqvxoa` FOREIGN KEY (`variant_id`) REFERENCES `variant` (`id`),
  CONSTRAINT `fk_card_grading` FOREIGN KEY (`grading_id`) REFERENCES `grading` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_card_season` FOREIGN KEY (`season_id`) REFERENCES `season` (`id`),
  CONSTRAINT `FKbyb0u8pl0bms3a11dql17ut0b` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`),
  CONSTRAINT `FKrhm60fo96t7r89farfjnmg0n9` FOREIGN KEY (`theme_id`) REFERENCES `card_theme` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1085 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card`
--

LOCK TABLES `card` WRITE;
/*!40000 ALTER TABLE `card` DISABLE KEYS */;
INSERT INTO `card` VALUES (2,0,0,'278',1,2,1,2,_binary '\0',_binary '\0',_binary '\0',NULL),(3,0,0,'278',1,2,1,3,_binary '\0',_binary '\0',_binary '\0',NULL),(4,750,689,'278',1,3,1,1,_binary '',_binary '\0',_binary '',NULL),(5,0,0,'S4',1,4,1,5,_binary '\0',_binary '\0',_binary '\0',NULL),(6,0,0,'S4',1,4,1,3,_binary '\0',_binary '\0',_binary '\0',NULL),(8,0,0,'5',1,6,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(10,0,0,'105',1,15,1,1,_binary '',_binary '\0',_binary '\0',NULL),(11,0,0,'105',1,16,1,3,_binary '\0',_binary '\0',_binary '\0',NULL),(12,0,0,'98',1,501,1,1,_binary '',_binary '\0',_binary '\0',NULL),(13,0,0,'103',1,502,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(14,0,0,'259',1,60,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(15,0,0,'259',1,60,1,201,_binary '\0',_binary '\0',_binary '\0',NULL),(16,0,0,'288',1,62,1,1,_binary '',_binary '\0',_binary '\0',NULL),(17,0,0,'288',1,62,1,201,_binary '\0',_binary '\0',_binary '\0',NULL),(18,0,0,'RP3',1,61,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(19,0,0,'RP3',1,61,1,201,_binary '\0',_binary '\0',_binary '\0',NULL),(20,0,0,'319',1,503,1,1,_binary '',_binary '\0',_binary '\0',NULL),(21,0,0,'3',1,504,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(22,0,0,'381',1,22,1,1,_binary '',_binary '\0',_binary '\0',NULL),(23,0,0,'5',1,23,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(24,0,0,'378',1,505,1,1,_binary '',_binary '\0',_binary '\0',NULL),(25,0,0,'425',1,506,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(26,0,0,'5',1,507,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(27,0,0,'AR5',1,508,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(28,0,0,'FAR5',1,508,1,709,_binary '\0',_binary '\0',_binary '\0',NULL),(29,0,0,'AR5',1,508,1,710,_binary '\0',_binary '\0',_binary '\0',NULL),(30,0,0,'3',1,509,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(31,0,0,'5',1,510,1,1,_binary '',_binary '\0',_binary '\0',NULL),(32,0,0,'D5',1,510,1,711,_binary '\0',_binary '\0',_binary '\0',NULL),(33,0,0,'PC24',1,511,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(34,0,0,'PC24',1,511,1,711,_binary '\0',_binary '\0',_binary '\0',NULL),(35,0,0,'134',1,512,1,1,_binary '',_binary '\0',_binary '\0',NULL),(36,0,0,'134',1,512,1,711,_binary '\0',_binary '\0',_binary '\0',NULL),(37,0,0,'293',1,513,1,1,_binary '',_binary '\0',_binary '\0',NULL),(38,0,0,'DP5',1,514,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(39,0,0,'2',1,515,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(40,0,0,'210',1,516,1,1,_binary '',_binary '\0',_binary '\0',NULL),(41,0,0,'210',1,516,1,712,_binary '\0',_binary '\0',_binary '\0',NULL),(42,0,0,'210',1,516,1,713,_binary '\0',_binary '\0',_binary '\0',NULL),(43,0,0,'210',1,516,1,714,_binary '\0',_binary '\0',_binary '\0',NULL),(44,0,0,'393',1,20,1,1,_binary '',_binary '\0',_binary '\0',NULL),(45,0,0,'393',1,20,1,715,_binary '\0',_binary '\0',_binary '\0',NULL),(46,0,0,'343',1,517,1,1,_binary '',_binary '\0',_binary '\0',NULL),(47,0,0,'3',1,518,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(49,0,0,'196',1,520,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(50,0,0,'331',1,521,1,1,_binary '',_binary '\0',_binary '\0',NULL),(51,0,0,'D5',1,522,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(52,0,0,'H38',1,523,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(53,0,0,'H38',1,523,1,5,_binary '\0',_binary '\0',_binary '\0',NULL),(54,0,0,'178',1,524,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(55,0,0,'178',1,524,1,3,_binary '\0',_binary '\0',_binary '\0',NULL),(56,0,0,'178',1,524,1,710,_binary '',_binary '\0',_binary '\0',NULL),(57,0,0,'RS5',1,525,1,1,_binary '\0',_binary '\0',_binary '\0',NULL),(58,0,0,'38',1,1,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(59,0,0,'38',1,1,2,299,_binary '\0',_binary '\0',_binary '\0',NULL),(60,0,0,'38',1,1,2,300,_binary '\0',_binary '\0',_binary '\0',NULL),(61,0,0,'349',1,7,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(62,0,0,'349',1,7,2,299,_binary '\0',_binary '\0',_binary '\0',NULL),(63,0,0,'349',1,7,2,300,_binary '\0',_binary '\0',_binary '\0',NULL),(64,0,0,'C3',1,8,2,2,_binary '\0',_binary '\0',_binary '\0',NULL),(65,0,0,'C3',1,8,2,3,_binary '\0',_binary '\0',_binary '\0',NULL),(66,0,0,'C3B',1,8,2,2,_binary '\0',_binary '\0',_binary '\0',NULL),(67,0,0,'C3B',1,8,2,3,_binary '\0',_binary '\0',_binary '\0',NULL),(68,0,0,'C3C',1,8,2,2,_binary '\0',_binary '\0',_binary '\0',NULL),(69,0,0,'C3C',1,8,2,3,_binary '\0',_binary '\0',_binary '\0',NULL),(70,0,0,'C3',1,8,2,6,_binary '\0',_binary '\0',_binary '\0',NULL),(71,0,0,'C3',1,8,2,716,_binary '\0',_binary '\0',_binary '\0',NULL),(72,0,0,'88',1,527,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(73,0,0,'88',1,527,2,18,_binary '\0',_binary '\0',_binary '\0',NULL),(74,0,0,'20',1,526,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(75,0,0,'224',1,62,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(76,0,0,'224',1,62,2,201,_binary '\0',_binary '\0',_binary '\0',NULL),(77,0,0,'DS29',1,63,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(78,0,0,'M20',1,64,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(79,0,0,'M20',1,65,2,201,_binary '\0',_binary '\0',_binary '\0',NULL),(80,0,0,'M20',1,65,2,201,_binary '\0',_binary '\0',_binary '\0',2),(81,0,0,'M20',1,65,2,201,_binary '\0',_binary '\0',_binary '\0',3),(82,0,0,'M20',1,65,2,201,_binary '\0',_binary '\0',_binary '\0',4),(83,0,0,'M20',1,66,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(85,0,0,'M20',1,66,2,201,_binary '\0',_binary '\0',_binary '\0',NULL),(88,0,0,'M20',1,66,2,201,_binary '\0',_binary '\0',_binary '\0',NULL),(89,0,0,'278',1,1,1,1,_binary '',_binary '\0',_binary '\0',NULL),(90,0,0,'M20',1,66,2,201,_binary '\0',_binary '\0',_binary '\0',4),(95,0,0,'145',1,503,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(96,0,0,'193',1,22,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(97,0,0,'4',1,25,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(98,0,0,'3',1,27,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(99,0,0,'166',1,505,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(100,0,0,'200',1,538,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(101,0,0,'14',1,530,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(102,0,0,'HS11',1,536,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(103,0,0,'SL49',1,537,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(104,0,0,'116',1,540,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(105,0,0,'116',1,540,2,711,_binary '\0',_binary '\0',_binary '\0',NULL),(106,0,0,'116',1,541,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(107,0,0,'116',1,541,2,717,_binary '\0',_binary '\0',_binary '\0',NULL),(108,0,0,'144',1,510,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(109,0,0,'AS9',1,543,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(110,0,0,'AS9',1,543,2,3,_binary '\0',_binary '\0',_binary '\0',NULL),(111,0,0,'PC39',1,511,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(112,0,0,'PC39',1,511,2,711,_binary '\0',_binary '\0',_binary '\0',NULL),(113,0,0,'114',1,512,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(114,0,0,'146',1,546,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(115,0,0,'C29',1,544,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(116,0,0,'C29',1,544,2,711,_binary '\0',_binary '\0',_binary '\0',NULL),(117,0,0,'S7',1,545,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(118,0,0,'S7',1,545,2,3,_binary '\0',_binary '\0',_binary '\0',NULL),(119,0,0,'10',1,516,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(120,0,0,'BT2',1,547,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(121,0,0,'BT2',1,547,2,713,_binary '\0',_binary '\0',_binary '\0',NULL),(122,0,0,'32',1,516,2,713,_binary '\0',_binary '\0',_binary '\0',NULL),(123,0,0,'32',1,516,2,713,_binary '\0',_binary '\0',_binary '\0',NULL),(124,0,0,'161',1,20,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(125,0,0,'WK3',1,548,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(126,0,0,'R1',1,549,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(130,0,0,'21',1,555,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(131,0,0,'21',1,555,2,720,_binary '\0',_binary '\0',_binary '\0',NULL),(133,0,0,'192',1,517,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(134,0,0,'192',1,517,2,301,_binary '\0',_binary '\0',_binary '\0',NULL),(135,0,0,'315',1,557,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(136,0,0,'6',1,556,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(137,0,0,'6',1,556,2,301,_binary '\0',_binary '\0',_binary '\0',NULL),(140,0,0,'160',1,521,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(141,0,0,'160',1,521,2,100,_binary '\0',_binary '\0',_binary '\0',NULL),(142,0,0,'160',1,521,2,100,_binary '\0',_binary '\0',_binary '\0',NULL),(143,0,0,'160',1,521,2,101,_binary '\0',_binary '\0',_binary '\0',NULL),(144,0,0,'207',1,521,2,100,_binary '\0',_binary '\0',_binary '\0',NULL),(145,0,0,'207',1,521,2,101,_binary '\0',_binary '\0',_binary '\0',NULL),(146,0,0,'348',1,559,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(147,0,0,'348',1,559,2,100,_binary '\0',_binary '\0',_binary '\0',NULL),(148,0,0,'348',1,559,2,101,_binary '\0',_binary '\0',_binary '\0',NULL),(150,0,0,'SE88',1,524,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(151,0,0,'SE88',1,524,2,3,_binary '\0',_binary '\0',_binary '\0',NULL),(160,0,0,'33',1,560,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(161,0,0,'33',1,560,3,201,_binary '\0',_binary '\0',_binary '\0',NULL),(162,0,0,'33',1,560,3,210,_binary '\0',_binary '\0',_binary '\0',NULL),(163,0,0,'BC3',1,561,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(164,0,0,'BC3',1,561,3,201,_binary '\0',_binary '\0',_binary '\0',NULL),(165,0,0,'BC3',1,561,3,210,_binary '\0',_binary '\0',_binary '\0',NULL),(166,0,0,'TB20',1,562,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(167,0,0,'TB20',1,562,3,201,_binary '\0',_binary '\0',_binary '\0',NULL),(168,0,0,'TB20',1,562,3,210,_binary '\0',_binary '\0',_binary '\0',NULL),(169,0,0,'HR9',1,563,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(170,0,0,'HR9',1,563,3,201,_binary '\0',_binary '\0',_binary '\0',NULL),(171,0,0,'HR9',1,563,3,210,_binary '\0',_binary '\0',_binary '\0',NULL),(177,0,0,'88',1,527,2,1,_binary '\0',_binary '\0',_binary '\0',NULL),(178,0,0,'288',1,62,1,201,_binary '\0',_binary '\0',_binary '\0',NULL),(179,0,0,'D5',1,510,1,711,_binary '\0',_binary '\0',_binary '\0',NULL),(180,0,0,'352',1,1,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(181,0,0,'M178',1,9,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(182,0,0,'M178',1,9,3,3,_binary '\0',_binary '\0',_binary '\0',NULL),(183,0,0,'S29',1,10,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(184,0,0,'C29',1,12,3,2,_binary '\0',_binary '\0',_binary '\0',NULL),(185,0,0,'C29',1,12,3,3,_binary '\0',_binary '\0',_binary '\0',NULL),(186,0,0,'C29',1,13,3,2,_binary '\0',_binary '\0',_binary '\0',NULL),(187,0,0,'C29',1,13,3,3,_binary '\0',_binary '\0',_binary '\0',NULL),(188,0,0,'R29',1,14,3,2,_binary '\0',_binary '\0',_binary '\0',NULL),(189,0,0,'R29',1,14,3,3,_binary '\0',_binary '\0',_binary '\0',NULL),(195,0,0,'79',1,550,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(196,499,259,'79',1,550,3,20,_binary '\0',_binary '\0',_binary '\0',NULL),(197,0,0,'6',1,552,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(200,0,0,'85',1,570,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(201,0,0,'85',1,570,3,201,_binary '\0',_binary '\0',_binary '\0',NULL),(202,0,0,'261',1,571,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(203,0,0,'261',1,571,3,201,_binary '\0',_binary '\0',_binary '\0',NULL),(204,0,0,'140',1,572,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(205,0,0,'140',1,572,3,201,_binary '\0',_binary '\0',_binary '\0',NULL),(210,0,0,'ROW 0 SEAT 5',1,580,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(211,0,0,'ROW 1 SEAT 5',1,581,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(212,0,0,'ROW 2 SEAT 5',1,582,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(213,150,11,'ROW 0 SEAT 5',1,580,3,21,_binary '\0',_binary '\0',_binary '\0',NULL),(214,150,13,'ROW 1 SEAT 5',1,581,3,21,_binary '\0',_binary '\0',_binary '\0',NULL),(215,150,22,'ROW 2 SEAT 5',1,582,3,21,_binary '\0',_binary '\0',_binary '\0',NULL),(220,0,0,'116',1,22,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(221,0,0,'148',1,585,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(222,0,0,'297',1,586,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(223,0,0,'4',1,587,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(224,0,0,'3',1,588,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(230,0,0,'170',1,505,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(231,0,0,'333',1,539,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(232,0,0,'H20',1,531,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(233,0,0,'2',1,590,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(234,0,0,'29',1,591,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(240,0,0,'106',1,541,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(241,0,0,'240',1,595,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(242,0,0,'240',1,595,3,25,_binary '\0',_binary '\0',_binary '\0',NULL),(243,0,0,'11',1,596,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(244,0,0,'5',1,597,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(245,0,0,'4',1,598,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(246,0,0,'4',1,599,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(250,0,0,'127',1,513,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(251,0,0,'127',1,513,3,11,_binary '\0',_binary '\0',_binary '\0',NULL),(252,0,0,'245',1,513,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(253,0,0,'245',1,513,3,11,_binary '\0',_binary '\0',_binary '\0',NULL),(254,0,0,'30',1,610,3,12,_binary '\0',_binary '\0',_binary '\0',NULL),(255,0,0,'30',1,610,3,18,_binary '\0',_binary '\0',_binary '\0',NULL),(256,0,0,'CU3',1,611,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(257,0,0,'6',1,612,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(258,0,0,'10',1,613,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(260,0,0,'123',1,510,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(261,0,0,'PC40',1,511,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(262,0,0,'F2',1,615,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(265,0,0,'98',1,516,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(266,0,0,'CA4',1,620,3,1,_binary '\0',_binary '\0',_binary '\0',NULL),(267,0,0,'CA4',1,620,3,201,_binary '\0',_binary '\0',_binary '\0',NULL),(1084,0,0,'CA4',1,620,3,210,_binary '\0',_binary '\0',_binary '\0',NULL);
/*!40000 ALTER TABLE `card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card_brand`
--

DROP TABLE IF EXISTS `card_brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_brand` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `manufacturer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_card_brand_manufacturer_id` (`manufacturer_id`),
  CONSTRAINT `fk_card_brand_manufacturer` FOREIGN KEY (`manufacturer_id`) REFERENCES `card_manufacturer` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=186 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_brand`
--

LOCK TABLES `card_brand` WRITE;
/*!40000 ALTER TABLE `card_brand` DISABLE KEYS */;
INSERT INTO `card_brand` VALUES (1,'Collectors Choice',1),(2,'Exquisite',1),(3,'SP Authentic',1),(4,'Upper Deck',1),(5,'SP',1),(6,'SP Championship',1),(7,'UD3',1),(8,'SPx',1),(9,'Hardcourt',1),(10,'Black Diamond',1),(11,'SPx Finite',1),(12,'Choice',1),(13,'Ionix',1),(14,'Ovation',1),(15,'Encore',1),(16,'HoloGrFX',1),(17,'Retro',1),(18,'MVP',1),(19,'Gold Reserve',1),(20,'Victory',1),(22,'UDx',1),(23,'SLAM',1),(24,'SP Game Used',1),(25,'Glass',1),(26,'Authentics',1),(27,'Sweet Shot',1),(28,'Ultimate Victory',1),(29,'Honor Roll',1),(30,'Inspiration',1),(31,'SP Authentic Limited',1),(32,'Flight Team',1),(33,'Finite',1),(34,'Ultimate Collection',1),(35,'Championship Drive',1),(36,'Exclusives',1),(37,'Standing O',1),(38,'Legends',1),(39,'R-Class',1),(40,'Trilogy',1),(41,'Reflections',1),(42,'ESPN',1),(43,'Rookie Debut',1),(44,'Signature Edition',1),(50,'Topps',2),(51,'Embossed',2),(55,'Topps Gallery',2),(56,'Bowman\'s Best',2),(58,'Topps Gold Label',2),(59,'Topps Tip Off',2),(60,'Topps Heritage',2),(61,'Topps Stars',2),(62,'Topps Reserve',2),(63,'Topps Pristine',2),(64,'Topps Jersey Edition',2),(65,'Topps Bazooka',2),(66,'Topps Turkey Red',2),(67,'Topps Contemporary Collection',2),(68,'Topps Rookie Matrix',2),(69,'Topps First Edition',2),(70,'Topps Luxury Box',2),(71,'Topps Total',2),(72,'Stadium Club',2),(80,'Flair',3),(81,'Fleer',3),(82,'Jam Session',3),(83,'SkyBox',3),(84,'SkyBox Premium',3),(85,'SkyBox Autographics',3),(86,'SkyBox E-X',3),(87,'SkyBox Z-Force',3),(88,'SkyBox Hoops',3),(89,'SkyBox LE',3),(90,'SkyBox Dominion',3),(91,'SkyBox Thunder',3),(92,'SkyBox Metal Universe',3),(93,'SkyBox Molten Metal',3),(94,'SkyBox Apex',3),(95,'SkyBox Holographics',3),(96,'SkyBox Impact',3),(97,'SkyBox Jam Session',3),(98,'Donruss',5),(99,'Donruss Optic',5),(100,'Panini Prizm',5),(101,'Panini Select',5),(102,'Panini Contenders',5),(103,'Panini Contenders Optic',5),(104,'Panini National Treasures',5),(105,'Panini Noir',5),(106,'Panini Immaculate',5),(107,'Panini Impeccable',5),(108,'Panini Court Kings',5),(109,'Panini Revolution',5),(110,'Panini Origins',5),(111,'Panini Mosaic',5),(112,'Panini Chronicles',5),(113,'Panini One and One',5),(114,'Panini Obsidian',5),(115,'Panini Absolute Memorabilia',5),(116,'Panini Illusions',5),(117,'Panini Spectra',5),(118,'Panini Dominion',5),(120,'Panini Instant',5),(121,'Panini Draft Picks',5),(122,'Panini Hoops',5),(123,'Leaf Metal',4),(128,'Leaf In The Game Used',4),(129,'Leaf Signature Series',4),(130,'Topps Chrome',2),(131,'Topps Finest',2),(132,'Topps Stadium Club',2),(133,'Topps Bazooka',2),(134,'Topps Total',2),(135,'Topps Midnight',2),(136,'SP Signature Edition',1),(137,'SP Top Prospects',1),(138,'SP Rookie Threads',1),(139,'UD Reserve',1),(140,'UD Black',1),(141,'UD Premier',1),(142,'UD Chronology',1),(143,'SkyBox Metal Universe Champions',1),(144,'Ultra',3),(145,'Fleer Metal',3),(146,'Fleer Tradition',3),(147,'Fleer Authority',3),(148,'Fleer Brilliants',3),(149,'Fleer Force',3),(150,'Fleer Mystique',3),(151,'Fleer Platinum',3),(152,'Fleer Showcase',3),(153,'Fleer Splendid',3),(154,'Leaf Limited',4),(155,'Leaf Originals',4),(156,'Scoreboard',7),(157,'Scoreboard Autographed Collection',7),(158,'Scoreboard Signature Series',7),(159,'Classic',6),(160,'Classic Draft Picks',6),(161,'Classic Games',6),(162,'Classic Images',6),(163,'Classic Road to the NBA',6),(164,'Topps Bowman',2),(165,'Topps Allen & Ginter',2),(166,'Topps Draft Picks & Prospects',2),(167,'Panini Crown Royale',5),(168,'Panini Prime',5),(169,'Panini Gold Standard',5),(170,'Panini Vanguard',5),(171,'Panini Encased',5),(172,'Panini Status',5),(173,'Panini Luminance',5),(174,'Panini Playbook',5),(175,'SkyBox Emotion',3),(176,'Finest',2),(178,'SkyBox E-XL',3),(179,'SkyBox E-2000',3),(185,'Flair Showcase',3);
/*!40000 ALTER TABLE `card_brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card_manufacturer`
--

DROP TABLE IF EXISTS `card_manufacturer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_manufacturer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_manufacturer`
--

LOCK TABLES `card_manufacturer` WRITE;
/*!40000 ALTER TABLE `card_manufacturer` DISABLE KEYS */;
INSERT INTO `card_manufacturer` VALUES (1,'Upper Deck'),(2,'Topps'),(3,'Fleer'),(4,'Leaf'),(5,'Panini'),(6,'Classic'),(7,'Score Board');
/*!40000 ALTER TABLE `card_manufacturer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card_theme`
--

DROP TABLE IF EXISTS `card_theme`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_theme` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `brand_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_card_theme_brand_id` (`brand_id`),
  CONSTRAINT `fk_card_theme_brand` FOREIGN KEY (`brand_id`) REFERENCES `card_brand` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=621 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_theme`
--

LOCK TABLES `card_theme` WRITE;
/*!40000 ALTER TABLE `card_theme` DISABLE KEYS */;
INSERT INTO `card_theme` VALUES (1,'Base Set',1),(2,'Signature',1),(3,'Autographs',1),(4,'You Crash The Game Rookie Scoring',1),(5,'You Crash The Game Rookie Rebounds',1),(6,'Draft Trade Lottery Picks',1),(7,'Scouting Report',1),(8,'Crash The Game Assists/Rebounds',1),(9,'Mini Cards',1),(10,'Super Action StickUms',1),(11,'You Crash The Game Scoring',1),(12,'You Crash The Game Scoring 2 17-23 Feb',1),(13,'You Crash The Game Scoring 2 7-13 Apr',1),(14,'You Crash The Game Scoring 2 Redemption',1),(15,'Base Set',51),(16,'Golden Idol',51),(17,'Refractors',130),(18,'Parallels',130),(19,'Stars',50),(20,'Base Set',50),(21,'Memorabilia',50),(22,'Base Set',81),(23,'Lottery Exchange',81),(24,'Precious Metals',92),(25,'Class Encounters',81),(26,'Autographs',81),(27,'Rookie Sensations',81),(28,'Base Set',123),(29,'Rookies',123),(30,'Certified Autographs',129),(31,'Game Used Memorabilia',128),(32,'Base Set',100),(33,'Rookies',100),(34,'Prizm Parallels',100),(35,'Color Blasts',100),(36,'Autographs',100),(37,'Memorabilia',100),(38,'Short Prints',100),(39,'Die-Cuts',167),(40,'Red Wave',100),(41,'Blue Shimmer',100),(42,'Gold Vinyl',100),(43,'Black Prizm',100),(44,'Downtown',100),(46,'Base Set',159),(47,'Rookies',159),(48,'Autographs',159),(49,'Base Set',156),(52,'Base Set',1),(60,'Collegiate Best',176),(61,'Rack Pack',176),(62,'Base Set',176),(63,'Dish And Swish',176),(64,'Mystery Bordered',176),(65,'Mystery Bordered Test',176),(66,'Mystery Borderless',176),(67,'Autographs',4),(68,'Memorabilia',4),(500,'Base Set',51),(501,'Time Out',175),(502,'Rookie',175),(503,'Base Set',80),(504,'Wave Of The Future',80),(505,'Base Set',88),(506,'TOP This',88),(507,'Draft Redemtion',88),(508,'Magic\'s All Rookies',88),(509,'Rookie Standouts',82),(510,'Base Set',5),(511,'Holoviews',5),(512,'Base Set',6),(513,'Base Set',84),(514,'Draft Picks',84),(515,'Head Of The Class',84),(516,'Base Set',72),(517,'Base Set',144),(518,'All Rookies',144),(519,'Platinum Medailion',144),(520,'Draft Analysis',4),(521,'Base Set',4),(522,'Draft Lottery Picks',4),(523,'Predictor Award Winners',4),(524,'Special Edition',4),(525,'Rookie Standouts',4),(526,'Unstoppable',178),(527,'Base Set',178),(530,'Block Party',88),(531,'HIP Notized',88),(536,'HoopStars ',88),(537,'SlamLand',88),(538,'Sizzlin\' Sophs',88),(539,'Career Best Game',88),(540,'Base Set',82),(541,'Base Set',145),(542,'Silver Spotlight',145),(543,'All-Stars',5),(544,'Champions Of The Court',6),(545,'Championship Shots',6),(546,'Race For The Playoffs',6),(547,'Beam Team',72),(548,'Whiz Kids',50),(549,'Rattle And Roll',50),(550,'Base Set',179),(551,'Credentials',179),(552,'Star Date 2000',179),(555,'Base Set',55),(556,'All Rookie Team',144),(557,'ENCore',144),(559,'Slams & Jams',4),(560,'Base Set',56),(561,'Best Cuts',56),(562,'Retro',56),(563,'Honor Roll',56),(570,'Gladiators',176),(571,'Foundations',176),(572,'Sterling',176),(580,'ShowTime',185),(581,'ShowStopper',185),(582,'ShowPiece',185),(585,'Hardwood Leaders',81),(586,'NBA All Star Retro',81),(587,'Franchise Futures',81),(588,'Total O',81),(590,'Fly With',88),(591,'Starting Five',88),(595,'Metal Shredders',145),(596,'Maximum Metal',145),(597,'Molten Metal',145),(598,'Net-Rageous',145),(599,'Power Tools',145),(610,'Autographics',84),(611,'Close Ups',84),(612,'Net Set',84),(613,'Thunder and Lightning',84),(615,'SPX Force',5),(620,'Class Acts ',72);
/*!40000 ALTER TABLE `card_theme` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','Create grading table and add fk to card','SQL','V1__Create_grading_table_and_add_fk_to_card.sql',1449826053,'root','2025-07-07 10:38:18',45,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grading`
--

DROP TABLE IF EXISTS `grading`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grading` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `grade` float NOT NULL,
  `grading_company` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `check_grade_range` CHECK (((`grade` >= 6.0) and (`grade` <= 10.0)))
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grading`
--

LOCK TABLES `grading` WRITE;
/*!40000 ALTER TABLE `grading` DISABLE KEYS */;
INSERT INTO `grading` VALUES (1,7,'PSA'),(2,8,'PSA'),(3,9,'PSA'),(4,10,'PSA'),(5,7,'BGS'),(6,8,'BGS'),(10,9,'BGS'),(12,10,'BGS');
/*!40000 ALTER TABLE `grading` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player`
--

DROP TABLE IF EXISTS `player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `surname` varchar(255) NOT NULL,
  `team_id` bigint NOT NULL,
  `sport_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_player_sport_id` (`sport_id`),
  KEY `idx_player_team_id` (`team_id`),
  CONSTRAINT `fk_player_sport` FOREIGN KEY (`sport_id`) REFERENCES `Sport` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_player_team` FOREIGN KEY (`team_id`) REFERENCES `Team` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player`
--

LOCK TABLES `player` WRITE;
/*!40000 ALTER TABLE `player` DISABLE KEYS */;
INSERT INTO `player` VALUES (1,'Juwan','Howard',1,1),(2,'Chris','Webber',1,1);
/*!40000 ALTER TABLE `player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `season`
--

DROP TABLE IF EXISTS `season`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `season` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_season_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `season`
--

LOCK TABLES `season` WRITE;
/*!40000 ALTER TABLE `season` DISABLE KEYS */;
INSERT INTO `season` VALUES (1,'1994-95'),(2,'1995-96'),(3,'1996-97'),(4,'1997-98'),(5,'1998-99'),(6,'1999-00'),(7,'2000-01');
/*!40000 ALTER TABLE `season` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sport`
--

DROP TABLE IF EXISTS `sport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sport` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sport`
--

LOCK TABLES `sport` WRITE;
/*!40000 ALTER TABLE `sport` DISABLE KEYS */;
INSERT INTO `sport` VALUES (1,'Basketball'),(2,'Baseball'),(3,'Football');
/*!40000 ALTER TABLE `sport` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team`
--

LOCK TABLES `team` WRITE;
/*!40000 ALTER TABLE `team` DISABLE KEYS */;
INSERT INTO `team` VALUES (1,'Washington Wizards'),(2,'Denver Nuggets'),(3,'Dallas Mavericks'),(4,'Houston Rockets'),(5,'Miami Heat'),(6,'Minnesota Timberwolves'),(7,'Chicago Bulls');
/*!40000 ALTER TABLE `team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `variant`
--

DROP TABLE IF EXISTS `variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `variant` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `theme_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_variant_theme_id` (`theme_id`),
  CONSTRAINT `fk_variant_theme` FOREIGN KEY (`theme_id`) REFERENCES `card_theme` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=723 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `variant`
--

LOCK TABLES `variant` WRITE;
/*!40000 ALTER TABLE `variant` DISABLE KEYS */;
INSERT INTO `variant` VALUES (1,'Base',1),(2,'Silver',1),(3,'Gold',1),(5,'Redemption',2),(6,'Redemption Silver',2),(7,'Bronze',2),(8,'Platinum',5),(9,'Diamond',4),(10,'Emerald',6),(11,'Ruby',500),(12,'Black',61),(13,'White',5),(14,'Yellow',5),(15,'Cyan',5),(16,'Magenta',5),(17,'Red',5),(18,'Blue',5),(19,'Grean',5),(20,'Credentials',551),(21,'Legacy Collection',580),(25,'Precious Metal',1),(100,'Electric Court',62),(101,'Electric Court Gold',62),(102,'Electric Court Platinum',62),(103,'Exclusives',62),(104,'Limited',62),(105,'Press Proof',62),(106,'Spectrum',62),(107,'Game Jersey Patch',68),(108,'Autographed Jersey',67),(109,'Black Diamond Triple',62),(110,'UD Choice Prime Cuts',62),(111,'UD Glossy',62),(112,'UD Ionix Reciprocal',62),(114,'SP Authentic Finite',62),(115,'SPx Spectrum',62),(116,'SPx Grand Finale',62),(117,'Upper Deck Finite Radiance',62),(118,'Upper Deck Finite Spectrum',62),(119,'UD Black Finite',62),(120,'UD Black Platinum',62),(121,'UD Premier Rookie Auto Patch',67),(122,'UD Chronology Auto',67),(200,'Frozenfractor',17),(201,'Refractor',17),(210,'Atomic Refractor',17),(299,'Players Club',7),(300,'Players Club Platinum',7),(301,'Ultra Gold Medallion',22),(709,'Foil Tech',508),(710,'Jumbo',508),(711,'Die-Cut',511),(712,'First Day Issue',516),(713,'Members Only',516),(714,'Super Teams NBA Finals',516),(715,'Spectra Light',20),(716,'Redemption Gold',2),(717,'Silver Spotlight',541),(720,'Player\'s Private Issue',555);
/*!40000 ALTER TABLE `variant` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-05 10:24:04

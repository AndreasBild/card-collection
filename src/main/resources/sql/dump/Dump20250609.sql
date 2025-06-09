-- MySQL dump 10.13  Distrib 8.0.38, for macos14 (arm64)
--
-- Host: localhost    Database: cardcollection
-- ------------------------------------------------------
-- Server version	9.0.1

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
-- Table structure for table `card`
--

DROP TABLE IF EXISTS `card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card`
(
    `id`                 bigint  NOT NULL AUTO_INCREMENT,
    `print_run`          int     NOT NULL,
    `serial_number`      int     NOT NULL,
    `number`             varchar(255)     DEFAULT NULL,
    `rookie_card` BIT(1) DEFAULT NULL,       -- << MODIFIED TYPE
    `game_used_material` BIT(1) DEFAULT NULL, -- << MODIFIED TYPE
    `player_id`          bigint           DEFAULT NULL,
    `theme_id`           bigint           DEFAULT NULL,
    `autograph` BIT(1) DEFAULT NULL,        -- << MODIFIED TYPE
    `season_id`          bigint           DEFAULT NULL,
    `variant_id`         bigint           DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY                  `FKbyb0u8pl0bms3a11dql17ut0b` (`player_id`),
    KEY                  `FKrhm60fo96t7r89farfjnmg0n9` (`theme_id`),
    KEY                  `FK6xhb82f364llei3se8shqvxoa` (`variant_id`),
    KEY                  `FK_card_season` (`season_id`),
    CONSTRAINT `FK6xhb82f364llei3se8shqvxoa` FOREIGN KEY (`variant_id`) REFERENCES `variant` (`id`),
    CONSTRAINT `FK_card_season` FOREIGN KEY (`season_id`) REFERENCES `season` (`id`),
    CONSTRAINT `FKbyb0u8pl0bms3a11dql17ut0b` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`),
    CONSTRAINT `FKrhm60fo96t7r89farfjnmg0n9` FOREIGN KEY (`theme_id`) REFERENCES `card_theme` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1047 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card`
--

LOCK
TABLES `card` WRITE;
/*!40000 ALTER TABLE `card` DISABLE KEYS */;
INSERT INTO `card`
VALUES (1, 0, 0, '278', 1, 0, 1, 1, 0, 1, 1),
       (2, 0, 0, '278', 0, 0, 1, 2, 0, 1, 2),
       (3, 0, 0, '278', 0, 0, 1, 2, 0, 1, 3),
       (4, 750, 689, '278', 0, 0, 1, 3, 1, 1, 1),
       (5, 0, 0, 'S4', 0, 0, 1, 4, 0, 1, 5),
       (6, 0, 0, 'S4', 0, 0, 1, 4, 0, 1, 3),
       (8, 0, 0, '5', 0, 0, 1, 6, 0, 1, 1),
       (10, 0, 0, '105', 1, 0, 1, 15, 0, 1, 1),
       (11, 0, 0, '105', 0, 0, 1, 16, 0, 1, 3),
       (12, 0, 0, '98', 1, 0, 1, 501, 0, 1, 1),
       (13, 0, 0, '103', 0, 0, 1, 502, 0, 1, 1),
       (14, 0, 0, '259', 0, 0, 1, 60, 0, 1, 1),
       (15, 0, 0, '259', 0, 0, 1, 60, 0, 1, 201),
       (16, 0, 0, '288', 1, 0, 1, 62, 0, 1, 1),
       (17, 0, 0, '288', 0, 0, 1, 62, 0, 1, 201),
       (18, 0, 0, 'RP3', 0, 0, 1, 61, 0, 1, 1),
       (19, 0, 0, 'RP3', 0, 0, 1, 61, 0, 1, 201),
       (20, 0, 0, '319', 1, 0, 1, 503, 0, 1, 1),
       (21, 0, 0, '3', 0, 0, 1, 504, 0, 1, 1),
       (22, 0, 0, '381', 1, 0, 1, 22, 0, 1, 1),
       (23, 0, 0, '5', 0, 0, 1, 23, 0, 1, 1),
       (24, 0, 0, '378', 1, 0, 1, 505, 0, 1, 1),
       (25, 0, 0, '425', 0, 0, 1, 506, 0, 1, 1),
       (26, 0, 0, '5', 0, 0, 1, 507, 0, 1, 1),
       (27, 0, 0, 'AR5', 0, 0, 1, 508, 0, 1, 1),
       (28, 0, 0, 'FAR5', 0, 0, 1, 508, 0, 1, 709),
       (29, 0, 0, 'AR5', 0, 0, 1, 508, 0, 1, 710),
       (30, 0, 0, '3', 1, 0, 1, 509, 0, 1, 1),
       (31, 0, 0, '5', 1, 0, 1, 510, 0, 1, 1),
       (32, 0, 0, 'D5', 0, 0, 1, 510, 0, 1, 711),
       (33, 0, 0, 'PC24', 0, 0, 1, 511, 0, 1, 1),
       (34, 0, 0, 'PC24', 0, 0, 1, 511, 0, 1, 711),
       (35, 0, 0, '134', 1, 0, 1, 512, 0, 1, 1),
       (36, 0, 0, '134', 0, 0, 1, 512, 0, 1, 711),
       (37, 0, 0, '293', 1, 0, 1, 513, 0, 1, 1),
       (38, 0, 0, 'DP5', 0, 0, 1, 514, 0, 1, 1),
       (39, 0, 0, '2', 0, 0, 1, 515, 0, 1, 1),
       (40, 0, 0, '210', 1, 0, 1, 516, 0, 1, 1),
       (41, 0, 0, '210', 0, 0, 1, 516, 0, 1, 712),
       (42, 0, 0, '210', 0, 0, 1, 516, 0, 1, 713),
       (43, 0, 0, '210', 0, 0, 1, 516, 0, 1, 714),
       (44, 0, 0, '393', 1, 0, 1, 20, 0, 1, 1),
       (45, 0, 0, '393', 0, 0, 1, 20, 0, 1, 715),
       (46, 0, 0, '343', 1, 0, 1, 517, 0, 1, 1),
       (47, 0, 0, '3', 0, 0, 1, 518, 0, 1, 1);
/*!40000 ALTER TABLE `card` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `card_brand`
--

DROP TABLE IF EXISTS `card_brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_brand`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `name`            VARCHAR(255) NOT NULL,
    `manufacturer_id` BIGINT       NOT NULL,
    PRIMARY KEY (`id`),
    KEY               `idx_card_brand_manufacturer_id` (`manufacturer_id`),
    CONSTRAINT `fk_card_brand_manufacturer` FOREIGN KEY (`manufacturer_id`) REFERENCES `card_manufacturer` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_brand`
--

LOCK
TABLES `card_brand` WRITE;
/*!40000 ALTER TABLE `card_brand` DISABLE KEYS */;
INSERT INTO `card_brand`
VALUES (1, 'Collectors Choice', 1),
       (2, 'Exquisite', 1),
       (3, 'SP Authentic', 1),
       (4, 'Upper Deck', 1),
       (5, 'SP', 1),
       (6, 'SP Championship', 1),
       (7, 'UD3', 1),
       (8, 'SPx', 1),
       (9, 'Hardcourt', 1),
       (10, 'Black Diamond', 1),
       (11, 'SPx Finite', 1),
       (12, 'Choice', 1),
       (13, 'Ionix', 1),
       (14, 'Ovation', 1),
       (15, 'Encore', 1),
       (16, 'HoloGrFX', 1),
       (17, 'Retro', 1),
       (18, 'MVP', 1),
       (19, 'Gold Reserve', 1),
       (20, 'Victory', 1),
       (22, 'UDx', 1),
       (23, 'SLAM', 1),
       (24, 'SP Game Used', 1),
       (25, 'Glass', 1),
       (26, 'Authentics', 1),
       (27, 'Sweet Shot', 1),
       (28, 'Ultimate Victory', 1),
       (29, 'Honor Roll', 1),
       (30, 'Inspiration', 1),
       (31, 'SP Authentic Limited', 1),
       (32, 'Flight Team', 1),
       (33, 'Finite', 1),
       (34, 'Ultimate Collection', 1),
       (35, 'Championship Drive', 1),
       (36, 'Exclusives', 1),
       (37, 'Standing O', 1),
       (38, 'Legends', 1),
       (39, 'R-Class', 1),
       (40, 'Trilogy', 1),
       (41, 'Reflections', 1),
       (42, 'ESPN', 1),
       (43, 'Rookie Debut', 1),
       (44, 'Signature Edition', 1),
       (50, 'Topps', 2),
       (51, 'Embossed', 2),
       (55, 'Topps Gallery', 2),
       (56, 'Topps Bowman\'s Best',2),(58,'Topps Gold Label',2),(59,'Topps Tip Off',2),(60,'Topps Heritage',2),(61,'Topps Stars',2),(62,'Topps Reserve',2),(63,'Topps Pristine',2),(64,'Topps Jersey Edition',2),(65,'Topps Bazooka',2),(66,'Topps Turkey Red',2),(67,'Topps Contemporary Collection',2),(68,'Topps Rookie Matrix',2),(69,'Topps First Edition',2),(70,'Topps Luxury Box',2),(71,'Topps Total',2),(72,'Stadium Club',2),(80,'Flair',3),(81,'Fleer',3),(82,'Jam Session',3),(83,'SkyBox',3),(84,'SkyBox Premium',3),(85,'SkyBox Autographics',3),(86,'SkyBox E-X',3),(87,'SkyBox Z-Force',3),(88,'SkyBox Hoops',3),(89,'SkyBox LE',3),(90,'SkyBox Dominion',3),(91,'SkyBox Thunder',3),(92,'SkyBox Metal Universe',3),(93,'SkyBox Molten Metal',3),(94,'SkyBox Apex',3),(95,'SkyBox Holographics',3),(96,'SkyBox Impact',3),(97,'SkyBox Jam Session',3),(98,'Donruss',5),(99,'Donruss Optic',5),(100,'Panini Prizm',5),(101,'Panini Select',5),(102,'Panini Contenders',5),(103,'Panini Contenders Optic',5),(104,'Panini National Treasures',5),(105,'Panini Noir',5),(106,'Panini Immaculate',5),(107,'Panini Impeccable',5),(108,'Panini Court Kings',5),(109,'Panini Revolution',5),(110,'Panini Origins',5),(111,'Panini Mosaic',5),(112,'Panini Chronicles',5),(113,'Panini One and One',5),(114,'Panini Obsidian',5),(115,'Panini Absolute Memorabilia',5),(116,'Panini Illusions',5),(117,'Panini Spectra',5),(118,'Panini Dominion',5),(119,'Panini PhotoGenic',5),(120,'Panini Instant',5),(121,'Panini Draft Picks',5),(122,'Panini Hoops',5),(123,'Leaf Metal',4),(124,'Leaf Valiant',4),(125,'Leaf Ultimate Draft',4),(126,'Leaf Trinity',4),(127,'Leaf Best of Basketball',4),(128,'Leaf In The Game Used',4),(129,'Leaf Signature Series',4),(130,'Topps Chrome',2),(131,'Topps Finest',2),(132,'Topps Stadium Club',2),(133,'Topps Bazooka',2),(134,'Topps Total',2),(135,'Topps Midnight',2),(136,'SP Signature Edition',1),(137,'SP Top Prospects',1),(138,'SP Rookie Threads',1),(139,'UD Reserve',1),(140,'UD Black',1),(141,'UD Premier',1),(142,'UD Chronology',1),(143,'SkyBox Metal Universe Champions',1),(144,'Ultra',3),(145,'Fleer Metal',3),(146,'Fleer Tradition',3),(147,'Fleer Authority',3),(148,'Fleer Brilliants',3),(149,'Fleer Force',3),(150,'Fleer Mystique',3),(151,'Fleer Platinum',3),(152,'Fleer Showcase',3),(153,'Fleer Splendid',3),(154,'Leaf Limited',4),(155,'Leaf Originals',4),(156,'Scoreboard',7),(157,'Scoreboard Autographed Collection',7),(158,'Scoreboard Signature Series',7),(159,'Classic',6),(160,'Classic Draft Picks',6),(161,'Classic Games',6),(162,'Classic Images',6),(163,'Classic Road to the NBA',6),(164,'Topps Bowman',2),(165,'Topps Allen & Ginter',2),(166,'Topps Draft Picks & Prospects',2),(167,'Panini Crown Royale',5),(168,'Panini Prime',5),(169,'Panini Gold Standard',5),(170,'Panini Vanguard',5),(171,'Panini Encased',5),(172,'Panini Status',5),(173,'Panini Luminance',5),(174,'Panini Playbook',5),(175,'SkyBox Emotion',3),(176,'Finest',2);
/*!40000 ALTER TABLE `card_brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card_manufacturer`
--

DROP TABLE IF EXISTS `card_manufacturer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_manufacturer` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `brand_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_card_theme_brand_id` (`brand_id`),
  CONSTRAINT `fk_card_theme_brand` FOREIGN KEY (`brand_id`) REFERENCES `card_brand` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_theme`
--

LOCK TABLES `card_theme` WRITE;
/*!40000 ALTER TABLE `card_theme` DISABLE KEYS */;
INSERT INTO `card_theme` VALUES (1,'Base Set',1),(2,'Signature',1),(3,'Autographs',1),(4,'You Crash The Game Rookie Scoring',1),(5,'You Crash The Game Rookie Rebounds',1),(6,'Draft Trade Lottery Picks',1),(15,'Base Set',51),(16,'Golden Idol',51),(17,'Refractors',130),(18,'Parallels',130),(19,'Stars',50),(20,'Base Set',50),(21,'Memorabilia',50),(22,'Base Set',81),(23,'Lottery Exchange',81),(24,'Precious Metals',92),(25,'Jambalaya',81),(26,'Autographs',81),(27,'Memorabilia',81),(28,'Base Set',123),(29,'Rookies',123),(30,'Certified Autographs',129),(31,'Game Used Memorabilia',128),(32,'Base Set',100),(33,'Rookies',100),(34,'Prizm Parallels',100),(35,'Color Blasts',100),(36,'Autographs',100),(37,'Memorabilia',100),(38,'Short Prints',100),(39,'Die-Cuts',167),(40,'Red Wave',100),(41,'Blue Shimmer',100),(42,'Gold Vinyl',100),(43,'Black Prizm',100),(44,'Downtown',100),(45,'Kaboom!',100),(46,'Base Set',159),(47,'Rookies',159),(48,'Autographs',159),(49,'Base Set',156),(52,'Base',1),(60,'Collegiate Best',176),(61,'Rack Pack',176),(62,'Base Set',176),(63,'Rookies',4),(64,'Star Rookies',4),(65,'All-Stars',4),(66,'Checklist',4),(67,'Autographs',4),(68,'Memorabilia',4),(500,'Base Set',51),(501,'Time Out',175),(502,'Rookie',175),(503,'Base Set',80),(504,'Wave Of The Future',80),(505,'Base Set',88),(506,'TOP This',88),(507,'Draft Redemtion',88),(508,'Magic\'s All Rookies',
        88),
       (509, 'Rookie Standouts', 82),
       (510, 'Base Set', 5),
       (511, 'Holoviews', 5),
       (512, 'Base Set', 6),
       (513, 'Base Set', 84),
       (514, 'Draft Picks', 84),
       (515, 'Head Of The Class', 84),
       (516, 'Base Set', 72),
       (517, 'Base Set', 144),
       (518, 'All Rookies', 144),
       (519, 'Platinum Medailion', 144);
/*!40000 ALTER TABLE `card_theme` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `Sport`
--

DROP TABLE IF EXISTS `sport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sport` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                         `name` VARCHAR(100) NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sport`
--

LOCK
TABLES `Sport` WRITE;
/*!40000 ALTER TABLE `sport` DISABLE KEYS */;
INSERT INTO `sport`
VALUES (1, 'Basketball'),
       (2, 'Baseball'),
       (3, 'Football');
/*!40000 ALTER TABLE `sport` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `player`
--

DROP TABLE IF EXISTS `player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                          `name` VARCHAR(255) NOT NULL,
                          `surname` VARCHAR(255) NOT NULL,
                          `team_id` BIGINT NOT NULL,
                          `sport_id` BIGINT NOT NULL,
                          PRIMARY KEY (`id`),
                          KEY `idx_player_sport_id` (`sport_id`),
                          KEY `idx_player_team_id` (`team_id`),
                          CONSTRAINT `fk_player_sport` FOREIGN KEY (`sport_id`) REFERENCES `Sport` (`id`) ON DELETE RESTRICT,
                          CONSTRAINT `fk_player_team` FOREIGN KEY (`team_id`) REFERENCES `Team` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player`
--

LOCK
TABLES `player` WRITE;
/*!40000 ALTER TABLE `player` DISABLE KEYS */;
INSERT INTO `player`
VALUES (1, 'Juwan', 'Howard', 1, 1);
/*!40000 ALTER TABLE `player` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `season`
--

DROP TABLE IF EXISTS `season`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `season`
(
    `id`   bigint       NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_season_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `season`
--

LOCK
TABLES `season` WRITE;
/*!40000 ALTER TABLE `season` DISABLE KEYS */;
INSERT INTO `season`
VALUES (1, '1994-95'),
       (2, '1995-96');
/*!40000 ALTER TABLE `season` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `Team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                        `name` VARCHAR(100) NOT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Team`
--

LOCK
TABLES `Team` WRITE;
/*!40000 ALTER TABLE `team` DISABLE KEYS */;
INSERT INTO `team`
VALUES (1, 'Washington Wizards'),
       (2, 'Denver Nuggets'),
       (3, 'Dallas Mavericks'),
       (4, 'Houston Rockets'),
       (5, 'Miami Heat'),
       (6, 'Minnesota Timberwolves'),
       (7, 'Chicago Bulls');
/*!40000 ALTER TABLE `team` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Table structure for table `variant`
--

DROP TABLE IF EXISTS `variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `variant` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `name` VARCHAR(255) NOT NULL,
                           `theme_id` BIGINT DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `idx_variant_theme_id` (`theme_id`),
                           CONSTRAINT `fk_variant_theme` FOREIGN KEY (`theme_id`) REFERENCES `card_theme` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `variant`
--

LOCK
TABLES `variant` WRITE;
/*!40000 ALTER TABLE `variant` DISABLE KEYS */;
INSERT INTO `variant`
VALUES (1, 'Base', 1),
       (2, 'Silver', 1),
       (3, 'Gold', 1),
       (5, 'Redemption', 2),
       (6, 'Redemption Gold', 2),
       (7, 'Bronze', 2),
       (8, 'Platinum', 5),
       (9, 'Diamond', 4),
       (10, 'Emerald', 6),
       (11, 'Ruby', 500),
       (12, 'Black', 61),
       (13, 'White', 5),
       (14, 'Yellow', 5),
       (15, 'Cyan', 5),
       (16, 'Magenta', 5),
       (17, 'Red', 5),
       (18, 'Blue', 5),
       (19, 'Grean', 5),
       (101, 'Electric Court Gold', 62),
       (102, 'Electric Court Platinum', 62),
       (103, 'Exclusives', 62),
       (104, 'Limited', 62),
       (105, 'Press Proof', 62),
       (106, 'Spectrum', 62),
       (107, 'Game Jersey Patch', 68),
       (108, 'Autographed Jersey', 67),
       (109, 'Black Diamond Triple', 62),
       (110, 'UD Choice Prime Cuts', 62),
       (111, 'UD Glossy', 62),
       (112, 'UD Ionix Reciprocal', 62),
       (113, 'UD Ionix Fusion', 62),
       (114, 'SP Authentic Finite', 62),
       (115, 'SPx Spectrum', 62),
       (116, 'SPx Grand Finale', 62),
       (117, 'Upper Deck Finite Radiance', 62),
       (118, 'Upper Deck Finite Spectrum', 62),
       (119, 'UD Black Finite', 62),
       (120, 'UD Black Platinum', 62),
       (121, 'UD Premier Rookie Auto Patch', 67),
       (122, 'UD Chronology Auto', 67),
       (201, 'Refractor', 17),
       (202, 'X-Fractor', 17),
       (203, 'Gold Refractor', 17),
       (204, 'Red Refractor', 17),
       (205, 'Blue Refractor', 17),
       (206, 'Green Refractor', 17),
       (207, 'Black Refractor', 17),
       (208, 'Orange Refractor', 17),
       (209, 'Superfractor (1/1)', 17),
       (210, 'Atomic Refractor', 17),
       (211, 'Pink Refractor', 17),
       (212, 'Prism Refractor', 17),
       (213, 'Sapphire Refractor', 17),
       (214, 'Base', 15),
       (215, 'Gold', 16),
       (216, 'Red', 15),
       (217, 'Blue', 15),
       (218, 'Rainbow Foil', 15),
       (219, 'Foilboard', 15),
       (220, 'Printing Plate Cyan', 15),
       (221, 'Printing Plate Magenta', 15),
       (222, 'Printing Plate Yellow', 15),
       (223, 'Printing Plate Black', 15),
       (224, 'Finest Refractor', 19),
       (225, 'Finest Gold Refractor', 19),
       (226, 'Finest Atomic Refractor', 19),
       (227, 'Finest Die-Cut', 19),
       (228, 'Finest Embossed', 19),
       (229, 'Finest Jumbos', 19),
       (230, 'Stadium Club Chrome', 19),
       (231, 'Stadium Club Chrome Refractor', 19),
       (232, 'Stadium Club Gold Parallel', 19),
       (233, 'Bowman Chrome Refractor', 16),
       (234, 'Bowman Chrome Gold Refractor', 16),
       (235, 'Topps Total Gold', 19),
       (301, 'Ultra Gold Medallion', 22),
       (302, 'Ultra Platinum Medallion', 22),
       (303, 'Flair Showcase Legacy Collection', 22),
       (304, 'Flair Showcase Row 0', 22),
       (305, 'Flair Showcase Row 1', 22),
       (306, 'Flair Showcase Row 2', 22),
       (307, 'Flair Showcase Row 3', 22),
       (308, 'Metal Universe Precious Metal Gems (PMG) Green', 24),
       (309, 'Metal Universe Precious Metal Gems (PMG) Red', 24),
       (310, 'Metal Universe Precious Metal Gems (PMG) Blue', 24),
       (311, 'Metal Universe Precious Metal Gems (PMG) Gold', 24),
       (312, 'Z-Force Rave', 22),
       (313, 'Z-Force Super Rave', 22),
       (314, 'Z-Force Big Men On Court', 22),
       (315, 'E-X Essential Credentials Future', 22),
       (316, 'E-X Essential Credentials Now', 22),
       (317, 'Fleer Metal Universe Platinum', 22),
       (318, 'Fleer Metal Universe Rubies', 22),
       (319, 'Fleer Metal Universe Emeralds', 22),
       (320, 'Fleer Ultra Platinum', 22),
       (321, 'Fleer Ultra Masterpiece (1/1)', 22),
       (322, 'Fleer Force ForceField', 22),
       (323, 'Fleer Mystique Premiere', 22),
       (324, 'Fleer Mystique Gold', 22),
       (325, 'Fleer Platinum Ruby', 22),
       (326, 'Fleer Platinum Sapphire', 22),
       (327, 'Fleer Showcase Row 4', 22),
       (328, 'Fleer Splendid Splendor', 22),
       (329, 'Fleer Hoops High Voltage', 22),
       (330, 'Fleer Hoops Spark Plugs', 22),
       (401, 'Gold', 28),
       (402, 'Silver', 28),
       (403, 'Red', 28),
       (404, 'Black', 28),
       (405, 'Prismatic', 28),
       (406, 'Numbered Parallel', 28),
       (407, 'Pulsar', 28),
       (408, 'Spectrum', 28),
       (409, 'X-Factor', 28),
       (410, 'True 1/1', 30),
       (411, 'Printing Plate', 28),
       (412, 'Sapphire', 28),
       (413, 'Ruby', 28),
       (501, 'Silver Prizm', 32),
       (502, 'Hyper Prizm', 32),
       (503, 'Red Prizm', 32),
       (504, 'Blue Prizm', 32),
       (505, 'Red White Blue Prizm', 32),
       (506, 'Green Prizm', 32),
       (507, 'Orange Prizm', 32),
       (508, 'Purple Prizm', 32),
       (509, 'Gold Prizm', 32),
       (510, 'Black Prizm', 32),
       (511, 'Mojo Prizm', 32),
       (512, 'Wave Prizm', 32),
       (513, 'Tie-Dye Prizm', 32),
       (514, 'Snakeskin Prizm', 32),
       (515, 'Neon Green Prizm', 32),
       (516, 'Choice Prizm', 32),
       (517, 'Fast Break Prizm', 32),
       (518, 'FOTL Prizm', 32),
       (519, 'Disco Prizm', 32),
       (520, 'Tiger Prizm', 32),
       (521, 'Rainbow Prizm', 32),
       (522, 'Camo Prizm', 32),
       (523, 'White Sparkle Prizm', 32),
       (524, 'Select Concourse Silver', 32),
       (525, 'Select Premier Level Silver', 32),
       (526, 'Select Courtside Silver', 32),
       (527, 'Select Zebra', 32),
       (528, 'Select Tie-Dye', 32),
       (529, 'Optic Shock', 32),
       (530, 'Optic Holo', 32),
       (531, 'Optic Gold', 32),
       (532, 'Optic Black', 32),
       (533, 'Optic Red', 32),
       (534, 'Optic Blue', 32),
       (535, 'Optic Purple', 32),
       (536, 'Optic Pink Velocity', 32),
       (537, 'Optic Black Velocity', 32),
       (538, 'Optic Green', 32),
       (539, 'Contenders Rookie Ticket Variation', 36),
       (540, 'Contenders Playoff Ticket', 36),
       (541, 'Contenders Championship Ticket', 36),
       (542, 'Contenders Super Bowl Ticket (1/1)', 36),
       (543, 'Mosaic Reactive Gold', 32),
       (544, 'Mosaic Genesis', 32),
       (545, 'Mosaic Stained Glass', 32),
       (546, 'Mosaic Peacock', 32),
       (547, 'Spectra Nebula', 32),
       (548, 'Spectra Gold', 32),
       (549, 'Noir Color', 32),
       (550, 'Noir Spotlight', 32),
       (551, 'Immaculate Dual Tags', 37),
       (552, 'National Treasures Logoman', 37),
       (553, 'National Treasures Printing Plate', 37),
       (554, 'National Treasures Laundry Tag', 37),
       (555, 'Flawless Diamond', 32),
       (556, 'Flawless Platinum (1/1)', 32),
       (557, 'Court Kings Impressionist', 32),
       (558, 'Court Kings Expressionist', 32),
       (559, 'Revolution Galactic', 32),
       (560, 'Revolution Cosmic', 32),
       (561, 'Revolution Sunburst', 32),
       (562, 'Obsidian Vitreous', 32),
       (563, 'Obsidian Electric Etch', 32),
       (564, 'Origins Gold', 32),
       (565, 'Origins Black', 32),
       (566, 'Crown Royale Silhouette', 39),
       (567, 'Crown Royale Kaboom!', 39),
       (568, 'One and One Downtown', 32),
       (569, 'One and One Gold', 32),
       (570, 'One and One Black', 32),
       (571, 'Impeccable Silver', 32),
       (572, 'Impeccable Gold', 32),
       (573, 'Certified Mirror', 32),
       (574, 'Certified Platinum', 32),
       (575, 'Threads Gold', 32),
       (576, 'Chronicles Gold', 32),
       (577, 'Chronicles Black', 32),
       (578, 'Elite Aspirations', 32),
       (579, 'Elite Status', 32),
       (580, 'Absolute Memorabilia Tools of the Trade', 32),
       (581, 'Status Blue', 32),
       (582, 'Luminance Gold', 32),
       (583, 'Playbook Nexus', 32),
       (601, 'Classic Gold', 46),
       (602, 'Classic Silver', 46),
       (603, 'Classic Blue', 46),
       (604, 'Classic Red', 46),
       (605, 'Classic Green', 46),
       (606, 'Classic Purple', 46),
       (607, 'Classic Autograph', 46),
       (701, 'Scoreboard Gold', 49),
       (702, 'Scoreboard Silver', 49),
       (703, 'Scoreboard Platinum', 49),
       (704, 'Scoreboard Autograph', 3),
       (705, 'Redemption ', 2),
       (706, 'Base', 501),
       (707, 'Base', 502),
       (708, 'Refractor', 502),
       (709, 'Foil Tech', 508),
       (710, 'Jumbo', 508),
       (711, 'Die-Cut', 511),
       (712, 'First Day Issue', 516),
       (713, 'Members Only', 516),
       (714, 'Super Teams NBA Finals', 516),
       (715, 'Spectra Light', 20);
/*!40000 ALTER TABLE `variant` ENABLE KEYS */;
UNLOCK
TABLES;

--
-- Dumping events for database 'cardcollection'
--

--
-- Dumping routines for database 'cardcollection'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-09 14:03:52

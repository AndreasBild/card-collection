-- DDL for the 'season' table
CREATE TABLE IF NOT EXISTS `season` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_season_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Optional: Add any initial static season data if desired, e.g.:
-- INSERT INTO `season` (name) VALUES ('1990-91'), ('1991-92') ON DUPLICATE KEY UPDATE name=name;

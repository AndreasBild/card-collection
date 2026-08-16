-- Initial Baseline Database Schema for Card Collection
-- Clean baseline representation generated from latest MySQL Dump

CREATE TABLE `sport` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_sport_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `card_manufacturer` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_card_manufacturer_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `card_brand` (
    `id` bigint NOT NULL,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_card_brand_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `card_theme` (
    `id` bigint NOT NULL,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_card_theme_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `variant` (
    `id` bigint NOT NULL,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_variant_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `season` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_season_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `grading` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `grade` float NOT NULL,
    `grading_company` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `check_grade_range` CHECK (((`grade` >= 6.0) and (`grade` <= 10.0)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `team` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_team_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `player` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `surname` varchar(255) NOT NULL,
    `sport_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_player_sport_id` (`sport_id`),
    KEY `idx_player_surname_name` (`surname`, `name`),
    KEY `idx_player_full_name` ((concat(`surname`, ' ', `name`))),
    CONSTRAINT `fk_player_sport` FOREIGN KEY (`sport_id`) REFERENCES `sport` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `card` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `print_run` int DEFAULT NULL,
    `serial_number` int NOT NULL,
    `number` varchar(255) DEFAULT NULL,
    `theme_id` bigint DEFAULT NULL,
    `season_id` bigint DEFAULT NULL,
    `variant_id` bigint DEFAULT NULL,
    `rookie_card` bit(1) DEFAULT NULL,
    `game_used_material` bit(1) DEFAULT NULL,
    `autograph` bit(1) DEFAULT NULL,
    `grading_id` bigint DEFAULT NULL,
    `manufacturer_id` bigint NOT NULL,
    `brand_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_card_print_run` (`print_run`),
    KEY `idx_card_mfg_brand_theme_variant` (`manufacturer_id`, `brand_id`, `theme_id`, `variant_id`),
    KEY `idx_card_mfg_brand_theme` (`manufacturer_id`, `brand_id`, `theme_id`),
    KEY `idx_card_attributes` (`rookie_card`, `game_used_material`, `autograph`),
    KEY `fk_card_brand` (`brand_id`),
    KEY `fk_card_grading` (`grading_id`),
    KEY `fk_card_manufacturer` (`manufacturer_id`),
    KEY `FK_card_season` (`season_id`),
    KEY `fk_card_theme` (`theme_id`),
    KEY `fk_card_variant` (`variant_id`),
    KEY `idx_card_number` (`number`),
    CONSTRAINT `fk_card_brand` FOREIGN KEY (`brand_id`) REFERENCES `card_brand` (`id`),
    CONSTRAINT `fk_card_grading` FOREIGN KEY (`grading_id`) REFERENCES `grading` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_card_manufacturer` FOREIGN KEY (`manufacturer_id`) REFERENCES `card_manufacturer` (`id`),
    CONSTRAINT `FK_card_season` FOREIGN KEY (`season_id`) REFERENCES `season` (`id`),
    CONSTRAINT `fk_card_theme` FOREIGN KEY (`theme_id`) REFERENCES `card_theme` (`id`),
    CONSTRAINT `fk_card_variant` FOREIGN KEY (`variant_id`) REFERENCES `variant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `card_player` (
    `card_id` bigint NOT NULL,
    `player_id` bigint NOT NULL,
    `team_id` bigint DEFAULT NULL,
    PRIMARY KEY (`card_id`, `player_id`),
    KEY `idx_card_player_player_card` (`player_id`, `card_id`),
    KEY `idx_card_player_team_card` (`team_id`, `card_id`),
    CONSTRAINT `card_player_ibfk_1` FOREIGN KEY (`card_id`) REFERENCES `card` (`id`) ON DELETE CASCADE,
    CONSTRAINT `card_player_ibfk_2` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_card_player_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

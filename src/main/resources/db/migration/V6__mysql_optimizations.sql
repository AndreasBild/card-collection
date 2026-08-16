-- Flyway Migration V6: MySQL 8 Specific Query & Optimizer Optimizations
-- 1. Functional index on player (surname, name) for fast concatenated full-name filtering/sorting
ALTER TABLE `player`
    ADD INDEX `idx_player_full_name` ((CONCAT(`surname`, ' ', `name`)));

-- 2. Index on card number for single-card lookups and sorting
ALTER TABLE `card`
    ADD INDEX `idx_card_number` (`number`);

-- 3. Update optimizer histograms on low-cardinality attributes
ANALYZE TABLE `card` UPDATE HISTOGRAM ON `rookie_card`, `game_used_material`, `autograph`, `print_run` WITH 16 BUCKETS;

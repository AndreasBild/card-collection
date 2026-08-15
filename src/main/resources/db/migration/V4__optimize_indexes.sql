-- Flyway Migration V4: Optimize Database Indexes & Clean Redundant Foreign Key Indexes

-- 1. Drop redundant duplicate indexes on `card` table
ALTER TABLE `card`
    DROP INDEX `idx_card_theme_id`,
    DROP INDEX `idx_card_variant_id`,
    DROP INDEX `idx_card_season_id`,
    DROP INDEX `idx_card_grading_id`,
    DROP INDEX `idx_card_manufacturer_id`,
    DROP INDEX `idx_card_brand_id`;

-- 2. Drop redundant indexes on `card_player` junction table
ALTER TABLE `card_player`
    DROP INDEX `idx_card_player_card_id`,
    DROP INDEX `idx_card_player_player_id`;

-- 3. Add high-performance covering index for cascading filter queries (mfg -> brand -> theme -> variant)
ALTER TABLE `card`
    ADD INDEX `idx_card_mfg_brand_theme_variant` (`manufacturer_id`, `brand_id`, `theme_id`, `variant_id`);

-- 4. Add composite covering indexes on `card_player` for player/team joins & subqueries
ALTER TABLE `card_player`
    ADD INDEX `idx_card_player_player_card` (`player_id`, `card_id`),
    ADD INDEX `idx_card_player_team_card` (`team_id`, `card_id`);

-- 5. Add unique indexes on lookup tables to guarantee data integrity and fast sorting
ALTER TABLE `sport` ADD UNIQUE INDEX `uk_sport_name` (`name`);
ALTER TABLE `card_manufacturer` ADD UNIQUE INDEX `uk_card_manufacturer_name` (`name`);
ALTER TABLE `card_brand` ADD UNIQUE INDEX `uk_card_brand_name` (`name`);
ALTER TABLE `card_theme` ADD UNIQUE INDEX `uk_card_theme_name` (`name`);
ALTER TABLE `team` ADD UNIQUE INDEX `uk_team_name` (`name`);
ALTER TABLE `variant` ADD UNIQUE INDEX `uk_variant_name` (`name`);

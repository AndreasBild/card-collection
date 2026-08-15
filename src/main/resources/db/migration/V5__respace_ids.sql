-- Flyway Migration V5: Respace Database IDs (Multiply by 10) to allow inserting rows in between
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Respace Master Reference Tables (card_brand, card_theme, variant)
UPDATE `card_brand` SET `id` = `id` * 10 ORDER BY `id` DESC;
UPDATE `card_theme` SET `id` = `id` * 10 ORDER BY `id` DESC;
UPDATE `variant` SET `id` = `id` * 10 ORDER BY `id` DESC;

-- 2. Respace Card Table IDs and Foreign Key References
UPDATE `card` SET 
    `id` = `id` * 10,
    `brand_id` = `brand_id` * 10,
    `theme_id` = CASE WHEN `theme_id` IS NOT NULL THEN `theme_id` * 10 ELSE NULL END,
    `variant_id` = CASE WHEN `variant_id` IS NOT NULL THEN `variant_id` * 10 ELSE NULL END
ORDER BY `id` DESC;

-- 3. Respace Junction Table (card_player)
UPDATE `card_player` SET `card_id` = `card_id` * 10 ORDER BY `card_id` DESC;

-- 4. Set auto_increment safe offset for card
ALTER TABLE `card` AUTO_INCREMENT = 250000;

SET FOREIGN_KEY_CHECKS = 1;

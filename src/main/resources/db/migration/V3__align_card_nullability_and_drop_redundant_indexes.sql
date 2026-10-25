-- V3: Align `card` nullability with the JPA entity and drop prefix-redundant indexes.
--
-- 1. The Card entity declares season, theme, variant, number and the three flag columns as non-null, but the
--    schema allowed NULL. A NULL row would fail at read time, so the schema now enforces the same contract.
--    Flags and number are backfilled defensively; the foreign keys are NOT backfilled on purpose, so the
--    migration fails fast instead of inventing reference data if orphans ever exist.
-- 2. InnoDB can serve a foreign key from any index that has the FK column as its leftmost column, so the
--    single-column FK indexes below are covered by existing composite indexes and only add write cost:
--      fk_card_brand            -> idx_card_brand_theme (brand_id, theme_id)
--      fk_card_manufacturer     -> idx_card_mfg_brand_theme_variant (manufacturer_id, ...)
--      fk_card_theme            -> idx_card_theme_variant (theme_id, variant_id)
--      idx_card_mfg_brand_theme -> prefix of idx_card_mfg_brand_theme_variant
--    fk_card_variant, FK_card_season and fk_card_grading stay: no other index leads with those columns.

UPDATE `card` SET `rookie_card` = b'0' WHERE `rookie_card` IS NULL;
UPDATE `card` SET `game_used_material` = b'0' WHERE `game_used_material` IS NULL;
UPDATE `card` SET `autograph` = b'0' WHERE `autograph` IS NULL;
UPDATE `card` SET `number` = '' WHERE `number` IS NULL;

ALTER TABLE `card`
    MODIFY COLUMN `number` varchar(255) NOT NULL DEFAULT '',
    MODIFY COLUMN `season_id` bigint NOT NULL,
    MODIFY COLUMN `theme_id` bigint NOT NULL,
    MODIFY COLUMN `variant_id` bigint NOT NULL,
    MODIFY COLUMN `rookie_card` bit(1) NOT NULL DEFAULT b'0',
    MODIFY COLUMN `game_used_material` bit(1) NOT NULL DEFAULT b'0',
    MODIFY COLUMN `autograph` bit(1) NOT NULL DEFAULT b'0',
    DROP INDEX `fk_card_brand`,
    DROP INDEX `fk_card_manufacturer`,
    DROP INDEX `fk_card_theme`,
    DROP INDEX `idx_card_mfg_brand_theme`;

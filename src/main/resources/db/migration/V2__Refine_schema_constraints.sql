-- MANDATORY PRE-CHECKS: Before applying this migration, ensure that no NULL values exist
-- in columns that will be altered to NOT NULL.
-- If NULLs exist, either update them to valid values or delete the rows.
-- Example placeholder UPDATE statements (uncomment and adapt if needed):
-- UPDATE Sport SET name = 'Unknown Sport' WHERE name IS NULL;
-- UPDATE variant SET name = 'Unknown Variant' WHERE name IS NULL;
-- UPDATE card_brand SET manufacturer_id = (SELECT id FROM card_manufacturer ORDER BY id LIMIT 1) WHERE manufacturer_id IS NULL; -- Replace with actual default/fallback logic
-- UPDATE card_theme SET brand_id = (SELECT id FROM card_brand ORDER BY id LIMIT 1) WHERE brand_id IS NULL; -- Replace with actual default/fallback logic
-- UPDATE player SET sport_id = (SELECT id FROM Sport ORDER BY id LIMIT 1) WHERE sport_id IS NULL; -- Replace with actual default/fallback logic
-- UPDATE player SET team_id = (SELECT id FROM Team ORDER BY id LIMIT 1) WHERE team_id IS NULL; -- Replace with actual default/fallback logic

-- 1. Modify columns to NOT NULL
ALTER TABLE Sport MODIFY name VARCHAR(100) NOT NULL;
ALTER TABLE variant MODIFY name VARCHAR(255) NOT NULL;
ALTER TABLE card_brand MODIFY manufacturer_id BIGINT NOT NULL;
ALTER TABLE card_theme MODIFY brand_id BIGINT NOT NULL;
ALTER TABLE player MODIFY sport_id INT NOT NULL;
ALTER TABLE player MODIFY team_id BIGINT NOT NULL;

-- 2. Drop redundant Foreign Keys
-- For card_brand (manufacturer_id) - dropping FKsq6h1hcxdvf1dn4vxwepxrbso (assumed redundant)
ALTER TABLE card_brand DROP FOREIGN KEY `FKsq6h1hcxdvf1dn4vxwepxrbso`;
-- For player (sport_id) - dropping FK4h1qd1hwwbpng4lklwhcpa3vl (assumed redundant)
ALTER TABLE player DROP FOREIGN KEY `FK4h1qd1hwwbpng4lklwhcpa3vl`;
-- For player (team_id) - dropping FKdvd6ljes11r44igawmpm1mc5s (assumed redundant)
ALTER TABLE player DROP FOREIGN KEY `FKdvd6ljes11r44igawmpm1mc5s`;

-- 3. Modify existing Foreign Keys from ON DELETE SET NULL to ON DELETE RESTRICT

-- For card_brand (manufacturer_id)
ALTER TABLE card_brand DROP FOREIGN KEY `fk_cardbrand_manufacturer`;
ALTER TABLE card_brand
    ADD CONSTRAINT `fk_cardbrand_manufacturer_restrict`
        FOREIGN KEY (`manufacturer_id`) REFERENCES `card_manufacturer` (`id`)
        ON DELETE RESTRICT;

-- For card_theme (brand_id)
ALTER TABLE card_theme DROP FOREIGN KEY `fk_cardtheme_brand`;
ALTER TABLE card_theme
    ADD CONSTRAINT `fk_cardtheme_brand_restrict`
        FOREIGN KEY (`brand_id`) REFERENCES `card_brand` (`id`)
        ON DELETE RESTRICT;

-- For player (sport_id)
-- The original constraint was: CONSTRAINT `fk_player_sport` FOREIGN KEY (`sport_id`) REFERENCES `Sport` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
ALTER TABLE player DROP FOREIGN KEY `fk_player_sport`;
ALTER TABLE player
    ADD CONSTRAINT `fk_player_sport_restrict`
        FOREIGN KEY (`sport_id`) REFERENCES `Sport` (`id`)
        ON DELETE RESTRICT ON UPDATE RESTRICT;

-- For player (team_id)
-- The original constraint was: CONSTRAINT `fk_player_team` FOREIGN KEY (`team_id`) REFERENCES `Team` (`id`) ON DELETE SET NULL
ALTER TABLE player DROP FOREIGN KEY `fk_player_team`;
ALTER TABLE player
    ADD CONSTRAINT `fk_player_team_restrict`
        FOREIGN KEY (`team_id`) REFERENCES `Team` (`id`)
        ON DELETE RESTRICT;

-- Note on Indexes:
-- Dropping foreign keys does not automatically drop their associated indexes if they were explicitly created
-- or named differently from the constraint.
-- After this migration, it's advisable to review `SHOW INDEX FROM <table_name>;` for each affected table
-- (`card_brand`, `player`) to identify and potentially drop orphaned or redundant indexes.
-- For example, if an index named `FKsq6h1hcxdvf1dn4vxwepxrbso` still exists on `card_brand(manufacturer_id)`
-- and is not used by `fk_cardbrand_manufacturer_restrict`, it could be dropped:
-- ALTER TABLE card_brand DROP INDEX `FKsq6h1hcxdvf1dn4vxwepxrbso`;
-- Similar checks for indexes `FK4h1qd1hwwbpng4lklwhcpa3vl` and `FKdvd6ljes11r44igawmpm1mc5s` on the `player` table.

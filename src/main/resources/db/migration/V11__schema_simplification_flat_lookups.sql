-- 1. Add manufacturer_id and brand_id columns to the card table
ALTER TABLE card ADD COLUMN manufacturer_id BIGINT;
ALTER TABLE card ADD COLUMN brand_id BIGINT;

-- 2. Populate card.brand_id and card.manufacturer_id from the existing chain
SET SQL_SAFE_UPDATES = 0;

UPDATE card c
SET c.brand_id = (SELECT t.brand_id FROM card_theme t WHERE t.id = c.theme_id)
WHERE c.id > 0;

UPDATE card c
SET c.manufacturer_id = (
    SELECT b.manufacturer_id 
    FROM card_theme t 
    JOIN card_brand b ON t.brand_id = b.id 
    WHERE t.id = c.theme_id
)
WHERE c.id > 0;

SET SQL_SAFE_UPDATES = 1;

-- 3. Set columns as NOT NULL and add foreign keys
ALTER TABLE card MODIFY COLUMN manufacturer_id BIGINT NOT NULL;
ALTER TABLE card MODIFY COLUMN brand_id BIGINT NOT NULL;

ALTER TABLE card ADD CONSTRAINT fk_card_manufacturer FOREIGN KEY (manufacturer_id) REFERENCES card_manufacturer(id);
ALTER TABLE card ADD CONSTRAINT fk_card_brand FOREIGN KEY (brand_id) REFERENCES card_brand(id);

-- 4. Deduplicate Card Themes
CREATE TABLE temp_theme_mapping (
    old_id BIGINT,
    new_id BIGINT
);

INSERT INTO temp_theme_mapping (old_id, new_id)
SELECT id, (SELECT MIN(id) FROM card_theme t2 WHERE t2.name = t1.name)
FROM card_theme t1;

SET SQL_SAFE_UPDATES = 0;
UPDATE card c
SET c.theme_id = (SELECT tm.new_id FROM temp_theme_mapping tm WHERE tm.old_id = c.theme_id)
WHERE c.id > 0;
SET SQL_SAFE_UPDATES = 1;

DELETE FROM card_theme WHERE id NOT IN (SELECT DISTINCT new_id FROM temp_theme_mapping);
DROP TABLE temp_theme_mapping;

-- Remove brand_id constraint and column from card_theme
SET @constraint_name = (
    SELECT CONSTRAINT_NAME
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'card_theme'
      AND COLUMN_NAME = 'brand_id'
      AND REFERENCED_TABLE_NAME = 'card_brand'
    LIMIT 1
);

SET @sql = IF(@constraint_name IS NOT NULL,
              CONCAT('ALTER TABLE card_theme DROP FOREIGN KEY ', @constraint_name),
              'SELECT "No theme foreign key constraint found to drop"');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE card_theme DROP COLUMN brand_id;


-- 5. Deduplicate Card Brands
CREATE TABLE temp_brand_mapping (
    old_id BIGINT,
    new_id BIGINT
);

INSERT INTO temp_brand_mapping (old_id, new_id)
SELECT id, (SELECT MIN(id) FROM card_brand b2 WHERE b2.name = b1.name)
FROM card_brand b1;

SET SQL_SAFE_UPDATES = 0;
UPDATE card c
SET c.brand_id = (SELECT bm.new_id FROM temp_brand_mapping bm WHERE bm.old_id = c.brand_id)
WHERE c.id > 0;
SET SQL_SAFE_UPDATES = 1;

DELETE FROM card_brand WHERE id NOT IN (SELECT DISTINCT new_id FROM temp_brand_mapping);
DROP TABLE temp_brand_mapping;

-- Remove manufacturer_id constraint and column from card_brand
SET @constraint_name = (
    SELECT CONSTRAINT_NAME
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'card_brand'
      AND COLUMN_NAME = 'manufacturer_id'
      AND REFERENCED_TABLE_NAME = 'card_manufacturer'
    LIMIT 1
);

SET @sql = IF(@constraint_name IS NOT NULL,
              CONCAT('ALTER TABLE card_brand DROP FOREIGN KEY ', @constraint_name),
              'SELECT "No brand foreign key constraint found to drop"');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE card_brand DROP COLUMN manufacturer_id;

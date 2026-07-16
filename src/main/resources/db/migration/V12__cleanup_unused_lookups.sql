-- 1. Remove unused themes
DELETE FROM card_theme 
WHERE id NOT IN (SELECT DISTINCT theme_id FROM card);

-- 2. Remove unused brands
DELETE FROM card_brand 
WHERE id NOT IN (SELECT DISTINCT brand_id FROM card);

-- 3. Remove unused variants
DELETE FROM variant 
WHERE id NOT IN (SELECT DISTINCT variant_id FROM card);

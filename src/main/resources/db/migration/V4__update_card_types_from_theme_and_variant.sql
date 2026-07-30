-- Automatically update card_type for cards based on card_theme and variant data

-- 1. Classify ROOKIE cards (theme IDs 130..155 or rookie/draft theme keywords)
UPDATE card c
JOIN card_theme t ON c.theme_id = t.id
SET c.card_type = 'ROOKIE'
WHERE t.id BETWEEN 130 AND 155
   OR LOWER(t.name) LIKE '%rookie%'
   OR LOWER(t.name) LIKE '%draft%'
   OR LOWER(t.name) LIKE '%rising star%'
   OR LOWER(t.name) LIKE '%first rounder%';

-- 2. Classify PROMO cards (theme IDs 307..311 or promo/convention keywords)
UPDATE card c
JOIN card_theme t ON c.theme_id = t.id
SET c.card_type = 'PROMO'
WHERE c.card_type = 'BASE'
  AND (t.id BETWEEN 307 AND 311
       OR LOWER(t.name) LIKE '%promo%'
       OR LOWER(t.name) LIKE '%mcdonald%'
       OR LOWER(t.name) LIKE '%convention%'
       OR LOWER(t.name) LIKE '%exclusives%');

-- 3. Classify INSERT cards (non-base themes like Beam Team, Holoviews, StarQuest, etc.)
UPDATE card c
JOIN card_theme t ON c.theme_id = t.id
SET c.card_type = 'INSERT'
WHERE c.card_type = 'BASE'
  AND t.name != 'Base Set'
  AND t.name != 'Base'
  AND LOWER(t.name) NOT LIKE '%base%';

-- 4. Classify PARALLEL cards (non-base variants like Refractor, Gold, Prizm, etc.)
UPDATE card c
JOIN variant v ON c.variant_id = v.id
SET c.card_type = 'PARALLEL'
WHERE c.card_type = 'BASE'
  AND v.name != 'Base'
  AND LOWER(v.name) NOT LIKE '%base%'
  AND v.id != 1;

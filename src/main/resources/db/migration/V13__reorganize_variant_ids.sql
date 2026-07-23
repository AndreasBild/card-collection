-- Disable foreign key checks & safe updates for smooth execution
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_SAFE_UPDATES = 0;

-- 1. Create temporary mapping table for remapping variant IDs
CREATE TABLE IF NOT EXISTS temp_variant_mapping (
    old_id BIGINT PRIMARY KEY,
    new_id BIGINT NOT NULL
);

TRUNCATE TABLE temp_variant_mapping;

-- Insert remapping records based on the latest Dump.sql source of truth
INSERT INTO temp_variant_mapping (old_id, new_id) VALUES
-- Block 1: Base & Standard
(1, 1),      -- Base
(61, 2),     -- Tiffany
(63, 3),     -- Classic
(310, 4),    -- First Edition
(712, 5),    -- First Day Issue
(721, 6),    -- Uncut
(699, 7),    -- Starter
(199, 8),    -- Mini
(1000, 9),   -- Flip Card
(1001, 10),  -- Club Set
(1002, 11),  -- Hot Print

-- Block 2: Colors & Metals
(2, 20),     -- Silver
(3, 21),     -- Gold
(4, 22),     -- Bronze
(6, 23),     -- Platinum
(69, 24),    -- Copper
(7, 25),     -- Emerald
(8, 26),     -- Ruby
(9, 27),     -- Sapphire
(10, 28),    -- Black
(11, 29),    -- White
(12, 30),    -- Blue
(13, 31),    -- Red
(14, 32),    -- Green
(15, 33),    -- Pink
(19, 34),    -- Purple
(23, 35),    -- Orange
(16, 36),    -- Asia Red
(17, 37),    -- Asia Gold
(62, 38),    -- 24 K Gold
(25, 39),    -- Precious Metal

-- Block 3: Refractors & Prizms
(201, 50),   -- Refractor
(215, 51),   -- First Day Issue Refractor
(202, 52),   -- Gold Refractor
(203, 53),   -- Gold Refractor Die-Cut
(204, 54),   -- X-Fractor
(205, 55),   -- Superfractor
(206, 56),   -- Negative Refractor
(207, 57),   -- Prism Refractor
(208, 58),   -- Speckle Refractor
(209, 59),   -- Aqua Refractor
(210, 60),   -- Atomic Refractor
(211, 61),   -- Embossed Refractor
(212, 62),   -- Double Refractor
(213, 63),   -- Left Refractor
(214, 64),   -- Right Refractor
(216, 65),   -- Black Refractor
(217, 66),   -- White Refractor
(218, 67),   -- Purple Refractor
(219, 68),   -- Blue Refractor
(220, 69),   -- Green Refractor
(221, 70),   -- Pink Refractor
(222, 71),   -- Wave Refractor
(223, 72),   -- Orange Refractor
(224, 73),   -- Red Refractor
(225, 74),   -- Sapphire Refractor
(226, 75),   -- Green Wave Refractor
(227, 76),   -- Green Logo Refractor
(230, 77),   -- Orange Geometric Refractor
(231, 78),   -- Gold Geometric Refractor
(233, 79),   -- Red Geometric Refractor
(241, 80),   -- Blue Basketball Refractor
(242, 81),   -- Orange Basketball Refractor
(243, 82),   -- Pink Basketball Refractor
(244, 83),   -- Orange Sapphire Refractor
(245, 84),   -- Purple Sapphire Refractor
(246, 85),   -- RayWave Refractor
(247, 86),   -- Red Flare Refractor
(200, 87),   -- Frozenfractor
(198, 88),   -- Sepia

-- Block 4: Holos & Solstices
(501, 100),  -- Bronze Holo
(502, 101),  -- Silver Holo
(503, 102),  -- Gold Holo
(504, 103),  -- Platinum Holo
(507, 104),  -- Sapphire Holo
(508, 105),  -- Orange Holo
(520, 106),  -- Green Holo
(522, 107),  -- Pink Holo
(523, 108),  -- Purple Holo
(852, 109),  -- Mojo
(854, 110),  -- Dusk
(855, 111),  -- Tie Dye
(860, 112),  -- Green Wave
(861, 113),  -- Black Gold
(862, 114),  -- Summer Solstice
(863, 115),  -- Winter Solstice
(864, 116),  -- Moonrise
(865, 117),  -- Midnight
(869, 118),  -- Sparkle
(870, 119),  -- Meta
(871, 120),  -- Marble
(872, 121),  -- Nebula
(874, 122),  -- Astral
(875, 123),  -- Fractal
(876, 124),  -- Infinite
(28, 125),   -- Pulsar
(29, 126),   -- Camo
(34, 127),   -- Pulsar Orange
(35, 128),   -- Pulsar Green
(899, 129),  -- Prismatic Jack-O-Lantern

-- Block 5: Medallions, Diamonds & Clubs
(250, 130),  -- Diamond
(251, 131),  -- Double Diamond
(252, 132),  -- Triple Diamond
(299, 133),  -- Players Club
(300, 134),  -- Players Club Platinum
(301, 135),  -- Gold Medallion
(302, 136),  -- Platinum Medallion

-- Block 6: Credentials & Scripts
(20, 150),   -- Credentials
(21, 151),   -- Credentials Now
(22, 152),   -- Credentials Future
(103, 153),  -- Silver Script
(106, 154),  -- Super Script
(80, 155),   -- Century Marks

-- Block 7: Precious Metal Gems & Rave
(70, 170),   -- Precious Metal Gems
(71, 171),   -- Precious Metal Gems Red
(72, 172),   -- Precious Metal Gems Green
(73, 173),   -- Rave
(74, 174),   -- Super Rave

-- Block 8: Legacy & Choice
(30, 190),   -- Legacy Collection
(31, 191),   -- Legacy 2000
(849, 192),  -- Choice Black Gold
(850, 193),  -- Choice Red
(851, 194),  -- Choice Green

-- Block 9: Rows, Levels & Collections
(32, 210),   -- Row 1
(39, 211),   -- Flair Collection Row 1
(90, 212),   -- Level 1
(91, 213),   -- Level 2
(92, 214),   -- Level 3
(64, 215),   -- Roundball Collection
(65, 216),   -- Supreme Court Collection

-- Block 10: Special Finishes & Die-Cuts
(40, 230),   -- Embossed
(42, 231),   -- Grand Finale
(43, 232),   -- Radiance
(44, 233),   -- Spectrum
(45, 234),   -- Reciprocal
(46, 235),   -- F/X
(47, 236),   -- Final Cut
(48, 237),   -- Rainbow
(49, 238),   -- AUSome
(66, 239),   -- Masterpiece Mania
(67, 240),   -- Forcefield
(68, 241),   -- Prominence
(105, 242),  -- Standing Ovation
(109, 243),  -- UD Promo
(110, 244),  -- Plexiglass
(111, 245),  -- Acetate Crystal Collection
(112, 246),  -- Championship Court Stamp
(27, 247),   -- Gold Vinyl
(711, 248),  -- Die-Cut
(5, 249),    -- Masterpiece

-- Block 11: Seating Levels
(729, 260),  -- General Admission
(730, 261),  -- Upgrade
(731, 262),  -- Mezzanine
(735, 263),  -- Balcony
(736, 264),  -- Club Box
(739, 265),  -- Standing Room
(750, 266),  -- Heights Edition
(760, 267),  -- Tier Reserved
(761, 268),  -- Loge Level
(762, 269),  -- Main reserved

-- Block 12: Anniversary & Limited
(727, 280),  -- 10th Anniversary
(734, 281),  -- Anniversary Edition
(737, 282),  -- Century Edition
(738, 283),  -- Decade Edition
(740, 284),  -- Anniversary Silver
(741, 285),  -- Anniversary Gold
(742, 286),  -- Collection Edition
(107, 287),  -- Limited
(108, 288),  -- Limited Extra

-- Block 13: Memorabilia & Miscellaneous
(722, 300),  -- Jersey
(723, 301),  -- Jersey Multicolor
(724, 302),  -- Jersey Patch
(800, 303),  -- Jersey Prime
(150, 304),  -- Autograph
(725, 305),  -- Error Print
(726, 306),  -- Photo Proof
(415, 307),  -- Printing Plate Cyan
(416, 308),  -- Printing Plate Magenta
(417, 309),  -- Printing Plate Yellow
(418, 310),  -- Printing Plate Black
(420, 311),  -- Printers Proof
(50, 312),   -- Redemption
(51, 313),   -- Redemption Silver
(52, 314),   -- Redemption Gold
(53, 315),   -- Exclusives
(60, 316),   -- Crystal
(76, 317),   -- Titanium
(77, 318),   -- X-TRA
(78, 319),   -- Star Ruby
(79, 320),   -- Extra
(81, 321),   -- Hoopla
(82, 322),   -- Hoopla Plus
(83, 323),   -- Draft Position
(84, 324),   -- Ultimate
(85, 325),   -- Glossy
(86, 326),   -- Five Stars
(93, 327),   -- Plus
(94, 328),   -- Prime
(880, 328),  -- Prime (Duplicate -> Merged to 328)
(95, 329),   -- Century Proof Platinum
(100, 330),  -- Electric Court
(101, 331),  -- Electric Court Gold
(700, 332),  -- All-Star
(701, 333),  -- Superstar
(702, 334),  -- Red Label
(703, 335),  -- Black Label
(709, 336),  -- Foil Tech
(710, 337),  -- Jumbo
(713, 338),  -- Members Only
(714, 339),  -- Super Teams NBA Finals
(715, 340),  -- Spectra Light
(716, 341),  -- One Of A Kind
(717, 342),  -- Silver Spotlight
(718, 343),  -- Luminescent
(719, 344),  -- Illuminator
(720, 345),  -- Player's Private Issue
(733, 346),  -- Special Forces
(881, 347),  -- International
(882, 348),  -- International Red
(900, 349),  -- Super Prismatic Gold Candy Corn
(901, 350),  -- Pre Production Proof
(902, 351),  -- Pre Production
(905, 352),  -- German Edition
(41, 353);   -- Sky

-- Dynamically map any other variant IDs present in the DB that were not in the explicit list
SET @extra_id = 500;
INSERT IGNORE INTO temp_variant_mapping (old_id, new_id)
SELECT id, (@extra_id := @extra_id + 1)
FROM variant
WHERE id NOT IN (SELECT old_id FROM temp_variant_mapping)
ORDER BY id;

-- 2. Drop the foreign key constraint on the card table if it exists
SET @constraint_name = (
    SELECT CONSTRAINT_NAME
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'card'
      AND COLUMN_NAME = 'variant_id'
      AND REFERENCED_TABLE_NAME = 'variant'
    LIMIT 1
);

SET @sql = IF(@constraint_name IS NOT NULL,
              CONCAT('ALTER TABLE card DROP FOREIGN KEY ', @constraint_name),
              'SELECT "No foreign key constraint found to drop"');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. Remap variant_id in the card table using the mapping table
UPDATE card c
JOIN temp_variant_mapping m ON c.variant_id = m.old_id
SET c.variant_id = m.new_id
WHERE c.id > 0;

-- 4. Create new clean variant table and populate it from mapping
DROP TABLE IF EXISTS variant_new;
CREATE TABLE variant_new (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

INSERT INTO variant_new (id, name)
SELECT m.new_id, MIN(v.name)
FROM variant v
JOIN temp_variant_mapping m ON v.id = m.old_id
GROUP BY m.new_id;

-- 5. Fallback safety check: Any card whose variant_id is not in variant_new defaults to 1 (Base)
UPDATE card
SET variant_id = 1
WHERE variant_id IS NOT NULL AND variant_id NOT IN (SELECT id FROM variant_new);

-- 6. Swap variant tables
DROP TABLE variant;
RENAME TABLE variant_new TO variant;

-- Clean up temporary mapping table
DROP TABLE IF EXISTS temp_variant_mapping;

-- 7. Re-add foreign key constraint and restore settings
ALTER TABLE card ADD CONSTRAINT fk_card_variant FOREIGN KEY (variant_id) REFERENCES variant(id);

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

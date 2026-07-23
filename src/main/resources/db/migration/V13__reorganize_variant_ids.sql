-- 1. Create temporary mapping table for remapping variant IDs
CREATE TABLE temp_variant_mapping (
    old_id BIGINT PRIMARY KEY,
    new_id BIGINT NOT NULL
);

-- Insert remapping records based on the approved proposal
INSERT INTO temp_variant_mapping (old_id, new_id) VALUES
-- Block 1: Base & Standard
(1, 1),   -- Base
(61, 2),  -- Tiffany
(63, 3),  -- Classic
(310, 4), -- First Edition
(712, 5), -- First Day Issue
(721, 6), -- Uncut
(699, 7), -- Starter
(199, 8), -- Mini
-- Block 2: Colors & Metals
(2, 20),   -- Silver
(3, 21),   -- Gold
(4, 22),   -- Bronze
(6, 23),   -- Platinum
(69, 24),  -- Copper
(7, 25),   -- Emerald
(8, 26),   -- Ruby
(9, 27),   -- Sapphire
(10, 28),  -- Black
(11, 29),  -- White
(12, 30),  -- Blue
(13, 31),  -- Red
(14, 32),  -- Green
(15, 33),  -- Pink
(19, 34),  -- Purple
(23, 35),  -- Orange
(16, 36),  -- Asia Red
(17, 37),  -- Asia Gold
(62, 38),  -- 24 K Gold
(25, 39),  -- Precious Metal
-- Block 3: Refractors & Prizms
(201, 50), -- Refractor
(215, 51), -- First Day Issue Refractor
(202, 52), -- Gold Refractor
(203, 53), -- Gold Refractor Die-Cut
(204, 54), -- X-Fractor
(205, 55), -- Superfractor
(206, 56), -- Negative Refractor
(207, 57), -- Prism Refractor
(208, 58), -- Speckle Refractor
(209, 59), -- Aqua Refractor
(210, 60), -- Atomic Refractor
(211, 61), -- Embossed Refractor
(212, 62), -- Double Refractor
(213, 63), -- Left Refractor
(214, 64), -- Right Refractor
(216, 65), -- Black Refractor
(217, 66), -- White Refractor
(218, 67), -- Purple Refractor
(219, 68), -- Blue Refractor
(220, 69), -- Green Refractor
(221, 70), -- Pink Refractor
(222, 71), -- Wave Refractor
(223, 72), -- Orange Refractor
(224, 73), -- Red Refractor
(225, 74), -- Sapphire Refractor
(226, 75), -- Green Wave Refractor
(230, 76), -- Orange Geometric Refractor
(231, 77), -- Gold Geometric Refractor
(232, 78), -- Purple Geometric Refractor
(241, 79), -- Blue Ball Refractor
(242, 80), -- Orange Ball Refractor
(243, 81), -- Pink Ball Refractor
(200, 82), -- Frozenfractor
(198, 83), -- Sepia
-- Block 4: Holos & Mojo
(501, 100), -- Bronze Holo
(502, 101), -- Silver Holo
(503, 102), -- Gold Holo
(504, 103), -- Platinum Holo
(505, 104), -- Emerald Holo
(507, 105), -- Sapphire Holo
(520, 106), -- Green Holo
(521, 107), -- Red Holo
(522, 108), -- Pink Holo
(523, 109), -- Purple Holo
(852, 110), -- Mojo
(855, 111), -- Tie Dye
(860, 112), -- Green Wave
(861, 113), -- Black Gold
(870, 114), -- Meta
(871, 115), -- Marble
(872, 116), -- Nebula
(874, 117), -- Astral
(875, 118), -- Fractal
(876, 119), -- Infinite
(28, 120),  -- Pulsar
(29, 121),  -- Camo
(34, 122),  -- Pulsar Orange
(35, 123),  -- Pulsar Green
-- Block 5: Medallions, Diamonds & Clubs
(250, 130), -- Diamond
(251, 131), -- Double Diamond
(252, 132), -- Triple Diamond
(253, 133), -- Quadruple Diamond
(299, 134), -- Players Club
(300, 135), -- Players Club Platinum
(301, 136), -- Gold Medallion
(302, 137), -- Platinum Medallion
-- Block 6: Credentials & Scripts
(20, 150),  -- Credentials
(21, 151),  -- Credentials Now
(22, 152),  -- Credentials Future
(103, 153), -- Silver Script
(104, 154), -- Gold Script
(106, 155), -- Super Script
(80, 156),  -- Century Marks
-- Block 7: Precious Metal Gems (PMG) & Rave
(70, 170),  -- Precious Metal Gems
(71, 171),  -- Precious Metal Gems Red
(72, 172),  -- Precious Metal Gems Green
(732, 172), -- Precious Metal Gems Green (Duplicate) -> Merged to 172
(73, 174),  -- Rave
(74, 175),  -- Super Rave
(75, 176),  -- Thunder Rave
-- Block 8: Legacy & Choice
(30, 190),  -- Legacy Collection
(31, 191),  -- Legacy 2000
(850, 192), -- Choice Red
(851, 193), -- Choice Green
(853, 194), -- Choice Gold
-- Block 9: Levels & Rows
(32, 210),  -- Row 1
(33, 211),  -- Row 2
(90, 212),  -- Level 1
(91, 213),  -- Level 2
(92, 214),  -- Level 3
-- Block 10: Special Finishes & Die-Cuts
(40, 230),  -- Embossed
(43, 231),  -- Radiance
(44, 232),  -- Spectrum
(45, 233),  -- Reciprocal
(46, 234),  -- F/X
(47, 235),  -- Final Cut
(48, 236),  -- Rainbow
(49, 237),  -- AUSome
(67, 238),  -- Forcefield
(68, 239),  -- Prominence
(105, 240), -- Standing Ovation
(110, 241), -- Plexiglass
(111, 242), -- Acetate Crystal Collection
(112, 243), -- Championship Court Stamp
(27, 244),  -- Gold Vinyl
(42, 245),  -- Grand Finale
(711, 246), -- Die-Cut
-- Block 11: Seating levels
(729, 260), -- General Admission
(730, 261), -- Upgrade
(731, 262), -- Mezzanine
(735, 263), -- Balcony
(736, 264), -- Club Box
(739, 265), -- Standing Room
(760, 266), -- Tier Reserved
(761, 267), -- Loge Level
(762, 268), -- Main reserved
(750, 269), -- Heights Edition
-- Block 12: Anniversary & Limited
(727, 280), -- 10th Anniversary
(734, 281), -- Anniversary Edition
(737, 282), -- Century Edition
(738, 283), -- Decade Edition
(740, 284), -- Anniversary Silver
(741, 285), -- Anniversary Gold
(742, 286), -- Collection Edition
(107, 287), -- Limited
(108, 288), -- Limited Extra
-- Block 13: Memorabilia & Miscellaneous
(722, 300), -- Jersey
(723, 301), -- Jersey Multicolor
(724, 302), -- Jersey Patch
(800, 303), -- Jersey Prime
(150, 304), -- Autograph
(725, 305), -- Error Print
(726, 306), -- Photo Proof
(415, 307), -- Printing Plate Cyan
(416, 308), -- Printing Plate Magenta
(417, 309), -- Printing Plate Yellow
(418, 310), -- Printing Plate Black
(420, 311), -- Printers Proof
(50, 312),  -- Redemption
(51, 313),  -- Redemption Silver
(52, 314),  -- Redemption Gold
(53, 315),  -- Exclusives
(60, 316),  -- Crystal
(78, 317),  -- Star Ruby
(84, 318),  -- Ultimate
(85, 319),  -- Glossy
(86, 320),  -- Five Stars
(93, 321),  -- Plus
(94, 322),  -- Prime
(880, 323), -- Prime (Duplicate)
(95, 324),  -- Century Proof Platinum
(700, 325), -- All-Star
(701, 326), -- Superstar
(702, 327), -- Red Label
(703, 328), -- Black Label
(709, 329), -- Foil Tech
(710, 330), -- Jumbo
(713, 331), -- Members Only
(714, 332), -- Super Teams NBA Finals
(715, 333), -- Spectra Light
(716, 334), -- One Of A Kind
(717, 335), -- Silver Spotlight
(718, 336), -- Luminescent
(719, 337), -- Illuminator
(720, 338), -- Player's Private Issue
(733, 339), -- Special Forces
(900, 340), -- Super Prismatic Gold Candy Corn
(901, 341), -- Pre Production Proof
(905, 342), -- German Edition
(41, 343),  -- Sky
(79, 344),  -- Extra
(81, 345),  -- Hoopla
(82, 346),  -- Hoopla Plus
(83, 347);  -- Draft Position

-- 2. Drop foreign key constraint on the card table
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

-- 3. Remap variant_id in the card table
SET SQL_SAFE_UPDATES = 0;
UPDATE card c
JOIN temp_variant_mapping m ON c.variant_id = m.old_id
SET c.variant_id = m.new_id
WHERE c.id > 0;

-- Merge any duplicate name "Prime" cards (old 880/new 323 mapped to old 94/new 322)
UPDATE card c
SET c.variant_id = 322
WHERE c.variant_id = 323 AND c.id > 0;
SET SQL_SAFE_UPDATES = 1;

-- 4. Create new clean variant table
CREATE TABLE variant_new (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Insert remapped variants (filter out duplicate new_ids such as 172 and 323)
INSERT INTO variant_new (id, name)
SELECT DISTINCT m.new_id, v.name
FROM variant v
JOIN temp_variant_mapping m ON v.id = m.old_id
WHERE m.new_id != 323;

-- 5. Swap variant tables
DROP TABLE variant;
RENAME TABLE variant_new TO variant;

-- Clean up mapping table
DROP TABLE temp_variant_mapping;

-- 6. Re-add foreign key constraint
ALTER TABLE card ADD CONSTRAINT fk_card_variant FOREIGN KEY (variant_id) REFERENCES variant(id);

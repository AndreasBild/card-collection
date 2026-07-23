-- Disable foreign key checks & safe updates for smooth execution
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_SAFE_UPDATES = 0;

-- 1. Create temporary mapping table for remapping brand IDs
CREATE TABLE IF NOT EXISTS temp_brand_mapping (
    old_id BIGINT PRIMARY KEY,
    new_id BIGINT NOT NULL
);

TRUNCATE TABLE temp_brand_mapping;

-- Insert remapping records based on manufacturer blocks
INSERT INTO temp_brand_mapping (old_id, new_id) VALUES
-- Block 1: Upper Deck (IDs 1–49)
(4, 1),      -- Upper Deck
(1, 2),      -- Collectors Choice
(5, 3),      -- SP
(3, 4),      -- SP Authentic
(6, 5),      -- SP Championship
(8, 6),      -- SPx
(11, 7),     -- SPx Finite
(21, 8),     -- SP Signature Edition
(24, 9),     -- SP Game Used Edition
(7, 10),     -- UD3
(9, 11),     -- UD Hardcourt
(10, 12),    -- UD Black Diamond
(12, 13),    -- UD Choice
(13, 14),    -- UD Ionix
(14, 15),    -- UD Ovation
(15, 16),    -- UD Encore
(16, 17),    -- UD HoloGrFX
(17, 18),    -- UD Retro
(18, 19),    -- UD MVP
(19, 20),    -- UD Gold Reserve
(20, 21),    -- UD Victory
(22, 22),    -- UDx
(23, 23),    -- UD SLAM
(25, 24),    -- UD Glass
(26, 25),    -- UD Authentics
(27, 26),    -- UD Sweet Shot
(28, 27),    -- UD Ultimate Victory
(29, 28),    -- UD Honor Roll
(30, 29),    -- UD Inspirations
(32, 30),    -- UD Flight Team
(33, 31),    -- UD Finite
(34, 32),    -- UD Ultimate Collection
(35, 33),    -- UD Championship Drive
(37, 34),    -- UD Standing O
(38, 35),    -- UD Legends
(39, 36),    -- UD R-Class
(40, 37),    -- UD Trilogy
(41, 38),    -- UD Reflections
(42, 39),    -- UD ESPN
(43, 40),    -- UD Rookie Debut
(45, 41),    -- UD Folz
(138, 42),   -- UD Pros & Prospects
(139, 43),   -- UD Reserve
(300, 44),   -- UD Kellogg's NBA
(301, 45),   -- UD All Star LineUp
(302, 46),   -- UD Artifacts

-- Block 2: Topps (IDs 50–99)
(50, 50),    -- Topps
(73, 51),    -- Topps Chrome
(176, 52),   -- Topps Finest
(72, 53),    -- Topps Stadium Club
(132, 54),   -- Topps Stadium Club Chrome
(55, 55),    -- Topps Gallery
(56, 56),    -- Topps Bowman's Best
(58, 57),    -- Topps Gold Label
(59, 58),    -- Topps Tip Off
(60, 59),    -- Topps Heritage
(61, 60),    -- Topps Stars
(62, 61),    -- Topps Reserve
(63, 62),    -- Topps Pristine
(64, 63),    -- Topps Jersey Edition
(65, 64),    -- Topps Game Jerseys
(66, 65),    -- Topps Turkey Red
(67, 66),    -- Topps Contemporary Collection
(68, 67),    -- Topps Rookie Matrix
(69, 68),    -- Topps First Edition
(70, 69),    -- Topps Luxury Box
(71, 70),    -- Topps Total
(133, 71),   -- Topps Bazooka
(134, 72),   -- Topps Triple Threats
(135, 73),   -- Topps Midnight
(136, 74),   -- Topps Cosmic Chrome
(51, 75),    -- Topps Embossed
(210, 76),   -- Topps Sprite Minyard

-- Block 3: Fleer / SkyBox / Flair (IDs 100–199)
(81, 100),   -- Fleer
(144, 101),  -- Ultra
(82, 102),   -- Jam Session
(145, 103),  -- Fleer Metal
(191, 104),  -- Fleer Metal Universe
(190, 105),  -- Fleer Metal Universe Championship
(146, 106),  -- Fleer Tradition
(147, 107),  -- Fleer Authority
(148, 108),  -- Fleer Brilliants
(149, 109),  -- Fleer Force
(150, 110),  -- Fleer Mystique
(151, 111),  -- Fleer Platinum
(152, 112),  -- Fleer Showcase
(153, 113),  -- Fleer Vintage 61
(187, 114),  -- Fleer NBA Hoops Stars
(188, 115),  -- Fleer Hot Shots
(189, 116),  -- Fleer Box Score
(192, 117),  -- Fleer Focus
(193, 118),  -- Fleer Futures
(194, 119),  -- Fleer Game Time
(195, 120),  -- Fleer Genuine
(196, 121),  -- Fleer Legacy
(197, 122),  -- Fleer Premium
(198, 123),  -- Fleer Triple Crown
(199, 124),  -- Fleer Exclusives
(200, 125),  -- Fleer Hoops Hot Prospects
(201, 126),  -- Fleer Marquee
(202, 127),  -- Fleer Maximum
(203, 128),  -- Fleer Shoebox
(204, 129),  -- Fleer Authentix
(206, 130),  -- Fleer Patchworks
(84, 131),   -- SkyBox Premium
(86, 132),   -- E-X
(87, 133),   -- SkyBox Z-Force
(88, 134),   -- SkyBox Hoops
(90, 135),   -- SkyBox Dominion
(91, 136),   -- SkyBox Thunder
(93, 137),   -- SkyBox Molten Metal
(94, 138),   -- SkyBox Apex
(96, 139),   -- SkyBox Impact
(175, 140),  -- SkyBox Emotion
(177, 141),  -- SkyBox E-X Century
(178, 142),  -- E-XL
(179, 143),  -- SkyBox E-2000
(180, 144),  -- SkyBox E-X2001
(205, 145),  -- Skybox Hoops Decade
(80, 146),   -- Flair
(79, 147),   -- Flair Final Edition
(185, 148),  -- Flair Showcase

-- Block 4: Panini (IDs 200–249)
(400, 200),  -- Panini
(402, 201),  -- Panini Prizm
(401, 202),  -- Panini Select
(498, 203),  -- Panini Donruss
(499, 204),  -- Panini Donruss Optic
(470, 205),  -- Panini Contenders
(403, 206),  -- Panini Contenders Optic
(411, 207),  -- Panini Mosaic
(167, 208),  -- Panini Crown Royale
(169, 209),  -- Panini Gold Standard
(171, 210),  -- Panini Encased
(404, 211),  -- Panini National Treasures
(405, 212),  -- Panini Noir
(406, 213),  -- Panini Immaculate Collection
(407, 214),  -- Panini Impeccable
(408, 215),  -- Panini Court Kings
(409, 216),  -- Panini Revolution
(414, 217),  -- Panini Season Update
(415, 218),  -- Panini Absolute Memorabilia
(416, 219),  -- Panini Illusions
(417, 220),  -- Panini Spectra
(418, 221),  -- Panini Dominion
(419, 222),  -- Panini Momentum
(422, 223),  -- Panini Hoops
(423, 224),  -- Panini Totally Certified
(424, 225),  -- Panini Elite Black Box
(425, 226),  -- Panini Timeless Treasures
(426, 227),  -- Panini Elite
(427, 228),  -- Panini Intrigue
(428, 229),  -- Panini Signatures
(429, 230),  -- Panini Flawless
(430, 231),  -- Panini Threads
(431, 232),  -- Panini Essentials
(432, 233),  -- Panini Certified
(433, 234),  -- Panini Flux
(434, 235),  -- Panini Donruss Elite
(435, 236),  -- Panini Prizm Draft Picks

-- Block 5: Leaf (IDs 250–269)
(123, 250),  -- Leaf Metal
(128, 251),  -- Leaf In The Game Used Sports
(130, 252),  -- Leaf Glory Of The Game

-- Block 6: Classic, Score Board, Signature Rookies & Misc (IDs 270+)
(605, 270),  -- Classic
(606, 271),  -- Classic 4 Sports
(607, 272),  -- Superior Pics
(608, 273),  -- Ted Williams
(610, 274),  -- Score Board
(600, 275),  -- Signature Rookies
(620, 276),  -- Pro Mags
(630, 277),  -- Assets
(631, 278),  -- Assets Gold
(635, 279),  -- Images 95
(640, 280);  -- Visions

-- Dynamically map any other brand IDs present in the DB that were not in the explicit list
SET @extra_id = 500;
INSERT IGNORE INTO temp_brand_mapping (old_id, new_id)
SELECT id, (@extra_id := @extra_id + 1)
FROM card_brand
WHERE id NOT IN (SELECT old_id FROM temp_brand_mapping)
ORDER BY id;

-- 2. Drop the foreign key constraint on the card table if it exists
SET @constraint_name = (
    SELECT CONSTRAINT_NAME
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'card'
      AND COLUMN_NAME = 'brand_id'
      AND REFERENCED_TABLE_NAME = 'card_brand'
    LIMIT 1
);

SET @sql = IF(@constraint_name IS NOT NULL,
              CONCAT('ALTER TABLE card DROP FOREIGN KEY ', @constraint_name),
              'SELECT "No foreign key constraint found to drop"');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. Remap brand_id in the card table using the mapping table
UPDATE card c
JOIN temp_brand_mapping m ON c.brand_id = m.old_id
SET c.brand_id = m.new_id
WHERE c.id > 0;

-- 4. Create new clean card_brand table and populate it from mapping
DROP TABLE IF EXISTS card_brand_new;
CREATE TABLE card_brand_new (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

INSERT INTO card_brand_new (id, name)
SELECT m.new_id, MIN(b.name)
FROM card_brand b
JOIN temp_brand_mapping m ON b.id = m.old_id
GROUP BY m.new_id;

-- 5. Fallback safety check: Any card whose brand_id is not in card_brand_new defaults to 1 (Upper Deck)
UPDATE card
SET brand_id = 1
WHERE brand_id IS NOT NULL AND brand_id NOT IN (SELECT id FROM card_brand_new);

-- 6. Swap card_brand tables
DROP TABLE card_brand;
RENAME TABLE card_brand_new TO card_brand;

-- Clean up temporary mapping table
DROP TABLE IF EXISTS temp_brand_mapping;

-- 7. Re-add foreign key constraint and restore settings
ALTER TABLE card ADD CONSTRAINT fk_card_brand FOREIGN KEY (brand_id) REFERENCES card_brand(id);

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

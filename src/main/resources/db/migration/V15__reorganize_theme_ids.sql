-- Disable foreign key checks & safe updates for smooth execution
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_SAFE_UPDATES = 0;

-- 1. Create temporary mapping table for remapping theme IDs
CREATE TABLE IF NOT EXISTS temp_theme_mapping (
    old_id BIGINT PRIMARY KEY,
    new_id BIGINT NOT NULL
);

TRUNCATE TABLE temp_theme_mapping;

-- Insert remapping records based on functional theme blocks
INSERT INTO temp_theme_mapping (old_id, new_id) VALUES
-- Block 1: Base Sets & Default Card Types
(1, 1),      -- Base Set
(562, 2),    -- Retro
(600, 3),    -- Class 1
(601, 4),    -- Class 2
(602, 5),    -- Class 3
(675, 6),    -- Traditions
(636, 7),    -- O-Pee-Chee
(1001, 8),   -- Check It Out
(611, 9),    -- Close Ups
(970, 10),   -- Picture Perfect
(871, 11),   -- Preview
(524, 12),   -- Special Edition
(575, 13),   -- No Protector

-- Block 2: Autographs & Signatures
(51, 20),    -- Autograph
(3, 21),     -- Autographs
(2, 22),     -- Signature
(610, 23),   -- Autographics
(702, 24),   -- Sign Of The Times
(710, 25),   -- Co-Signers
(916, 26),   -- Fresh Ink
(973, 27),   -- Dark Marks
(972, 28),   -- Notch Signatures
(974, 29),   -- Autograph Variation
(618, 30),   -- Chromographs
(617, 31),   -- '72 Topps Autographs
(643, 32),   -- Season Ticket Autograph
(137, 33),   -- Fast Break Signatures
(1415, 34),  -- Fast Break Autographs
(30, 35),    -- Certified Autographs
(45, 36),    -- National Party Autographs
(1262, 37),  -- Determination Autographs
(1263, 38),  -- Retired Autographs
(1286, 39),  -- Signatures
(1293, 40),  -- 14K Autograph
(1294, 41),  -- Signs Of Gold
(1300, 42),  -- Monumental Marks
(1305, 43),  -- Private Signings
(1306, 44),  -- Game Gear Autographs
(1309, 45),  -- Colossal NBA Logoman Signatures
(1313, 46),  -- Peerless Signatures
(1316, 47),  -- HOT Signatures
(1319, 48),  -- Triple Autographs
(1321, 49),  -- Shadow Box Signatures
(1323, 50),  -- Heralded Signatures
(1325, 51),  -- Autograph Jerseys
(1330, 52),  -- Endorsement
(1335, 53),  -- Elite Signatures
(1336, 54),  -- Dominator Signatures
(1337, 55),  -- Significant Signatures
(1342, 56),  -- Premium Ink Signatures
(1343, 57),  -- Flawless Autographs
(1344, 58),  -- Legendary Scripts
(1346, 59),  -- Indelible Ink
(1347, 60),  -- Impeccable Stars Signatures
(1348, 61),  -- NBA Legends Ink
(1349, 62),  -- Distinguished Autographs
(1360, 63),  -- Scripted Signatures
(1372, 64),  -- Retro Series Signatures
(1373, 65),  -- Opti-Graphs
(1374, 66),  -- Perennial Contenders
(1375, 67),  -- Contenders Autographs
(1380, 68),  -- Established Autographs
(1385, 69),  -- Sensational Signatures
(1390, 70),  -- Signatures Prizms
(1395, 71),  -- Flux Signatures
(1410, 72),  -- Legendary Contenders Autographs
(1421, 73),  -- Shadow Signatures
(1430, 74),  -- Icons Autographs
(1440, 75),  -- Legacy Signatures
(1451, 76),  -- Signature Series Superscribe
(1460, 77),  -- Game Used Memorabilia Autograph
(2006, 78),  -- Tetrad Authentic Signature
(2038, 79),  -- Authentic Signature

-- Block 3: Game-Used Relics & Jersey Swatches
(1124, 80),  -- Game Jersey
(1182, 81),  -- Game Patch
(1183, 82),  -- Game Materials
(1050, 83),  -- Game Worn Jersey
(1107, 84),  -- Dual Jerseys
(1126, 85),  -- Authentic Fabrics
(1129, 86),  -- Foursome Fabrics
(1131, 87),  -- Four On Four Fabrics
(1132, 88),  -- Tag Team Quads
(1139, 89),  -- Elements Triple Warm Ups
(704, 90),   -- Authentic Memorabilia
(1061, 91),  -- Authentic Arenas
(1202, 92),  -- Dual Fabric
(1225, 93),  -- Relics Combos
(1236, 94),  -- Divisional Artifacts
(1271, 95),  -- Materials
(1310, 96),  -- Retro Materials
(1317, 97),  -- Timeless Treasures Materials
(1322, 98),  -- Dual Patch Jersey Number
(1340, 99),  -- Patch
(1455, 100), -- Game Used 2
(936, 101),  -- Rare Finds Jersey
(59, 102),   -- Far East Fabrics
(70, 103),   -- Dual Shooting Shirts
(594, 104),  -- Genuine Coverage Jersey
(1101, 105), -- Sweat & Tears
(1127, 106), -- By The Letter
(1180, 107), -- Majestic Materials
(2007, 108), -- Tetrad Autobilia

-- Block 4: Rookies & Draft Picks
(502, 130),  -- Rookie
(514, 131),  -- Draft Picks
(507, 132),  -- Draft Redemption
(6, 133),    -- Draft Trade Lottery Picks
(522, 134),  -- Draft Lottery Picks
(520, 135),  -- Draft Analysis
(2016, 136), -- Draft Day
(23, 137),   -- Lottery Exchange
(2037, 138), -- Lottery Pic
(27, 139),   -- Rookie Sensations
(509, 140),  -- Rookie Standouts
(2020, 141), -- Rookie Showcase
(515, 142),  -- Head Of The Class
(518, 143),  -- All Rookies
(508, 144),  -- Magic's All Rookies
(556, 145),  -- All Rookie Team
(564, 146),  -- Rising Stars
(587, 147),  -- Franchise Futures
(552, 148),  -- Star Date 2000
(504, 149),  -- Wave Of The Future
(548, 150),  -- Whiz Kids
(538, 151),  -- Sizzlin' Sophs
(2009, 152), -- First Rounders
(2036, 153), -- Instant Impact
(25, 154),   -- Class Encounters
(7, 155),    -- Scouting Report

-- Block 5: Inserts, Parallel Finishes & Holos
(511, 180),  -- Holoviews
(651, 181),  -- Holoview Heroes
(745, 182),  -- Holojams
(1261, 183), -- Spectrum
(39, 184),   -- Die-Cuts
(1281, 185), -- Die Cut
(16, 186),   -- Golden Idol
(33, 187),   -- Comic
(53, 188),   -- High Voltage
(60, 189),   -- Collegiate Best
(61, 190),   -- Rack Pack
(64, 191),   -- Mystery Bordered
(65, 192),   -- Mystery Bordered Test
(66, 193),   -- Mystery Borderless
(631, 194),  -- Mystery Finest Bordered
(632, 195),  -- Mystery Finest Borderless
(622, 196),  -- High Risers
(621, 197),  -- Fusion
(820, 198),  -- Heavy Metal
(821, 199),  -- Heavy Metal Fusion
(822, 200),  -- Heavy Metal Xplosion
(881, 201),  -- Skyonix
(696, 202),  -- Silver Slams
(698, 203),  -- Gold Universe
(697, 204),  -- Titanium
(595, 205),  -- Metal Shredders
(596, 206),  -- Maximum Metal
(597, 207),  -- Molten Metal
(2008, 208), -- Kromax
(2011, 209), -- Super Acrylium
(609, 210),  -- Electrified
(648, 211),  -- Z-Cling
(711, 212),  -- Triumvirate
(1282, 213), -- Press Proof
(1285, 214), -- Status
(1287, 215), -- Aspirations
(1291, 216), -- Gold Mining
(1400, 217), -- Trophy Collection

-- Block 6: Star Power, All-Stars & Champions
(543, 240),  -- All-Stars
(642, 241),  -- All Stars
(851, 242),  -- Star Power
(852, 243),  -- Top Flight
(665, 244),  -- StarQuest
(544, 245),  -- Champions Of The Court
(545, 246),  -- Championship Shots
(547, 247),  -- Beam Team
(570, 248),  -- Gladiators
(558, 249),  -- Court Masters
(747, 250),  -- SuperPowers
(748, 251),  -- Forces
(573, 252),  -- Force
(40, 253),   -- Dicky Vitale Prime Time Players
(2003, 254), -- Prime Time Powers
(2000, 255), -- Royal Court
(740, 256),  -- Court Perspectives
(752, 257),  -- Home Court Advantage
(751, 258),  -- High Court
(753, 259),  -- Baseline Groves
(754, 260),  -- Power In The Paint
(565, 261),  -- Big Shots
(567, 262),  -- Jam City
(568, 263),  -- Neat Feats
(569, 264),  -- Rim Rocker
(559, 265),  -- Slams & Jams
(537, 266),  -- SlamLand
(660, 267),  -- Slam! Dunk
(531, 268),  -- HIP Notized
(530, 269),  -- Block Party
(536, 270),  -- HoopStars
(598, 271),  -- Net-Rageous
(599, 272),  -- Power Tools
(612, 273),  -- Net Set
(613, 274),  -- Thunder and Lightning
(614, 275),  -- Jam Pack
(615, 276),  -- SPX Force
(647, 277),  -- Vortex
(646, 278),  -- Zuperman
(649, 279),  -- Fast Track
(677, 280),  -- Game Breakers
(678, 281),  -- Key Ingredients
(679, 282),  -- Zone
(685, 283),  -- Frequent Flyer Club
(686, 284),  -- Talkin' Hoops
(691, 285),  -- All-Millennium Team
(699, 286),  -- Big Ups
(506, 287),  -- TOP This
(526, 288),  -- Unstoppable
(1002, 289), -- Dynamite Dunks

-- Block 7: Contests, Games & Promos
(4, 290),    -- You Crash The Game Rookie Scoring
(12, 291),   -- You Crash The Game Scoring 2 17-23 Feb
(13, 292),   -- You Crash The Game Scoring 2 7-13 Apr
(14, 293),   -- You Crash The Game Scoring 2 Redemption
(8, 294),    -- Crash The Game Assists/Rebounds
(523, 295),  -- Predictor Award Winners
(528, 296),  -- Predictor Scoring
(529, 297),  -- Predictor TV Cel
(988, 298),  -- SportsNut
(9, 299),    -- Mini Cards
(10, 300),   -- Super Action StickUms
(43, 301),   -- Classic Game Card
(44, 302),   -- Classic Picks
(41, 303),   -- Bonus Cards
(634, 304),  -- Super Team Conference Winners Bordered
(635, 305),  -- Super Team Conference Winners Borderless
(644, 306),  -- Super Teams
(703, 307),  -- Buy Back
(782, 308),  -- McDonald's All American
(2015, 309), -- National Sportscollector Convention
(1450, 310), -- Helloween Exclusives
(1276, 311), -- Green Week

-- Block 8: Highlights, Moments & Milestones
(18, 330),   -- Hardwood Classics
(1128, 331), -- Hardcourt Classics
(585, 332),  -- Hardwood Leaders
(739, 333),  -- Defining Moments
(628, 334),  -- NBA at 50
(637, 335),  -- Minted In Springfield
(539, 336),  -- Career Best Game
(546, 337),  -- Race For The Playoffs
(63, 338),   -- Dish And Swish
(69, 339),   -- Building A Winner
(566, 340),  -- Inside/Outside
(571, 341),  -- Foundations
(572, 342),  -- Sterling
(576, 343),  -- Arena Stars
(577, 344),  -- Double Feature
(578, 345),  -- Flair
(579, 346),  -- Style
(580, 347),  -- ShowTime
(581, 348),  -- ShowStopper
(582, 349),  -- ShowPiece
(583, 350),  -- Showcase
(584, 351),  -- Grace
(586, 352),  -- NBA All Star Retro
(588, 353),  -- Total O
(590, 354),  -- Fly With
(591, 355),  -- Starting Five
(592, 356),  -- Shout Outs
(608, 357),  -- Stand Outs
(620, 358),  -- Class Acts
(623, 359),  -- Top Crop
(624, 360),  -- Members 55
(626, 361),  -- ProFiles
(629, 362),  -- DNA
(639, 363),  -- Rock Stars
(641, 364),  -- The Winning Edge
(652, 365),  -- Limited Access
(653, 366),  -- Zensations
(654, 367),  -- Winning Materials
(730, 368),  -- Sweet Deal
(731, 369),  -- Ultrabilities
(741, 370),  -- Heart & Soul
(742, 371),  -- Team Mates
(743, 372),  -- Ultimates
(744, 373),  -- Highway 99
(749, 374),  -- Now Showing
(770, 375),  -- Mirror Image
(800, 376),  -- Passion
(801, 377),  -- Power
(865, 378),  -- East/West
(872, 379),  -- Reserve
(900, 380),  -- Diamond Cut
(901, 381),  -- Diamond Might
(918, 382),  -- One On One
(955, 383),  -- SPXcitement
(971, 384),  -- Seasons Best Gliders
(986, 385),  -- Jam Time
(987, 386),  -- Classic
(1063, 387), -- Team Leaders
(1111, 388), -- Tip Off Trios
(1115, 389), -- School Ties
(1144, 390), -- Then and Now
(1149, 391), -- 4 on 1 Stickers
(1151, 392), -- Winning Combos
(1201, 393), -- Compare and Contrast
(1235, 394), -- Conference Pairings
(1292, 395), -- Team Logo
(1307, 396), -- Timeless Talents
(1311, 397), -- Back To The Future
(1312, 398), -- Hometown Heroes
(1314, 399), -- Impact Impressions
(1320, 400), -- Team Logos Numbers
(1350, 401), -- Canvas Creations
(1355, 402), -- Main Exhibit Legends
(1365, 403), -- Called To Excellence
(2010, 404); -- Gold Standard

-- Dynamically map any other theme IDs present in the DB that were not in the explicit list
SET @extra_id = 1000;
INSERT IGNORE INTO temp_theme_mapping (old_id, new_id)
SELECT id, (@extra_id := @extra_id + 1)
FROM card_theme
WHERE id NOT IN (SELECT old_id FROM temp_theme_mapping)
ORDER BY id;

-- 2. Drop the foreign key constraint on the card table if it exists
SET @constraint_name = (
    SELECT CONSTRAINT_NAME
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'card'
      AND COLUMN_NAME = 'theme_id'
      AND REFERENCED_TABLE_NAME = 'card_theme'
    LIMIT 1
);

SET @sql = IF(@constraint_name IS NOT NULL,
              CONCAT('ALTER TABLE card DROP FOREIGN KEY ', @constraint_name),
              'SELECT "No foreign key constraint found to drop"');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. Remap theme_id in the card table using the mapping table
UPDATE card c
JOIN temp_theme_mapping m ON c.theme_id = m.old_id
SET c.theme_id = m.new_id
WHERE c.id > 0;

-- 4. Create new clean card_theme table and populate it from mapping
DROP TABLE IF EXISTS card_theme_new;
CREATE TABLE card_theme_new (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

INSERT INTO card_theme_new (id, name)
SELECT m.new_id, MIN(t.name)
FROM card_theme t
JOIN temp_theme_mapping m ON t.id = m.old_id
GROUP BY m.new_id;

-- 5. Fallback safety check: Any card whose theme_id is not in card_theme_new defaults to 1 (Base Set)
UPDATE card
SET theme_id = 1
WHERE theme_id IS NOT NULL AND theme_id NOT IN (SELECT id FROM card_theme_new);

-- 6. Swap card_theme tables
DROP TABLE card_theme;
RENAME TABLE card_theme_new TO card_theme;

-- Clean up temporary mapping table
DROP TABLE IF EXISTS temp_theme_mapping;

-- 7. Re-add foreign key constraint and restore settings
ALTER TABLE card ADD CONSTRAINT fk_card_theme FOREIGN KEY (theme_id) REFERENCES card_theme(id);

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

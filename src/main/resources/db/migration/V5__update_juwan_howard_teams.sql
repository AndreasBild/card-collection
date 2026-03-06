-- Add missing teams
INSERT INTO team (name) SELECT 'Orlando Magic' WHERE NOT EXISTS (SELECT 1 FROM team WHERE name = 'Orlando Magic');
INSERT INTO team (name) SELECT 'Charlotte Bobcats' WHERE NOT EXISTS (SELECT 1 FROM team WHERE name = 'Charlotte Bobcats');
INSERT INTO team (name) SELECT 'Portland Trail Blazers' WHERE NOT EXISTS (SELECT 1 FROM team WHERE name = 'Portland Trail Blazers');
INSERT INTO team (name) SELECT 'Michigan Wolverines' WHERE NOT EXISTS (SELECT 1 FROM team WHERE name = 'Michigan Wolverines');

-- Update Juwan Howard's cards
-- Use variables to store the player_id to avoid repeated subqueries if possible,
-- but since this is a pure SQL migration, we'll use lookups in each statement.

-- 2001-02: Dallas Mavericks
UPDATE card
SET team_id = (SELECT id FROM team WHERE name = 'Dallas Mavericks')
WHERE player_id = (SELECT id FROM player WHERE name = 'Juwan' AND surname = 'Howard')
AND season_id = (SELECT id FROM season WHERE name = '2001-02');

-- 2002-03: Denver Nuggets
UPDATE card
SET team_id = (SELECT id FROM team WHERE name = 'Denver Nuggets')
WHERE player_id = (SELECT id FROM player WHERE name = 'Juwan' AND surname = 'Howard')
AND season_id = (SELECT id FROM season WHERE name = '2002-03');

-- 2003-04: Orlando Magic
UPDATE card
SET team_id = (SELECT id FROM team WHERE name = 'Orlando Magic')
WHERE player_id = (SELECT id FROM player WHERE name = 'Juwan' AND surname = 'Howard')
AND season_id = (SELECT id FROM season WHERE name = '2003-04');

-- 2004-05 bis 2006-07: Houston Rockets
UPDATE card
SET team_id = (SELECT id FROM team WHERE name = 'Houston Rockets')
WHERE player_id = (SELECT id FROM player WHERE name = 'Juwan' AND surname = 'Howard')
AND season_id IN (SELECT id FROM season WHERE name IN ('2004-05', '2005-06', '2006-07'));

-- 2007-08: Dallas Mavericks
UPDATE card
SET team_id = (SELECT id FROM team WHERE name = 'Dallas Mavericks')
WHERE player_id = (SELECT id FROM player WHERE name = 'Juwan' AND surname = 'Howard')
AND season_id = (SELECT id FROM season WHERE name = '2007-08');

-- 2008-09: Charlotte Bobcats
UPDATE card
SET team_id = (SELECT id FROM team WHERE name = 'Charlotte Bobcats')
WHERE player_id = (SELECT id FROM player WHERE name = 'Juwan' AND surname = 'Howard')
AND season_id = (SELECT id FROM season WHERE name = '2008-09');

-- 2009-10: Portland Trail Blazers
UPDATE card
SET team_id = (SELECT id FROM team WHERE name = 'Portland Trail Blazers')
WHERE player_id = (SELECT id FROM player WHERE name = 'Juwan' AND surname = 'Howard')
AND season_id = (SELECT id FROM season WHERE name = '2009-10');

-- 2010-11 bis 2012-13: Miami Heat
UPDATE card
SET team_id = (SELECT id FROM team WHERE name = 'Miami Heat')
WHERE player_id = (SELECT id FROM player WHERE name = 'Juwan' AND surname = 'Howard')
AND season_id IN (SELECT id FROM season WHERE name IN ('2010-11', '2011-12', '2012-13'));

-- Seasons 1994, 1995, 1996: Michigan Wolverines
UPDATE card
SET team_id = (SELECT id FROM team WHERE name = 'Michigan Wolverines')
WHERE player_id = (SELECT id FROM player WHERE name = 'Juwan' AND surname = 'Howard')
AND season_id IN (SELECT id FROM season WHERE name IN ('1994', '1995', '1996'));

-- Ensure Juwan Howard is associated with all these teams in player_team
INSERT INTO player_team (player_id, team_id)
SELECT DISTINCT c.player_id, c.team_id
FROM card c
WHERE c.player_id = (SELECT id FROM player WHERE name = 'Juwan' AND surname = 'Howard')
AND c.team_id IS NOT NULL
AND NOT EXISTS (
    SELECT 1 FROM player_team pt
    WHERE pt.player_id = c.player_id AND pt.team_id = c.team_id
);

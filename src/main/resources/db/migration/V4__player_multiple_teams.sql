-- 1. Add team_id to card
ALTER TABLE card ADD COLUMN team_id BIGINT;

-- 2. Update card.team_id from player.team_id
UPDATE card c
SET c.team_id = (SELECT p.team_id FROM player p WHERE p.id = c.player_id);

-- 3. Add foreign key to card.team_id
ALTER TABLE card ADD CONSTRAINT fk_card_team FOREIGN KEY (team_id) REFERENCES team(id);

-- 4. Create player_team join table
CREATE TABLE player_team (
    player_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    PRIMARY KEY (player_id, team_id),
    FOREIGN KEY (player_id) REFERENCES player(id),
    FOREIGN KEY (team_id) REFERENCES team(id)
);

-- 5. Merge duplicate players (specifically Juwan Howard in this dataset)
-- First, identify a "primary" ID for each unique player name
-- In this case, we'll keep the lowest ID.
CREATE TABLE player_temp (
    old_id BIGINT,
    new_id BIGINT
);

INSERT INTO player_temp (old_id, new_id)
SELECT id, (SELECT MIN(id) FROM player p2 WHERE p2.name = p.name AND p2.surname = p.surname)
FROM player p;

-- 6. Update card.player_id to the new primary player ID
UPDATE card c
SET c.player_id = (SELECT pt.new_id FROM player_temp pt WHERE pt.old_id = c.player_id);

-- 7. Populate player_team from the old player data using the new primary player ID
INSERT INTO player_team (player_id, team_id)
SELECT DISTINCT pt.new_id, p.team_id
FROM player p
JOIN player_temp pt ON p.id = pt.old_id
WHERE p.team_id IS NOT NULL;

-- 8. Delete the redundant player records
-- Note: We must be careful about foreign keys. player_team and card already point to the new_id.
-- However, player table itself still has the old records.
DELETE FROM player WHERE id NOT IN (SELECT DISTINCT new_id FROM player_temp);

-- 9. Remove team_id from player
ALTER TABLE player DROP COLUMN team_id;

-- 10. Clean up temp table
DROP TABLE player_temp;

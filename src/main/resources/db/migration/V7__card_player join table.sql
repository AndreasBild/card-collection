-- 1. Add team_id to the card_player join table
ALTER TABLE card_player ADD COLUMN team_id BIGINT;
-- 2. Populate card_player.team_id from the current card.team_id (bypasses safe update limits)
SET SQL_SAFE_UPDATES = 0;
UPDATE card_player cp
SET cp.team_id = (SELECT c.team_id FROM card c WHERE c.id = cp.card_id);
SET SQL_SAFE_UPDATES = 1;
-- 3. Add the foreign key constraint to card_player.team_id
ALTER TABLE card_player ADD CONSTRAINT fk_card_player_team FOREIGN KEY (team_id) REFERENCES team(id) ON DELETE SET NULL;
-- 4. Drop the team_id foreign key constraint and column from the card table
-- Note: MySQL requires dropping the foreign key constraint before dropping the column.
SET @constraint_name = (
    SELECT CONSTRAINT_NAME
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'card'
      AND COLUMN_NAME = 'team_id'
      AND REFERENCED_TABLE_NAME = 'team'
    LIMIT 1
);
SET @sql = IF(@constraint_name IS NOT NULL,
              CONCAT('ALTER TABLE card DROP FOREIGN KEY ', @constraint_name),
              'SELECT "No team foreign key constraint found to drop"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
-- Drop the index created in V6__Add_indexes.sql
DROP INDEX idx_card_team_id ON card;
-- Drop the column
ALTER TABLE card DROP COLUMN team_id;
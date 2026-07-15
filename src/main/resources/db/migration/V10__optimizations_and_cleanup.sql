-- 1. Drop the redundant player_team join table
DROP TABLE IF EXISTS player_team;

-- 2. Add database indexes to card_player table for speed up
CREATE INDEX idx_card_player_player_id ON card_player(player_id);
CREATE INDEX idx_card_player_team_id ON card_player(team_id);

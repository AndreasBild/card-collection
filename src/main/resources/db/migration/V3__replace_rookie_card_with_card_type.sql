ALTER TABLE card ADD COLUMN card_type VARCHAR(50) NOT NULL DEFAULT 'BASE';

UPDATE card SET card_type = 'ROOKIE' WHERE rookie_card = 1;

ALTER TABLE card DROP INDEX idx_card_attributes;
ALTER TABLE card ADD KEY idx_card_attributes (card_type, game_used_material, autograph);

ALTER TABLE card DROP COLUMN rookie_card;

-- 1. Add direct indexes for new flat lookup columns on card table
CREATE INDEX idx_card_manufacturer_id ON card(manufacturer_id);
CREATE INDEX idx_card_brand_id ON card(brand_id);

-- 2. Add composite index for faceted filter dropdown queries (Manufacturer -> Brand -> Theme)
CREATE INDEX idx_card_mfg_brand_theme ON card(manufacturer_id, brand_id, theme_id);

-- 3. Add composite index for common card attribute boolean filtering
CREATE INDEX idx_card_attributes ON card(rookie_card, game_used_material, autograph);

-- 4. Add composite index for card_player junction table to speed up player and team joins
CREATE INDEX idx_card_player_card_player ON card_player(card_id, player_id);
CREATE INDEX idx_card_player_card_team ON card_player(card_id, team_id);

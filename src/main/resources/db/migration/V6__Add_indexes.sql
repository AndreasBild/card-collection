-- Indexes for performance optimization
CREATE INDEX idx_card_player_id ON card(player_id);
CREATE INDEX idx_card_team_id ON card(team_id);
CREATE INDEX idx_card_season_id ON card(season_id);
CREATE INDEX idx_card_variant_id ON card(variant_id);
CREATE INDEX idx_card_theme_id ON card(theme_id);
CREATE INDEX idx_card_grading_id ON card(grading_id);

CREATE INDEX idx_player_surname_name ON player(surname, name);

CREATE INDEX idx_card_theme_brand_id ON card_theme(brand_id);
CREATE INDEX idx_card_brand_manufacturer_id ON card_brand(manufacturer_id);
CREATE INDEX idx_card_print_run ON card(print_run);

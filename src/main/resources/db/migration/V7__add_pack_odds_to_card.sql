ALTER TABLE `card`
    ADD COLUMN `pack_odds` VARCHAR(50) NULL AFTER `print_run`,
    ADD KEY `idx_card_pack_odds` (`pack_odds`);

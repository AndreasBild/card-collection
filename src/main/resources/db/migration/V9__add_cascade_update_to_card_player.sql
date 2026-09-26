-- Migration V9: Enable ON UPDATE CASCADE on card_player.card_id foreign key
-- Allows updating a card's ID with automatic synchronization to all associated card_player bridge rows.

ALTER TABLE `card_player`
    DROP FOREIGN KEY `card_player_ibfk_1`;

ALTER TABLE `card_player`
    ADD CONSTRAINT `card_player_ibfk_1`
    FOREIGN KEY (`card_id`) REFERENCES `card` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

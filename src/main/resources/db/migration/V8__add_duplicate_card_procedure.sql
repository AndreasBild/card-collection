-- Migration V8: Add Stored Procedure to safely duplicate a card along with its card_player records
-- Resets grading_id, grading_cert_number to NULL and serial_number to 0 to prevent slab and serial collisions.

DROP PROCEDURE IF EXISTS `duplicate_card`;

DELIMITER //

CREATE PROCEDURE `duplicate_card`(
    IN `source_card_id` BIGINT
)
BEGIN
    DECLARE `source_exists` INT DEFAULT 0;
    DECLARE `new_card_id` BIGINT;

    -- Verify source card exists
    SELECT COUNT(*) INTO `source_exists` FROM `card` WHERE `id` = `source_card_id`;

    IF `source_exists` = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Source card does not exist';
    END IF;

    -- 1. Insert duplicated card
    INSERT INTO `card` (
        `print_run`,
        `pack_odds`,
        `serial_number`,
        `number`,
        `theme_id`,
        `season_id`,
        `variant_id`,
        `rookie_card`,
        `game_used_material`,
        `autograph`,
        `grading_id`,
        `grading_cert_number`,
        `manufacturer_id`,
        `brand_id`
    )
    SELECT
        `print_run`,
        `pack_odds`,
        0,
        `number`,
        `theme_id`,
        `season_id`,
        `variant_id`,
        `rookie_card`,
        `game_used_material`,
        `autograph`,
        NULL,
        NULL,
        `manufacturer_id`,
        `brand_id`
    FROM `card`
    WHERE `id` = `source_card_id`;

    SET `new_card_id` = LAST_INSERT_ID();

    -- 2. Duplicate card_player associations for the new card ID
    INSERT INTO `card_player` (`card_id`, `player_id`, `team_id`)
    SELECT `new_card_id`, `player_id`, `team_id`
    FROM `card_player`
    WHERE `card_id` = `source_card_id`;

    -- Return the newly created card record and its associated players
    SELECT 
        c.id AS created_card_id,
        c.number,
        c.serial_number,
        c.print_run,
        cp.player_id,
        cp.team_id
    FROM `card` c
    LEFT JOIN `card_player` cp ON cp.card_id = c.id
    WHERE c.id = `new_card_id`;
END //

DELIMITER ;

ALTER TABLE `card`
    ADD COLUMN `grading_cert_number` VARCHAR(64) NULL AFTER `grading_id`,
    ADD KEY `idx_card_grading_cert_number` (`grading_cert_number`);

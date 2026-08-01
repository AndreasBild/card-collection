ALTER TABLE `card` MODIFY COLUMN `print_run` int DEFAULT NULL;

UPDATE `card` SET `print_run` = NULL WHERE `print_run` = 0;

-- Add theme_id to variant table to establish a one-to-many relationship
-- One theme can have many variants

ALTER TABLE `variant`
ADD COLUMN `theme_id` BIGINT NULL DEFAULT NULL,
ADD CONSTRAINT `fk_variant_card_theme`
  FOREIGN KEY (`theme_id`)
  REFERENCES `card_theme` (`id`)
  ON DELETE SET NULL
  ON UPDATE CASCADE;

-- Migration V10: Add secondary performance indexes to card table
-- Covers serial_number sorting and cascading filter lookups for brand+theme and theme+variant

ALTER TABLE `card`
    ADD KEY `idx_card_serial_number` (`serial_number`),
    ADD KEY `idx_card_brand_theme` (`brand_id`, `theme_id`),
    ADD KEY `idx_card_theme_variant` (`theme_id`, `variant_id`);

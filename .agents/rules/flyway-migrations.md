# Flyway Migrations & MySQL Schema Standards

## Schema Invariants & Best Practices

1. **Migration Immutability:**
   - Migration scripts in `src/main/resources/db/migration/` are strictly immutable once merged.
   - Never modify an existing migration file. Any alterations, backfills, or constraint updates must be added as a new migration script.

2. **Naming Convention:**
   - Incremental changes must follow `V<version>__<descriptive_name>.sql` or `V<timestamp>__<descriptive_name>.sql` (e.g. `V20260823120000__add_card_provenance_index.sql`).

3. **Normalization (3NF) & Foreign Keys:**
   - Core domain entities (`sport`, `card_manufacturer`, `card_brand`, `card_theme`, `variant`, `season`, `grading`, `team`, `player`) must remain cleanly normalized.
   - Bridge tables (such as `card_player`) must declare composite primary keys `(card_id, player_id)`.
   - Explicit FK actions:
     - `card_player -> card`: `ON DELETE CASCADE`
     - `card_player -> player`: `ON DELETE CASCADE`
     - `card_player -> team`: `ON DELETE SET NULL`
     - `card -> grading`: `ON DELETE SET NULL`
     - `player -> sport`: `ON DELETE RESTRICT`

4. **Charset & Indexing Topology:**
   - All tables and text columns must use `DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci`.
   - Zero unindexed foreign keys: Every FK column must be covered by a primary, unique, or secondary index.
   - Use composite indexes matching filter combinations (`(manufacturer_id, brand_id, theme_id, variant_id)`).

5. **Dump File Synchronization:**
   - Whenever schema migrations are added, verify and synchronize the baseline dump in `src/main/resources/sql/dump/Dump.sql`.

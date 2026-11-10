---
name: verify-schema-and-migrations
description: Validate Flyway migration scripts, database dump consistency, 3NF schema, FK topology, and JPA entity definitions.
---

# Verify Schema & Migrations Skill

Use this skill when modifying or adding database schema migrations (`src/main/resources/db/migration/`), updating JPA entity annotations, or synchronizing the baseline dump file.

## 1. Schema Invariants & Best Practices

1. **Migration Immutability & Naming:**
   - Migration scripts in `src/main/resources/db/migration/` are strictly immutable once merged. Never modify or delete an existing migration file.
   - Incremental changes must follow `V<version>__<descriptive_name>.sql` or `V<timestamp>__<descriptive_name>.sql` (e.g. `V20260823120000__add_card_provenance_index.sql`).
2. **Normalization (3NF) & Foreign Key Topology:**
   - Core domain entities (`sport`, `card_manufacturer`, `card_brand`, `card_theme`, `variant`, `season`, `grading`, `team`, `player`) must remain cleanly normalized.
   - Bridge tables (such as `card_player`) must declare composite primary keys `(card_id, player_id)`.
   - Explicit FK actions:
     - `card_player -> card`: `ON DELETE CASCADE`
     - `card_player -> player`: `ON DELETE CASCADE`
     - `card_player -> team`: `ON DELETE SET NULL`
     - `card -> grading`: `ON DELETE SET NULL`
     - `player -> sport`: `ON DELETE RESTRICT`
3. **Charset, Indexing & Constraints:**
   - All tables and text columns must use `DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci`.
   - Zero unindexed foreign keys: Every FK column must be covered by a primary, unique, or secondary index.
   - Use composite indexes matching filter combinations (`(manufacturer_id, brand_id, theme_id, variant_id)`) and functional indexes where lookups perform string manipulation (e.g. `idx_player_full_name` on `(concat(surname, ' ', name))`).
   - Enforce domain rules via `CHECK` constraints (e.g. `check_grade_range` between `6.0` and `10.0`) and `UNIQUE` keys on natural entity names (`UK_sport_name`, `UK_season_name`, etc.).

## 2. Verification Checklist

1. **Verify JPA Entity & Repository Consistency:**
   ```bash
   ./mvnw test -Dtest=*RepositoryTest,*EntityTest
   ```
2. **Synchronize Baseline Dump File:**
   - Whenever schema migrations are added, verify and synchronize the baseline dump in `src/main/resources/sql/dump/Dump.sql` (inspect using bounded `read_file` slices, never full-file reads).

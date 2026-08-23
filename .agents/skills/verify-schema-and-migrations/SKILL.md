---
name: verify-schema-and-migrations
description: Validate Flyway migration scripts, database dump consistency, 3NF schema, and JPA entity definitions.
---

# Verify Schema & Migrations Skill

Use this skill when modifying or adding database schema migrations (`src/main/resources/db/migration/`), updating JPA entity annotations, or synchronizing the baseline dump file.

## Verification Checklist

1. **Verify Flyway Script Naming & Immutability:**
   - Ensure the new migration file follows `V<version>__<description>.sql` or `V<timestamp>__<description>.sql`.
   - Confirm no previously committed migrations were edited or deleted.

2. **Schema Invariants Check:**
   - Confirm collation `DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci`.
   - Confirm bridge tables have composite primary keys (e.g. `PRIMARY KEY (card_id, player_id)` on `card_player`).
   - Confirm all foreign keys have supporting secondary indexes.

3. **Verify JPA Entity Consistency:**
   - Run the integration and repository test suite:
   ```bash
   ./mvnw test -Dtest=*RepositoryTest,*EntityTest
   ```

4. **Verify Baseline Dump File:**
   - Ensure `src/main/resources/sql/dump/Dump.sql` reflects the latest schema changes.

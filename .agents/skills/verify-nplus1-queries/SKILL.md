---
name: verify-nplus1-queries
description: Validate JPA queries, Specifications, fetch joins, and batch sizing to prevent N+1 query regressions.
---

# Verify N+1 Queries Skill

Use this skill when introducing or modifying repository queries, card filters, pagination, or dynamic `Specification<Card>` predicates.

## Verification Protocol

1. **Count Query Guard Check:**
   - Verify that dynamic `Specification<Card>` checks `query.resultType` before applying `root.fetch(...)`.

2. **Run Repository & Filter Tests:**
   ```bash
   ./mvnw test -Dtest=CardRepositoryTest,CardSpecificationTest,FilterDataControllerTest
   ```

3. **Check Hibernate SQL Logs:**
   - Verify that querying cards with relationships (`manufacturer`, `brand`, `theme`, `variant`, `grading`, `cardPlayers`) uses `LEFT JOIN FETCH` or batch loading rather than individual secondary SELECT statements per entity.

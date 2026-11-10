---
name: verify-nplus1-queries
description: Validate JPA queries, Specifications, count-query fetch guards, and batch sizing to prevent N+1 query regressions.
---

# Verify N+1 Queries Skill

Use this skill when introducing or modifying repository queries, card filters, pagination, or dynamic `Specification<Card>` predicates.

## 1. Data Fetching & Specification Invariants

1. **Explicit Fetch Joins in Repositories:**
   - Provide dedicated repository methods using `LEFT JOIN FETCH` (such as `findAllWithDetails()`) when details and relations are required for rendering or serialization.
2. **Count Query Guarding in Dynamic Specifications:**
   - In dynamic `Specification<Card>`, eager fetch joins MUST NOT be executed during pagination count queries.
   - Guard root fetch operations with:
     ```kotlin
     val resultType = query.resultType
     if (resultType != Long::class.java && resultType != Long::class.javaObjectType && resultType.simpleName != "Long") {
         root.fetch<Card, CardManufacturer>("manufacturer", JoinType.LEFT)
         root.fetch<Card, CardBrand>("brand", JoinType.LEFT)
         // ...
     }
     ```
3. **Batch Sizing on Collections:**
   - Annotate `@OneToMany` relationships (such as `cardPlayers` in `Card`) with Hibernate `@BatchSize(size = 20)` to eliminate N+1 queries if lazy associations are traversed.
4. **Transactional Read Boundary:**
   - Never trigger lazy loading outside transactional boundaries. Always annotate read operations with `@Transactional(readOnly = true)`.

## 2. Verification Protocol

1. **Run Repository, Specification & Filter Tests:**
   ```bash
   ./mvnw test -Dtest=CardRepositoryTest,CardSpecificationTest,FilterDataControllerTest
   ```
2. **Check Hibernate SQL Logs:**
   - Verify that querying cards with relationships (`manufacturer`, `brand`, `theme`, `variant`, `grading`, `cardPlayers`) uses `LEFT JOIN FETCH` or batch loading rather than secondary per-entity `SELECT` statements.

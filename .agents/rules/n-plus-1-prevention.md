# JPA Query Optimization & N+1 Prevention Standards

## Invariants for Data Fetching & Specifications

1. **Explicit Fetch Joins in Repositories:**
   - Always provide dedicated methods using `LEFT JOIN FETCH` (such as `findAllWithDetails()`) when details and relations are required for rendering or serialization.

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

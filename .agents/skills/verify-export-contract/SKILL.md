---
name: verify-export-contract
description: Validate CardJsonDto serialization, deterministic slug generation, and export compatibility with card-collectionJava.
---

# Verify Export Contract Skill

Use this skill when modifying export DTOs (`CardJsonDto`), JSON serialization logic, slug generation (`toSlug()`), or `DatabaseChangeDetectorService`.

## Verification Steps

1. **Run Export DTO & Service Tests:**
   ```bash
   ./mvnw test -Dtest=*Export*Test,*Slug*Test
   ```

2. **Verify JSON Output Invariants:**
   - Confirm all required fields (`id`, `player`, `season`, `team`, `company`, `brand`, `theme`, `variant`, `cardNumber`, `serialNumber`, `printRun`, `gradingCompany`, `grade`, `isAutograph`, `isPatch`, `isRookie`, `collection`, `notes`) serialize accurately.
   - Confirm null properties serialize as `null` or omitted depending on configuration.

3. **Verify Slug Formula & Collisions:**
   - Slug formula: `[season]-[brand]-[theme]-[variant]-[number]-sn[serialNumber]`.
   - Ensure default theme (`Base Set`) and default variant (`Base`) are stripped.
   - Ensure collision resolution appends `-card<id>` deterministically.

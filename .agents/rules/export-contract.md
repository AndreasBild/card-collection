# Export Interface & card-collectionJava Contract

## SSOT & Export Pipeline Invariants

1. **Single Source of Truth (SSOT):**
   - This application (`card-collection`) is the master transactional database.
   - The static site generator (`card-collectionJava`) consumes the exported `cards.json` schema.

2. **DTO Contract Compatibility (`CardJsonDto`):**
   - Properties in `CardJsonDto` (`id`, `player`, `season`, `team`, `company`, `brand`, `theme`, `variant`, `cardNumber`, `serialNumber`, `printRun`, `gradingCompany`, `grade`, `isAutograph`, `isPatch`, `isRookie`, `collection`, `notes`) must remain stable.
   - Any new property must be backward-compatible (nullable default).

3. **Deterministic Slug Generation (`toSlug()`):**
   - Normalization: Unicode NFD normalization, ASCII transliteration, lowercasing, non-alphanumeric replacement.
   - Slug formula: `[season]-[brand]-[theme]-[variant]-[number]-sn[serialNumber]`.
   - Omit default themes (`Base Set`) and default variants (`Base`) from slugs to maintain canonical URLs.
   - Collision resolution: append `-card<id>` on first collision, and `-card<id>-<index>` for subsequent collisions.

4. **Change Detection & Cache Eviction:**
   - `DatabaseChangeDetectorService` tracks entity metric signatures (14 metrics).
   - When external or internal changes are detected, Caffeine caches are evicted and `cards.json` is auto-synced to `export.json.sync-path`.

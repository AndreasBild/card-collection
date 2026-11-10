---
name: verify-export-contract
description: Validate CardJsonDto serialization, deterministic slug generation, change detection signatures, and export compatibility with card-collectionJava.
---

# Verify Export Contract Skill

Use this skill when modifying export DTOs (`CardJsonDto`), JSON/CSV/HTML serialization, slug generation (`toSlug()`), `ExportRateLimiter`, or `DatabaseChangeDetectorService`.

## 1. SSOT & Export Pipeline Invariants

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
   - `DatabaseChangeDetectorService` tracks a content signature over the 11 exported tables (row count + CRC32 checksum of all exported columns per table, 22 values), defined in `SIGNATURE_TABLES`.
   - When external or internal changes are detected, Caffeine caches are evicted and `cards.json` is auto-synced to `export.json.sync-path`.
5. **CSV & HTML Export Standards:**
   - **RFC 4180 CSV Conformance:** Enclose fields containing commas, quotes, or newlines in double quotes with internal quotes escaped as `""`.
   - **Virtual Threads for Parallelism:** Use Java Virtual Threads (`Executors.newVirtualThreadPerTaskExecutor()`) for CPU/IO-bound zip batch exports (`/export/html`).
   - **XSS Sanitization:** Escape all user/entity strings embedded in HTML exports via `HtmlUtils.htmlEscape()`.
   - **Rate Limiting:** Protect heavy export endpoints using `ExportRateLimiter` (IP rate limiting + concurrency Semaphore).

## 2. Verification Steps

1. **Run Export, Slug & Change Detector Tests:**
   ```bash
   ./mvnw test -Dtest=*Export*Test,*Slug*Test,DatabaseChangeDetectorServiceTest,DatabaseChangeEventListenerTest
   ```
2. **Verify JSON & Slug Output:**
   - Confirm all 18 `CardJsonDto` fields serialize accurately and nullable additions remain backward-compatible.
   - Confirm default `Base Set` / `Base` stripping and deterministic `-card<id>` collision resolution.

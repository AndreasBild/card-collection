## 📌 Pull Request Overview

<!-- Provide a concise description of the changes made and the motivation behind them. -->

### 🎯 Type of Change
- [ ] 🚀 New Feature
- [ ] 🐛 Bugfix
- [ ] 🗄️ Database Migration (Flyway)
- [ ] 📤 Export Logic / DTO Contract Change (Affects `card-collectionJava`)
- [ ] ⚡ Performance Optimization / Caching / Virtual Threads
- [ ] 🧹 Refactoring / Code Cleanup
- [ ] 📖 Documentation Update

---

## 🔒 Branch & Safety Check
- [ ] **Branch Target**: Base is `main` (Protected Branch) and changes originate from a dedicated branch (`feature/...`, `fix/...`, `chore/...`). Direct push to `main` is forbidden.
- [ ] **No Secret Leaks**: No credentials, secrets, or local environment files are committed.

---

## 🗄️ Database & Schema Integrity (if applicable)
- [ ] **Flyway Migration**:
  - Script follows immutable naming convention: `V<timestamp>__<description>.sql` or `V<version>__<description>.sql`.
  - Schema is fully normalized (3NF / explicit composite PK on bridge tables).
  - Explicit foreign keys, check constraints, and optimal indexes added (`utf8mb4_0900_ai_ci`).
  - Baseline dump (`src/main/resources/sql/dump/Dump.sql`) updated or verified if schema changed.
- [ ] **Query Performance**:
  - Verified no N+1 query regressions (uses `LEFT JOIN FETCH` or `@BatchSize`).
  - Dynamic filters adhere to `Specification` & indexing paths.

---

## 📤 Export Interface Contract (card-collectionJava)
- [ ] **DTO Contract Compatibility**: `CardJsonDto` field mappings and JSON serialization remain backward-compatible or versioned.
- [ ] **Slug Stability**: Unique slug generation rules preserved (`toSlug` normalization and collision resolution).
- [ ] **CSV / HTML Integrity**: RFC 4180 escaping maintained; Virtual Thread batch rendering verified.

---

## ✅ Quality Gate & Testing
- [ ] Local build and tests pass: `./mvnw clean test` (or `./mvnw clean verify`).
- [ ] Unit & Integration tests added/updated for new business logic.
- [ ] Virtual Thread concurrency and Caffeine cache TTL verified.
- [ ] Static analysis / Linter / Qodana passed without critical warnings.

---

## 📸 Screenshots / Verification Evidence (Optional)
<!-- Add console output or query execution plans if applicable -->

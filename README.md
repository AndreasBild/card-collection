# 🃏 Basketball Trading Card Collection Engine

A high-performance **Spring Boot 4 / Kotlin 2 / MySQL** domain engine and inventory system for sports trading cards (specializing in the Juwan Howard Basketball Collection). This repository serves as the **Single Source of Truth (SSOT)** and export engine for downstream static catalog generators (such as [`card-collectionJava`](https://github.com/AndreasBild/card-collectionJava)).

---

## 🏗️ Architecture & Governance

* **[ARCHITECTURE.md](ARCHITECTURE.md):** Complete architectural blueprint covering normalized 3NF schema, index topology, multi-tier Caffeine caching, automated DB change detection engine, and export contracts.
* **[AGENTS.md](AGENTS.md):** AI agent instructions, database standards, count-safe JPA specifications, N+1 prevention, and the 6-stage development lifecycle for IntelliJ IDEA, Jules, and Antigravity.
* **[Pull Request Template](.github/pull_request_template.md):** Standardized PR checklist ensuring branch protection, Flyway immutability, and contract stability.

---

## 🚀 Quickstart & Local Setup

### 1. Requirements
* **Java 27** SDK
* **MySQL 8.x / 9.x / 26.x** (Ver 26.10+) running locally on port `3306` (or use `docker compose up -d`)

### 2. Database Initialization (Flyway)
The application automatically executes all versioned migrations (`src/main/resources/db/migration`) on startup.

1. Start MySQL locally or with Docker Compose:
   ```bash
   docker compose up -d
   ```
   *(Or create `CREATE DATABASE card_collection;` manually).*
2. Configure local credentials in `src/main/resources/application.properties` (or via environment variables).
3. Connect instantly with **Sequel Ace**:
   ```bash
   open card_collection.spf
   ```

### 3. Running the Application
```bash
./mvnw spring-boot:run
```
Access the application web UI at `http://localhost:8080/cards`.

> **IntelliJ IDEA Tip:** The pre-configured run action (`CardCollectionApplication` and `All Tests`) automatically verifies if MySQL is listening on port `3306` (via `scripts/ensure-mysql-running.sh`) and starts the service on-demand before launch.

---

## 📤 Export Pipelines & Static Site Sync

The backend serves as the Master Database and provides high-performance export pipelines:

| Endpoint | Content-Type | Format / Purpose |
| :--- | :--- | :--- |
| `GET /export/json` | `application/json` | Browser download of `cards.json` + **automatic background sync** to `../card-collectionJava/content/json/cards.json`. |
| `GET/POST /export/json/sync` | `application/json` | Programmatic trigger to sync database records directly to the static site repository without triggering a browser download. |
| `GET /export/csv` | `text/csv` | RFC 4180 compliant CSV export for spreadsheet analysis. |
| `GET /export/html` | `application/zip` | Season-partitioned HTML collection archives generated via Java 27 Virtual Threads. |

### How to trigger the Static Site Sync:

1. **Via Web UI or Browser:**
   * Open `http://localhost:8080/export/json` (downloads `cards.json` and updates the static site generator simultaneously).
   * Open `http://localhost:8080/export/json/sync` (updates the static site file and returns a JSON confirmation).
2. **Via Terminal / Build Pipeline:**
   ```bash
   curl -X POST http://localhost:8080/export/json/sync
   ```
3. **Automatic External DB Change Detection & Live Backups:**
   * When modifying records in Sequel Ace, DataGrip, or MySQL CLI, the backend automatically detects changes within ~30 seconds (default, configurable), evicts stale Caffeine caches, writes the updated `cards.json` to the static site project, and **automatically syncs `src/main/resources/sql/dump/Dump.sql` while creating a timestamped rolling backup in `backups/`**.
   ```properties
   export.db-sync.enabled=true
   export.db-sync.polling-interval-ms=30000
   export.json.sync-path=${EXPORT_JSON_SYNC_PATH:../card-collectionJava/content/json/cards.json}
   export.dump.enabled=true
   export.dump.max-backups=30
   ```
4. **Manual One-Click Dump Sync:**
   * Run `python3 scripts/sync_db_dump.py` from CLI, or use the pre-configured **Sync Database Dump** run action in IntelliJ IDEA.

---

## 🧪 Testing & Quality Gates

Run the comprehensive unit and integration test suite:
```bash
./mvnw clean verify
```
* Continuous Integration runs automatically via GitHub Actions on all pushes and PRs against `main`.

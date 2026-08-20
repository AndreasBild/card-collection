# 🃏 Basketball Trading Card Collection Engine

A high-performance **Spring Boot 4 / Kotlin 2 / MySQL** domain engine and inventory system for sports trading cards (specializing in the Juwan Howard Basketball Collection). This repository serves as the **Single Source of Truth (SSOT)** and export engine for downstream static catalog generators (such as [`card-collectionJava`](https://github.com/AndreasBild/card-collectionJava)).

---

## 🏗️ Architecture & Governance

* **[ARCHITECTURE.md](ARCHITECTURE.md):** Complete architectural blueprint covering normalized 3NF schema, index topology, Caffeine caching tiers, and export contracts.
* **[AGENTS.md](AGENTS.md):** AI agent instructions, database standards, N+1 prevention, and the 6-stage development lifecycle for IntelliJ IDEA, Jules, and Antigravity.
* **[Pull Request Template](.github/pull_request_template.md):** Standardized PR checklist ensuring branch protection, Flyway immutability, and contract stability.

---

## 🚀 Quickstart & Local Setup

### 1. Requirements
* **Java 26** SDK
* **MySQL 8.x / 9.x** running locally on port `3306`

### 2. Database Initialization (Flyway)
The application automatically executes all versioned migrations (`src/main/resources/db/migration`) on startup.

1. Create an empty database in MySQL:
   ```sql
   CREATE DATABASE card_collection;
   ```
2. Configure local credentials in `src/main/resources/application.properties` (or via environment variables).

### 3. Running the Application
```bash
./mvnw spring-boot:run
```
Access the application web UI at `http://localhost:8080/cards`.

---

## 📤 Export Pipelines & Integration

The backend provides optimized export endpoints protected by `ExportRateLimiter`:

| Endpoint | Content-Type | Format / Purpose |
| :--- | :--- | :--- |
| `GET /export/json` | `application/json` | Syndication payload (`cards.json`) for `card-collectionJava` SSG. Automatically syncs file to `../card-collectionJava/content/json/cards.json`. |
| `GET/POST /export/json/sync` | `application/json` | Explicit trigger to sync database export directly to `../card-collectionJava/content/json/cards.json`. |
| `GET /export/csv` | `text/csv` | RFC 4180 compliant CSV export for spreadsheet analysis. |
| `GET /export/html` | `application/zip` | Season-partitioned HTML collection archives generated via Java 26 Virtual Threads. |

---

## 🧪 Testing & Quality Gates

Run the comprehensive unit and integration test suite:
```bash
./mvnw clean verify
```
* Continuous Integration runs automatically via GitHub Actions on all pushes and PRs against `main`.

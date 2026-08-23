# Architectural Blueprint: Card Collection Engine

## 1. System Overview & Context

`card-collection` is a high-performance, domain-driven Spring Boot application written in Kotlin (running on Java 26) backed by a relational MySQL database. It serves as the **Single Source of Truth (SSOT)** for sports trading card inventory, advanced relational querying, and data syndication.

The application feeds structured data to the downstream static site generator **`card-collectionJava`**, which generates static collection catalogs, SEO-optimized landing pages, and interactive showcase portals.

```mermaid
graph TD
    subgraph Storage & Migration Layer
        FW[Flyway Migrations] -->|DDL & Baseline Data| DB[(MySQL 8.x/9.x Database)]
        DUMP[Dump.sql Baseline] -.->|Sync Reference| DB
    end

    subgraph Core Spring Boot Application
        DB <-->|Hibernate 7 / Spring Data JPA| REPO[Repository & JPA Layer]
        REPO <-->|Dynamic Specifications & Left Join Fetch| SRV[CardService & CardExportService]
        CACHE[(Caffeine Multi-Tier Cache)] <-->|TTLs 30m - 24h| SRV
        DETECTOR[DatabaseChangeDetectorService] -->|3s Polling Signature| DB
        DETECTOR -->|Publish DatabaseChangedEvent| BUS[Spring ApplicationEventPublisher]
        BUS -->|DatabaseChangedEvent| LISTENER[DatabaseChangeEventListener]
        LISTENER -->|Evict All Caches| CACHE
        LISTENER -->|Auto-Sync on Mutation| EXP[CardExportService]
        SRV --> CTRL[CardController / FilterDataController / ExportController]
        LIMITER[ExportRateLimiter & SecurityHeadersFilter] --> CTRL
        HEALTH[DatabaseSyncHealthIndicator & CardCollectionInfoContributor] --> ACT[Actuator /health & /info]
    end

    subgraph Export & Syndication Layer
        CTRL -->|JSON API /export/json & /export/json/sync| SSG["Static Site Generator (card-collectionJava)"]
        EXP -->|Direct JSON File Sync| SSG
        CTRL -->|CSV Export /export/csv| CSV[Spreadsheets & External Ingestion]
        CTRL -->|Virtual Thread ZIP /export/html| HTML[Static HTML Season Archives]
    end
```

---

## 2. Database Topology & Entity Schema

The database schema is fully normalized (Third Normal Form) with explicit domain constraints, index coverage for all foreign keys, and dedicated functional indexes for complex string matching.

### 2.1 Entity Relationship Diagram

```mermaid
erDiagram
    SPORT ||--o{ PLAYER : "categorizes"
    PLAYER ||--o{ CARD_PLAYER : "features in"
    TEAM ||--o{ CARD_PLAYER : "represents (optional)"
    CARD ||--o{ CARD_PLAYER : "contains"
    
    CARD_MANUFACTURER ||--o{ CARD : "manufactures"
    CARD_BRAND ||--o{ CARD : "brands"
    CARD_THEME ||--o{ CARD : "themes"
    VARIANT ||--o{ CARD : "sub-types"
    SEASON ||--o{ CARD : "released in"
    GRADING ||--o| CARD : "grades (1:1 optional)"

    SPORT {
        bigint id PK
        varchar(100) name UK
    }

    PLAYER {
        bigint id PK
        varchar(255) name
        varchar(255) surname
        bigint sport_id FK
    }

    TEAM {
        bigint id PK
        varchar(100) name UK
    }

    CARD_MANUFACTURER {
        bigint id PK
        varchar(255) name UK
    }

    CARD_BRAND {
        bigint id PK
        varchar(255) name UK
    }

    CARD_THEME {
        bigint id PK
        varchar(255) name UK
    }

    VARIANT {
        bigint id PK
        varchar(255) name UK
    }

    SEASON {
        bigint id PK
        varchar(255) name UK
    }

    GRADING {
        bigint id PK
        float grade "CHECK 6.0 - 10.0"
        varchar(255) grading_company
    }

    CARD {
        bigint id PK
        int print_run "NULL or >0"
        int serial_number
        varchar(255) number
        bit rookie_card
        bit game_used_material
        bit autograph
        bigint manufacturer_id FK
        bigint brand_id FK
        bigint theme_id FK
        bigint variant_id FK
        bigint season_id FK
        bigint grading_id FK "Nullable"
    }

    CARD_PLAYER {
        bigint card_id PK,FK "CASCADE"
        bigint player_id PK,FK "CASCADE"
        bigint team_id FK "SET NULL"
    }
```

### 2.2 Relational Integrity & Cascading Semantics

| Relationship | Cardinality | FK Constraint Action | Rationale |
| :--- | :--- | :--- | :--- |
| `card_player` $\rightarrow$ `card` | $N:1$ | `ON DELETE CASCADE` | Removing a card cascades to its player associations. |
| `card_player` $\rightarrow$ `player` | $N:1$ | `ON DELETE CASCADE` | Removing a player removes their links in card collections. |
| `card_player` $\rightarrow$ `team` | $N:1$ | `ON DELETE SET NULL` | Card link remains intact if a team entry is cleared. |
| `card` $\rightarrow$ `grading` | $1:1$ | `ON DELETE SET NULL` | Deleting a grading record keeps the physical card record. |
| `player` $\rightarrow$ `sport` | $N:1$ | `ON DELETE RESTRICT` | Prevents accidental deletion of a sport category in use. |
| `card` $\rightarrow$ `manufacturer/brand/theme/variant/season` | $N:1$ | `ON DELETE RESTRICT` | Preserves referential integrity for master domain lookups. |

### 2.3 Index Topology

```sql
-- Card Search & Dynamic Filter Composite Indexes
KEY `idx_card_mfg_brand_theme_variant` (`manufacturer_id`, `brand_id`, `theme_id`, `variant_id`)
KEY `idx_card_mfg_brand_theme` (`manufacturer_id`, `brand_id`, `theme_id`)
KEY `idx_card_attributes` (`rookie_card`, `game_used_material`, `autograph`)
KEY `idx_card_print_run` (`print_run`)
KEY `idx_card_number` (`number`)

-- Bridge & Functional Player Lookups
KEY `idx_player_full_name` ((concat(`surname`, ' ', `name`)))
KEY `idx_player_surname_name` (`surname`, `name`)
KEY `idx_card_player_player_card` (`player_id`, `card_id`)
KEY `idx_card_player_team_card` (`team_id`, `card_id`)
```

---

## 3. Data Processing & Export Pipelines

### 3.1 Synchronous Ingestion & Filter Flow

```mermaid
sequenceDiagram
    autonumber
    actor Client as Web Browser / API Consumer
    participant Ctrl as CardController / FilterDataController
    participant Cache as Caffeine CacheManager
    participant Srv as CardService
    participant Repo as CardRepository (JPA Specification)
    participant DB as MySQL 8.x/9.x

    Client->>Ctrl: GET /cards?manufacturerId=1&brandId=2&page=0
    Ctrl->>Srv: getCardsFiltered(filter, pageable)
    Srv->>Cache: Check "filteredCards" CacheKey(filter, pageable)
    alt Cache Hit
        Cache-->>Srv: Return Cached Page<Card>
    else Cache Miss
        Srv->>Repo: findAll(Specification<Card>, Pageable)
        Repo->>DB: SELECT DISTINCT c FROM Card c LEFT JOIN FETCH ...
        DB-->>Repo: Result Set
        Repo-->>Srv: Page<Card> (Eagerly Loaded Entities)
        Srv->>Cache: Store Result in Caffeine (30m TTL)
    end
    Srv-->>Ctrl: Page<Card>
    Ctrl-->>Client: Rendered View / HTML Payload
```

### 3.2 Automated Database Change Detection & Sync Engine

When external tools (Sequel Ace, DataGrip, MySQL Workbench, or direct SQL scripts) insert, update, or delete records in the MySQL database, `DatabaseChangeDetectorService` automatically senses state divergence without requiring manual cache purging or server restarts:

```mermaid
sequenceDiagram
    autonumber
    actor Admin as External DBA / Sequel Ace / SQL Script
    participant DB as MySQL Database
    participant Det as DatabaseChangeDetectorService
    participant Bus as Spring ApplicationEventPublisher
    participant Lis as DatabaseChangeEventListener
    participant Cache as Caffeine Caches
    participant Exp as CardExportService
    participant SSG as Static Site Generator (card-collectionJava)

    Admin->>DB: Direct INSERT / UPDATE / DELETE
    loop Every 3000ms
        Det->>DB: Execute 14-Point Aggregate Signature Query
        DB-->>Det: Return Aggregated Signature Hash
        alt Signature Mismatch
            Det->>Bus: publishEvent(DatabaseChangedEvent)
            Bus->>Lis: onDatabaseChanged(event)
            Lis->>Cache: evictAllCaches() (Purge all Caffeine tiers)
            Lis->>Exp: syncCardsJsonToStaticSite()
            Exp->>SSG: Stream & Write cards.json to sync-path
            Det->>Det: Update lastStateSignature & lastSyncTimestamp
        else Signature Matches
            Det->>Det: No-op
        end
    end
```

The change signature query aggregates row counts and key identifier checksums across 14 tables and relationships (`card`, `card_player`, `player`, `grading`, `team`, `season`, `card_brand`, `card_theme`, `variant`, `card_manufacturer`, `sport`).

---

## 4. Export Interface Specification (SSG: card-collectionJava)

The static site generator `card-collectionJava` consumes the exported `cards.json` schema. Export and synchronization operations can be invoked on demand:

| Endpoint | Method | Action | Target / Response |
| :--- | :--- | :--- | :--- |
| `/export/json` | `GET` | Generates & streams `cards.json` as attachment and syncs to `sync-path` | `application/json` download + File sync |
| `/export/json/sync` | `GET`, `POST` | Triggers background file sync to `sync-path` without downloading | `application/json` (`SyncStatusResponse` contract) |
| `/export/csv` | `GET` | RFC 4180 compliant CSV export with quoted fields and double-quote escapes | `text/csv;charset=UTF-8` download |
| `/export/html` | `GET` | Virtual-thread parallel season archive generation | `application/zip` download |

### 4.1 JSON Contract (`/export/json` $\rightarrow$ `cards.json`)

```json
[
  {
    "id": "1994-95-finest-refractors-230",
    "player": "Juwan Howard",
    "season": "1994-95",
    "team": "Washington Bullets",
    "company": "Topps",
    "brand": "Finest",
    "theme": "Base Set",
    "variant": "Refractors",
    "cardNumber": "230",
    "serialNumber": null,
    "printRun": null,
    "gradingCompany": "PSA",
    "grade": "10",
    "isAutograph": false,
    "isPatch": false,
    "isRookie": true,
    "collection": "Juwan Howard",
    "notes": null
  }
]
```

### 4.2 Deterministic Slug Formula

The unique identifier (`id`) in `CardJsonDto` is generated deterministically via `CardExportService`:

1. **Extraction:** Season $\rightarrow$ Brand $\rightarrow$ Theme (if $\neq$ "Base Set") $\rightarrow$ Variant (if $\neq$ "Base") $\rightarrow$ Number $\rightarrow$ Serial (prefix `sn` if $\neq 0$).
2. **Unicode Normalization:** Strip diacritics via `Normalizer.normalize(input, Normalizer.Form.NFD)`.
3. **Kebab-Case Sanitization:** Lowercase, replace all non-alphanumerics with `-`, collapse consecutive dashes.
4. **Deterministic Collision Handling:**
   - First collision: appends `-card<id>`.
   - Subsequent collisions for the same base slug: appends `-card<id>-<index>` (e.g., `-card10-2`, `-card10-3`).

---

## 5. Performance, Concurrency & Caching Architecture

### 5.1 Caffeine Cache Tiers

```mermaid
graph TD
    subgraph Long-Term Reference Caches [TTL: 24 Hours | Max Size: 500]
        C1[sports]
        C2[seasons]
        C3[manufacturers]
        C4[teams]
        C5[brands]
        C6[themes]
        C7[variants]
    end

    subgraph Entity Cache [TTL: 12 Hours | Max Size: 1,000]
        C8[players]
    end

    subgraph Dynamic Query Cache [TTL: 30 Minutes | Max Size: 2,000]
        C9[filteredCards]
    end

    subgraph Default Fallback [TTL: 1 Hour | Max Size: 1,000]
        C10[Any other registered cache]
    end
```

* **Composite Cache Key:** Dynamic queries in `filteredCards` compute compound keys based on `{#filter, #pageable.pageNumber, #pageable.pageSize, #pageable.sort}`.
* **Global Eviction:** `DatabaseChangeDetectorService.evictAllCaches()` purges all cache tiers simultaneously upon detecting external modifications.

### 5.2 Virtual Thread Export Parallelization

When exporting the entire collection partitioned by season (`/export/html`), the controller splits processing across Java 26 Virtual Threads:

```kotlin
Executors.newVirtualThreadPerTaskExecutor().use { executor ->
    cardsBySeason.entries.map { (seasonName, cards) ->
        executor.submit(Callable {
            seasonName to buildSeasonHtml(cards)
        })
    }.map { it.get() }
}
```

This ensures maximum CPU/IO utilization while streaming the resulting ZIP archive directly to the HTTP response stream with minimal memory overhead.

---

## 6. Security & Operational Governance

* **SQL Injection Immunity:** All dynamic queries use Spring Data `JpaSpecificationExecutor` and CriteriaBuilder. No string concatenation for queries.
* **Count-Safe JPA Specification:** Fetch specifications check `query.resultType` before attaching entity joins to preserve compatibility with pagination count queries.
* **Rate Limiting:** `ExportRateLimiter` enforces a dual-protection mechanism:
  - Max 20 export requests per minute per IP.
  - Max 10 concurrent heavy export processes via `Semaphore`.
* **Security Headers:** `SecurityHeadersFilter` applies OWASP-recommended headers:
  - `Content-Security-Policy: default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:; connect-src 'self'; frame-ancestors 'self'; form-action 'self';`
  - `X-Content-Type-Options: nosniff`
  - `X-Frame-Options: SAMEORIGIN`
  - `Referrer-Policy: strict-origin-when-cross-origin`
  - `Permissions-Policy: camera=(), microphone=(), geolocation=(), payment=()`
  - `Strict-Transport-Security: max-age=31536000; includeSubDomains`

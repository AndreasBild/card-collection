# Modern Kotlin 2.x & Spring Boot 4.x Standards

## Architecture & Code Quality

1. **Kotlin Idioms & Immutability:**
   - Prefer Kotlin `data class` for DTOs and value objects.
   - Enforce strict null-safety; avoid force unwrap operator (`!!`) unless guarded by exhaustive null-checks.
   - Leverage extension functions and sealed interfaces/classes for expressive domain modeling.

2. **Java 26 Virtual Threads & Concurrency:**
   - Use Virtual Threads (`Executors.newVirtualThreadPerTaskExecutor()`) for CPU/IO-bound zip batch exports and parallel processing.
   - Ensure non-blocking execution patterns and thread safety.

3. **Caffeine Cache Policy:**
   - Reference data TTL: 24 hours.
   - Player data TTL: 12 hours.
   - Filtered card search TTL: 30 minutes.
   - Explicit eviction on entity mutation.

4. **Security & Input Sanitization:**
   - Parameterize all database interactions via JPA / Criteria / Specification APIs.
   - Sanitize HTML/CSV export outputs (`HtmlUtils.htmlEscape()`, RFC 4180 escaping).
   - Enforce rate-limiting and concurrency controls on heavy export endpoints.

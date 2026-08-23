# Jules Operational Guidelines & Repository Rules

## 1. Primary Directives & Invariants
- **Target Java & Kotlin Versions:** This project strictly uses **Java 26** and **Kotlin 2.x** with **Spring Boot 4.x**.
- **DO NOT MODIFY JAVA VERSION IN POM.XML:** Never modify, downgrade, or revert `<java.version>` or `<kotlin.version>` in `pom.xml` to 21, 17, or any earlier version.
- **Do not downgrade dependencies:** Do not change dependencies or build properties to accommodate older JDKs. The authoritative CI pipeline runs on JDK 26 (`.github/workflows/ci.yml`).

## 2. Test Generation & Quality Standards
- **Frameworks:** JUnit Jupiter (JUnit 5.x), `kotlin.test`, `mockito-kotlin`, and Spring Boot Test slices (`@DataJpaTest`, `@WebMvcTest`).
- **Modern Kotlin & Java 26 Features:** Leverage Kotlin idioms (data classes, null-safety, coroutines/virtual threads, extension functions) and Java 26 virtual threads.
- **Scope:** Focus strictly on generating unit and integration test coverage (`src/test/kotlin`), verifying repository queries, N+1 query prevention, validation edge cases, and export DTO serialization.
- **Database & Flyway Invariants:** Never edit existing Flyway migration scripts (`src/main/resources/db/migration/`). Use in-memory H2 or test containers for integration test validation.

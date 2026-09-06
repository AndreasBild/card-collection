# Jules Operational Guidelines & Repository Rules

## 1. Primary Directives & Invariants
- **Target Java & Kotlin Versions:** This project strictly uses **Java 26** and **Kotlin 2.x** with **Spring Boot 4.x**.
- **DO NOT MODIFY JAVA VERSION IN POM.XML:** Never modify, downgrade, or revert `<java.version>` or `<kotlin.version>` in `pom.xml` to 21, 17, or any earlier version.
- **Do not downgrade dependencies:** Do not change dependencies or build properties to accommodate older JDKs. The authoritative CI pipeline runs on JDK 26 (`.github/workflows/ci.yml`).
- **Main Branch Protection:** Never push commits directly to `main`. Operate strictly within the assigned Pull Request branch.

## 2. Token & Context Efficiency
- **Scoped Diff Inspection:** Do not scan or read the entire repository. Inspect only changed files and methods in the Pull Request diff (`gh pr diff` or PR file list).
- **Context Boundary Discipline:** Avoid loading large data or database dump files (e.g. `src/main/resources/sql/dump/Dump.sql` [~173 KB] or downstream `cards.json` [~770 KB]) into prompt context in full. Inspect strongly typed Kotlin entities and DTOs (`Card`, `Player`, `CardJsonDto`).
- **Lean Review Output:** Keep pull request reviews and generated comments high-signal and concise. Do not dump entire files or verbose logs into PR comments.

## 3. Test Generation & Quality Standards
- **Frameworks:** JUnit Jupiter (JUnit 5.x), `kotlin.test`, `mockito-kotlin`, and Spring Boot Test slices (`@DataJpaTest`, `@WebMvcTest`).
- **Modern Kotlin & Java 26 Features:** Leverage Kotlin idioms (data classes, null-safety, coroutines/virtual threads, extension functions) and Java 26 virtual threads.
- **Scope:** Focus strictly on generating unit and integration test coverage (`src/test/kotlin`) for newly added or altered logic, verifying repository queries, N+1 query prevention, validation edge cases, and export DTO serialization.
- **Database & Flyway Invariants:** Never edit existing Flyway migration scripts (`src/main/resources/db/migration/`). Use in-memory H2 or mock slices for deterministic, isolated testing.

## 4. Execution Efficiency & Formatting
- **Targeted Test Execution (Inner Loop):** During test generation and debugging, run only the specific test class being authored:
  ```bash
  ./mvnw test -Dtest=YourNewTest
  ```
- **Code Standards Compliance:** Adhere to `.editorconfig` formatting rules and Kotlin compiler conventions.
- **Final Verification (Outer Gate):** Run the test suite only once after logic and single-test validation succeed:
  ```bash
  ./mvnw test -B
  ```

---
name: test-suite
description: Execute Maven tests, verify builds, run targeted test slices, and manage subagent test isolation.
---

# Test Suite Skill

Tailored for Maven, Kotlin 2.x, Java 26, and Spring Boot 4.x.

## 1. Fast Inner Loop (Development & TDD)
Iterate quickly during coding without invoking the entire regression suite:
- **Compile & Typecheck:**
  ```bash
  ./mvnw test-compile
  ```
- **Single Test Class:**
  ```bash
  ./mvnw test -Dtest=TargetClassTest
  ```
- **Domain Slice Tests:**
  ```bash
  ./mvnw test -Dtest=*Export*Test,*Slug*Test
  ./mvnw test -Dtest=CardRepositoryTest,CardSpecificationTest
  ./mvnw test -Dtest=DatabaseChangeDetectorServiceTest,DatabaseChangeEventListenerTest
  ```

## 2. Outer Regression Gate (Pre-PR Verification)
Execute only after completing implementation:
- **Full Suite Run:**
  ```bash
  ./mvnw clean test
  ```
- **Full Package & Verify:**
  ```bash
  ./mvnw clean verify
  ```

## 3. Subagent Boundary Isolation for Test Execution
- For verbose multi-suite runs (`./mvnw clean test` or `./mvnw clean verify`), delegate execution to an isolated subagent or background task.
- Subagent must report back an **Executive Memo**:
  1. Status (PASS / FAIL).
  2. Metrics (tests run, failures, execution time).
  3. Isolated failure snippets with file paths (suppressing passing test outputs).

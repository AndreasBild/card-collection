---
name: run-test-suite
description: Execute the full Maven test suite, run integration tests, and check build health.
---

# Run Test Suite Skill

Use this skill to verify the entire application health, run unit and integration tests, and check compilation.

## Command Reference

### 1. Run Unit & Integration Tests (Fast)
```bash
./mvnw clean test
```

### 2. Run Full Build & Verification
```bash
./mvnw clean verify
```

### 3. Run Specific Test Class
```bash
./mvnw test -Dtest=CardControllerTest
```

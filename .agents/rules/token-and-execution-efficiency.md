# Token Economics & Execution Efficiency Rule

## 1. Context Boundaries & Token Hygiene
- **Massive Data Files Invariant:** Never read massive data, database dump, or cache files in full into context. Specifically:
  - `src/main/resources/sql/dump/Dump.sql` (~173 KB / large relational dump)
  - Downstream sync target `../card-collectionJava/content/json/cards.json` or local `cards.json` (~770 KB / ~200k tokens)
  - Generated export zip archives or batch HTML files
- **Targeted Inspection Protocol:**
  - Use `grep_search` with specific query terms to locate entity definitions, repository methods, or table schemas.
  - Use `view_file` strictly with bounded `StartLine` and `EndLine` slices (maximum 50–100 lines at a time).
  - Inspect Kotlin data classes and entities (`Card`, `Player`, `CardJsonDto`, `FilterCriteria`) rather than raw SQL dumps or JSON payloads whenever possible.
- **Surgical Diffing:** Always use targeted diff tools (`replace_file_content` or `multi_replace_file_content`) with minimal necessary context lines. Never rewrite entire large Kotlin classes or configuration files unmodified.

## 2. Dynamic Model Tier Recommendation Protocol
To maintain peak token efficiency and execution accuracy, agents dynamically evaluate incoming tasks and propose the appropriate model tier in implementation plans or initial scoping responses:

| Model Tier | Capability Profile | Typical Workflows in `card-collection` |
| :--- | :--- | :--- |
| **Tier 1: Fast / Medium**<br>*(Latest Flash / Medium in IDE)* | High throughput, sub-second latency, optimal token economy. | • Spring Data JPA repository & service minor enhancements<br>• Single-class JUnit 5 unit tests (`./mvnw test -Dtest=TargetTest`)<br>• Flyway migration DDL creation (`V...__...sql`)<br>• DTO mapping and Jackson serialization (`CardJsonDto`)<br>• Controller endpoints, Thymeleaf views, and static assets<br>• Routine bugfixes, dependency bumps, and agent governance rules |
| **Tier 2: Deep Reasoning / Pro**<br>*(Latest Pro / Thinking in IDE)* | Multi-step reasoning, architectural synthesis, subtle edge-case detection. | • Dynamic JPA `Specification<Card>` multi-join query optimization<br>• Count query syntax error prevention and pagination guarding<br>• Multi-system 3NF database schema refactoring and constraint topology<br>• Java 26 Virtual Thread batch export coordination & streaming ZIPs<br>• Multi-tier Caffeine cache synchronization and `DatabaseChangeDetectorService` signature tracking<br>• Downstream SSOT export schema contract negotiations (`card-collectionJava`) |

*Implementation Plan Standard:* Every `implementation_plan.md` must include a `## 🎯 Recommended Execution Model` section declaring the recommended tier and rationale.

## 3. Working Tree Hygiene & Unrelated Changes
- **Preserve User Data Work & Local Configs:** The workspace owner may have local overrides in `application-local.properties`, unstaged baseline syncs, or independent database exports.
- **Never Run `git add .` Blindly:** Strictly stage only files directly modified for the agent's specific task.
- Never discard or overwrite existing uncommitted changes in database dump files (`Dump.sql`) or export targets unless the task explicitly targets schema dump synchronization.

## 4. Frequent Execution Shortcuts
Always prefer targeted Maven execution commands over full rebuilds:
- **Fast Syntax & Type Check:** `./mvnw test-compile`
- **Single Test Class (Inner Loop):** `./mvnw test -Dtest=TargetClassTest`
- **Export & Slug Verification:** `./mvnw test -Dtest=*Export*Test,*Slug*Test`
- **Repository & Query Verification:** `./mvnw test -Dtest=CardRepositoryTest,CardSpecificationTest`
- **Change Detector & Event Listener:** `./mvnw test -Dtest=DatabaseChangeDetectorServiceTest,DatabaseChangeEventListenerTest`

## 5. Dual-Loop Execution Strategy
Separate iteration into two distinct loops:

### 5.1 Fast Inner Loop (Active Development & TDD)
Do NOT run the entire test suite on intermediate code changes.
1. Incremental Compilation: `./mvnw test-compile`
2. Targeted Test: `./mvnw test -Dtest=TargetClassTest`

### 5.2 Comprehensive Outer Gate (Pre-Commit / Pre-PR)
Execute only after the inner loop passes and task logic is finalized:
1. `./mvnw clean test` (runs full JUnit 5 suite across all slices).
2. Verify Flyway migrations and `src/main/resources/sql/dump/Dump.sql` consistency if schema was modified.
3. Verify DTO backward-compatibility (`CardJsonDto`).

## 6. Subagent Boundary Isolation
- **Context Pollution Prevention:** Heavy verification runs (e.g. `./mvnw clean test`, full multi-suite regression, extensive linting) generate thousands of lines of terminal output that quickly consume context tokens and degrade the main orchestrator's reasoning window.
- **Isolated Subagent Delegation:** Offload verbose, long-running build, test, or verification runs to isolated subagents or asynchronous background executions.
- **Executive Memo Protocol:** Subagents must synthesize execution outcomes into a compact executive memo returned to the primary context:
  - **Status:** Explicit PASS / FAIL verdict.
  - **Metrics:** Total tests executed, passed, failed, and duration.
  - **Surgical Failure Snippets:** Extract only relevant compiler errors or failing stack traces; suppress successful test outputs.
  - **Actionable Remediation:** Provide direct clickable links to offending source files and line numbers.

## 7. Command Output & Log Truncation
- Avoid commands that generate unbounded terminal output into conversation context.
- Scope and filter Maven commands (`-Dtest=...` or `-q`).
- Do not poll running tasks or background timers in tight loops. Use reactive wakeup.

## 8. Cache & Signature Preservation
- Respect Caffeine cache TTLs and `DatabaseChangeDetectorService` entity signature metrics.
- Never arbitrarily evict or disable cache mechanisms unless testing change listener reactivity.

## 9. High-Signal Communication
- Eliminate conversational pleasantries, repetitive apologies, and generic introductions.
- Always provide concise, actionable markdown with direct clickable `file://` links.


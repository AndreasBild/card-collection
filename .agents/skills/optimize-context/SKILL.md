---
name: optimize-context
description: Self-auditing routine to detect rule bloat, enforce token hygiene, calibrate model tiers, and maintain progressive disclosure efficiency.
---

# Optimize Context Skill

Routine for auditing repository governance, preventing rule bloat, and keeping prompt overhead minimal.

## 1. Context Budget & Token Hygiene Invariants
- **Root Kernel Budget:** `AGENTS.md` must remain ≤ 40 lines (target ~30 lines, < 400 tokens).
- **Single Progressive Layer (`.agents/skills/`):** Operational instructions, domain runbooks, and verification checklists live exclusively in `.agents/skills/<skill>/SKILL.md` (loaded on demand), avoiding duplicate unindexed files in `.agents/rules/`.
- **Deterministic Hooks (`.agents/hooks.json`):** Enforce hard command safety gates (`git add .` block, `main` commit protection) via `PreToolUse` hooks at zero token cost.
- **Targeted Inspection & Surgical Diffs:**
  - Never read `src/main/resources/sql/dump/Dump.sql` (~173 KB), `cards.json` (~770 KB), or export archives in full.
  - Use `read_file` strictly with bounded `StartLine`/`EndLine` slices (≤ 100 lines) and `edit_file` for minimal surgical replacements.
- **Subagent Isolation:** Offload verbose test suites (`./mvnw clean test`) to subagents or background tasks returning compact Executive Memos.

## 2. Dynamic Model Tier Calibration

| Model Tier | Capability Profile | Typical Workflows in `card-collection` |
| :--- | :--- | :--- |
| **Tier 1: Fast / Medium**<br>*(Flash / Medium)* | High throughput, low latency, optimal token economy. | • Repository & service minor enhancements, single-class JUnit 5 tests<br>• Flyway migration DDL (`V...__...sql`), `CardJsonDto` mapping<br>• Controller endpoints, Thymeleaf views, static assets, governance updates |
| **Tier 2: Deep Reasoning / Pro**<br>*(Pro / Thinking)* | Multi-step reasoning, architectural synthesis, edge-case detection. | • Dynamic JPA `Specification<Card>` multi-join optimization & count-query guards<br>• 3NF schema refactoring, Java 27 Virtual Thread streaming ZIP exports<br>• Caffeine cache + `DatabaseChangeDetectorService` signature tracking |

## 3. Self-Audit Checklist

1. **Root Kernel Line Check:**
   ```bash
   wc -l AGENTS.md
   ```
   - Must be ≤ 40 lines; extract any operational runbooks into `.agents/skills/`.
2. **Skill Frontmatter & Router Verification:**
   - Verify each `.agents/skills/<skill-name>/SKILL.md` contains valid YAML frontmatter (`name`, `description`) and matches the router table in `AGENTS.md` 1:1 with zero duplicate skills.
3. **Dataset Boundary Audit:**
   - Confirm `.antigravityignore` and `.geminiignore` exclude `src/main/resources/sql/dump/Dump.sql`, `cards.json`, and `target/`.
4. **Hook Health Check:**
   - Confirm `.agents/hooks.json` and `.agents/scripts/pre-command-guard.sh` are executable and active.

---
name: optimize-context
description: Self-auditing routine to detect rule bloat, audit context token budgets, and maintain progressive disclosure efficiency.
---

# Optimize Context Skill

Routine for auditing repository governance, preventing rule bloat, and keeping prompt overhead minimal.

## 1. Context Budget Invariants
- **Root Kernel Budget:** `AGENTS.md` must remain ≤ 40 lines (target ~35 lines, < 400 tokens).
- **Progressive Disclosure:** Operational instructions, multi-step runbooks, and deep domain guides belong in `.agents/skills/` or `.agents/rules/`, not in the root kernel.
- **Subagent Delegation:** Lengthy command logs must be isolated into subagents returning executive memos.

## 2. Self-Audit Checklist

### Step 1: Root Kernel Audit
Run a line count check on the root kernel:
```bash
wc -l AGENTS.md
```
- If lines > 40: Identify operational runbooks or tables and extract them into dedicated skills in `.agents/skills/`.

### Step 2: Skill Frontmatter & Router Verification
- Verify each `.agents/skills/<skill-name>/SKILL.md` contains valid YAML frontmatter:
  ```yaml
  ---
  name: <skill-name>
  description: <high-signal summary used by agent router>
  ---
  ```
- Verify the router table in `AGENTS.md` lists every active skill.

### Step 3: Dataset Boundary Audit
- Confirm `.antigravityignore` and `.geminiignore` prevent ingestion of:
  - `src/main/resources/sql/dump/Dump.sql`
  - Downstream export targets (`cards.json`)
  - Build targets (`target/`)
- Verify tools use bounded slices (`StartLine`/`EndLine` ≤ 100) or `grep_search` instead of whole-file reads.

### Step 4: Model Tier Calibration
- Ensure `token-and-execution-efficiency.md` reflects current model tiers (e.g. Flash/Medium for routine tasks, Pro/Thinking for complex architecture).

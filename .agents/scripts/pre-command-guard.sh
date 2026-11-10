#!/bin/sh
# Deterministic PreToolUse guard for card-collection repository invariants:
# 1. Block broad staging (`git add .`, `git add -A`, `git add --all`)
# 2. Block direct `git commit` while on the `main` branch

PAYLOAD=$(cat)

# Extract CommandLine from JSON payload
if echo "$PAYLOAD" | grep -Eq '"CommandLine"[[:space:]]*:[[:space:]]*"[^"]*git[[:space:]]+add[[:space:]]+(\.|-A|--all)([[:space:]";&|]|")'; then
  printf '{"decision":"deny","reason":"Blocked by repo-governance-guard: Never run git add . or git add -A. Selectively stage only task-specific files."}\n'
  exit 0
fi

# Block direct commits to main branch
if echo "$PAYLOAD" | grep -Eq '"CommandLine"[[:space:]]*:[[:space:]]*"[^"]*git[[:space:]]+commit([[:space:]";&|]|")'; then
  REPO_ROOT=$(cd "$(dirname "$0")/../.." && pwd)
  BRANCH=$(git -C "$REPO_ROOT" branch --show-current 2>/dev/null)
  if [ "$BRANCH" = "main" ]; then
    printf '{"decision":"deny","reason":"Blocked by repo-governance-guard: Direct commits to main are forbidden. Switch to a topic branch (feature/*, fix/*, chore/*, migration/*) first."}\n'
    exit 0
  fi
fi

printf '{"decision":"allow"}\n'

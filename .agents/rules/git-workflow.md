# Git Workflow & Automated Pull Request Invariant

## Rule: Always Commit, Push & Trigger PR at Task Conclusion

Whenever a feature, bugfix, refactoring, database migration, or configuration task is completed:

1. **Verify Code Health First:**
   - Ensure all unit and integration tests pass cleanly (`./mvnw clean test` or `./mvnw clean verify`).
   - Confirm no compilation warnings or broken specifications.

2. **Stage & Commit Changes:**
   - Stage all modified and newly created source/test/resource files.
   - Craft a clean, semantic commit message following conventional commits format (e.g. `feat(export): optimize virtual thread batch rendering and caching`, `fix(query): guard count queries against fetch joins in dynamic specification`).

3. **Push & Trigger Auto-PR:**
   - Push the isolated topic branch (`feature/*`, `fix/*`, `chore/*`, or `migration/*`) to `origin` (`git push -u origin <branch-name>`).
   - The push automatically triggers `.github/workflows/auto-pr.yml` on GitHub to create the Pull Request and invoke Jules CI validation.
   - Always provide the PR link or branch status in the final response.

4. **Never Leave Completed Work Uncommitted:**
   - Do NOT stop and ask the user if they want a PR created; execute the commit, push, and PR creation workflow automatically as the final step of each task.

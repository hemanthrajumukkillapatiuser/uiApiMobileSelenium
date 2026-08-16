# Git Push Agent

Commit changes to the current branch, push to remote, and optionally pull latest from main.

## Workflow

### 1. Show current state

Run and display the output of:

```bash
git status
git branch --show-current
```

If the working tree is clean (nothing to commit), say so and stop.

### 2. Show what changed

Run `git diff` and `git diff --cached`. Summarize the changes for the user — which files changed, what was added/modified/deleted.

List any untracked files that would be included.

### 3. Safety check

Before committing, verify none of these are in the changeset:
- `.env`, `credentials.json`, or files likely containing secrets
- IDE files (`.idea/`, `.vscode/`)
- Build artifacts (`target/`, `node_modules/`, `dist/`)

If any are found, warn the user and exclude them.

### 4. Get confirmation

Ask the user:
- Do the changes look correct?
- What commit message should be used? (suggest one based on the diff)

**Do not commit without explicit user approval.**

### 5. Commit and push

```bash
git add <specific files>
git commit -m "<agreed message>"
git push -u origin <current-branch>
```

### 6. Report

Tell the user:
- Commit hash and message
- Branch name (local and remote)
- Remote repository URL

### 7. Offer to pull from main

Ask: "Do you want to pull the latest changes from main into this branch?"

If yes:

```bash
git fetch origin main
git merge origin/main
```

If there are merge conflicts, show them and ask the user how to resolve. Do not auto-resolve.

If the merge is clean, push the updated branch and confirm.

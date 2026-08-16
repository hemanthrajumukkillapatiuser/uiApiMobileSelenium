---
name: push
description: Commit all changes to the current branch, push to remote, and optionally pull latest from main. Shows a summary of what was committed and the remote branch.
user_invocable: true
---

# Git Push Skill

Commit changes, push to remote, and optionally sync with main.

## Steps

### 1. Check the current state

Run these commands and show the output to the user:

```bash
git status
git branch --show-current
```

If there are no changes (nothing staged, nothing modified, no untracked files), tell the user "Nothing to commit — working tree is clean" and stop.

### 2. Show the diff

Run `git diff` and `git diff --cached` to show both unstaged and staged changes. Summarize the changes for the user in a short list (files changed, what was added/modified/deleted).

If there are untracked files, list them and note they will be included.

### 3. Ask for confirmation

Before committing, ask the user:
- Whether the changes look correct
- For a commit message (suggest one based on the changes)

**Do NOT proceed without explicit confirmation.** Never commit secrets, `.env` files, credentials, or IDE-specific files (`.idea/`, `.vscode/`). Warn the user if any such files are in the changeset.

### 4. Stage and commit

Stage the relevant files (prefer specific file names over `git add -A`). Create the commit with the agreed message.

### 5. Push to remote

Push the current branch to origin:

```bash
git push -u origin <current-branch>
```

### 6. Report what happened

Show the user:
- The commit hash and message
- The branch name (local and remote)
- The remote URL

### 7. Offer to pull from main

Ask the user: "Do you want to pull the latest changes from main into this branch?"

If yes:

```bash
git fetch origin main
git merge origin/main
```

If there are merge conflicts, show them and ask the user how to resolve — do not auto-resolve.

If the merge succeeds, push the updated branch:

```bash
git push
```

Report the result.

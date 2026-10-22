---
name: ledger-tasks-yylo
description: Kanban-style task ledger for coding agents -- create, list, search, and update tasks, manage blocked-by dependencies, surface ready work, and topologically order parallel execution against a repository-local, hash-chained Markdown store. Use whenever the user mentions tasks, a kanban board, a backlog, to-dos, dependencies, blocked work, "what should I work on next", or tracked task state for a coding-agent project, or asks to plan, split, prioritize, retrieve, or finish recorded work -- even if they do not say "ledger".
license: Complete terms in LICENSE.txt
compatibility: Requires Node.js >= 20.10 and the YYLO CLI (`npm install --global '@yylo/cli@latest'`); task state lives in repository-local Markdown under `.juno_task/`.
metadata:
  short-description: "Track coding-agent tasks and dependencies in a git-native Kanban ledger"
  author: YYLO
  source: https://github.com/yylo-dev/yylo-skills/tree/main/skills/ledger-tasks-yylo
---

# Ledger Tasks (YYLO)

Entry point for task and dependency tracking with the YYLO Ledger, from any coding agent that reads a skills folder (JetBrains AI Assistant, the GitHub Copilot plugin, Claude Code, Codex, and others). It records kanban-style task state as Markdown inside the repository, so boards, dependencies, and completion receipts travel with the code and stay reviewable in Git.

| Need | Command group |
| --- | --- |
| Create / update / archive tasks | `yy ledger create` · `update` · `archive` |
| Browse and find work | `yy ledger list` · `search` |
| See what a task depends on | `yy ledger deps TASK_ID` |
| Find safe-to-start work | `yy ledger ready` |
| Order work for parallel execution | `yy ledger order` |
| Record progress or completion | `yy ledger mark in_progress|done --id --response` |
| Retrieve any stored record by ID | `yy ledger get RECORD_ID` |

## How to use this skill (read first -- act, do not stall)

1. **Act now, in this turn.** If the task operation is clear, run the command below immediately; missing tooling is installed as part of the task, not reported as a blocker.
2. **Do not re-ask for what you were given.** If the request already names a task ID or description, use it.
3. **Preflight once per session**: `yy ledger --version` and `yy ledger --help` -- installed help is authoritative for the selected runtime.
4. **Never hand-edit ledger store files.** All reads and writes go through the CLI so receipts and revisions stay consistent.

## Step 1: Ensure the CLI is present

```bash
node --version          # needs >= 20.10
npm install --global '@yylo/cli@latest'
yy --version
```

In a project, initialize once (creates the repository-local `.juno_task/` store):

```bash
yy init --task "Document the onboarding path"
```

## Step 2: Run the task loop

**CREATE** -- add a task

```bash
yy ledger create "Export the CSV report" --status backlog --tags feature,backend
```

Options: `--status` (backlog|todo|in_progress|done), `--tags`, `--blocked-by` (task IDs), `--related-tasks` (task IDs).

**LIST / SEARCH** -- browse with summary stats, or filter

```bash
yy ledger list --limit 5 --sort asc
yy ledger list --status todo,in_progress --limit 10
yy ledger search --status todo --tag backend --limit 10
yy ledger search --body "OAuth" --open
```

**GET** -- read a task (or any stored record) by exact ID

```bash
yy ledger get TASK_ID
```

**MARK** -- status changes require a response message documenting what was done

```bash
yy ledger mark in_progress --id TASK_ID --response "Starting: implemented X"
yy ledger mark done --id TASK_ID --response "Completed: implemented X, tested Y" --commit abc123def
```

`--commit` links the change to Git history when marking done.

**UPDATE / ARCHIVE** -- modify fields, or soft-delete while preserving data

```bash
yy ledger update TASK_ID --tags backend,urgent
yy ledger archive TASK_ID
```

## Step 3: Manage dependencies

```bash
yy ledger deps TASK_ID                       # blockers, dependents, priority score
yy ledger deps add --id TASK_ID --blocked-by BLOCKER1 BLOCKER2
yy ledger deps remove --id TASK_ID --blocked-by BLOCKER1
yy ledger ready                              # all blockers satisfied
yy ledger order --scores                     # topological order for parallel work
```

Cycle detection rejects circular dependencies automatically. Dependencies and related tasks can also be declared inline in task body text with `[blocked_by]TASK_ID[/blocked_by]` and `[task_id]RELATED_ID[/task_id]` markup, parsed on create/update.

## Best practices

1. Size tasks to complete within one iteration without filling the context window.
2. Follow the status flow backlog -> todo -> in_progress -> done (archive for abandoned work).
3. Always pass `--response` on `mark`, and attach `--commit` when marking done.
4. Start from `ready` and plan parallel pipelines with `order --scores`.
5. Report the immutable record ID returned by create/get operations; never invent one from a title.
6. Cross-project access is opt-in (disabled by default); configure via the controller's registry settings rather than reaching into other boards.

## Scope boundaries

- The ledger store is repository-local Markdown; agents never write store files by hand and never bypass the CLI's receipts.
- Archival, merge, and cross-directory consolidation operations are plan-first: produce and review a plan file, then apply it with a retained receipt. Never automate archival.
- This tile is the task-tracking entry point of an eight-skill set (wiki, artifacts, planning, project-understanding, workflow, review loop, benchmark). The full set installs with:

```bash
npx skills add yylo-dev/yylo-skills
```

Upstream skill source, versions, and release tags: [yylo-dev/yylo-skills](https://github.com/yylo-dev/yylo-skills).

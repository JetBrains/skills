---
name: managing-youtrack
description: |
  Interact with YouTrack issue tracker via REST API. Create, read, update, and
  search issues and drafts; manage comments, tags, and issue links; log work
  items; inspect custom field schemas; list saved queries; look up users and
  groups. Use when the user asks about YouTrack issues, wants to file bugs,
  update tickets, add comments, manage tags or links, track time, or search
  for issues. Requires YOUTRACK_URL and YOUTRACK_TOKEN environment variables.
allowed-tools:
  - Bash
  - Read
  - AskUserQuestion
metadata:
  source: https://github.com/forketyfork/agentic-skills/tree/main/skills/managing-youtrack/skills/managing-youtrack
---

# YouTrack REST API

## Setup (minimal)
- NEVER use pipes after curl commands to parse the output, do that in a separate tool call.
- Always pass the auth header via `"${YOUTRACK_TOKEN}"`. Always include `Accept: application/json`; add `Content-Type: application/json` for write calls.
- For queries containing spaces or symbols, use `curl -G --data-urlencode "query=..."` instead of embedding the query string.
- Request only needed fields via the `fields` parameter; default minimal issue fields: `idReadable,summary`.

## Navigation
- Issues, drafts, comments → [reference/issues.md](reference/issues.md)
- Tags, links, work items → [reference/metadata.md](reference/metadata.md)
- Fields, saved searches, users, groups → [reference/admin.md](reference/admin.md)
- Field presets and `$type` values → [reference/fields.md](reference/fields.md)

## Output requirement
After create/update/delete, print a link to the affected item:
- Issue: `$YOUTRACK_URL/issue/<idReadable>`
- Comment: `$YOUTRACK_URL/issue/<idReadable>#focus=Comments-<COMMENT_ID>`
- Drafts have no web URL.

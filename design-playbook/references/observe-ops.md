# observe* operating detail

Load only when `execute_capture_plan` is available (see [`load-map.md`](load-map.md)).

1. Derive capture plan from each runtime L6 criterion (`Given/When` → state+actions).
2. Call capture contract v1: `schemaVersion: 1`, explicit `viewport`, default freeze on.
3. Append one `manifest.jsonl` line per capture with full request snapshot + provider return.
4. Prefer live host URL; mirror surfaces require `surface: mirror` note + evaluator finding.

Skip narration template: `-> observe*: adapter absent, skipped (G6 not triggered; enable via packages/design-playbook/mcp/evidence/ + Playwright)`.

# Optional operations load map

| Reference | Load when | Gate if skipped |
| --- | --- | --- |
| [`preview-ops.md`](preview-ops.md) | MCP `tools/list` exposes `preview_prototype` after decision report | G5 not triggered; narrate enable path `mcp/preview/` |
| [`observe-ops.md`](observe-ops.md) | MCP `tools/list` exposes `execute_capture_plan` after craft | G6 not triggered; narrate enable path `mcp/evidence/` + Playwright |

Shared declarations loaded by the pipeline itself (not optional, listed for discoverability):

| Artifact | Loaded by | Notes |
| --- | --- | --- |
| [`rules.md`](rules.md) (rule registry) | `craft-guard` (predicate evaluation + seven-column rows), `ui-evaluator` (audit-row consumption) | Product-level, read-only at run time; G8 self-check lives in the repo validation gate |
| `.scratch/<run>/shaping/shaping-log.jsonl` + `queue.json` | `ux-spec` session (S0-S6), `run-status` narration | Run-level; G9 gates the session exit; the queue is derived, never hand-edited |
| `plan.md` run-profile block | orchestrator (tier grading), `run-status` narration | Mandatory block; skip list carries the one-line skip narration duty |

Main orchestrator keeps routing, gates, boundaries, and Done-when criteria. Optional operating detail lives only in these references.

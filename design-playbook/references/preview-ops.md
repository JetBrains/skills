# preview* operating detail

Load only when `preview_prototype` is available (see [`load-map.md`](load-map.md)).

1. Generate disposable prototype HTML under `.scratch/<run>/preview/round-{n}.html`.
2. Before opening the preview, narrate the annotation flow to the user: open Annotate → Pick to annotate, then click page elements to add anchors; write a note per anchor (or overall feedback); Ctrl/Cmd+Enter submits, Ctrl/Cmd+Z undoes (Shift redoes).
3. Call `preview_prototype` with path/html, summary, round, report_ref, options.
4. Require feedback floor (ADR-0008): non-empty feedback or anchors with selector+comment.
5. Confirm record must have `confirmed=true` and `floor_pass=true` before Fill.
6. Same blocker two repair rounds without new evidence → stop and report.

Skip narration template: `-> preview*: adapter absent, skipped (G5 not triggered; enable via packages/design-playbook/mcp/preview/ or host MCP)`.

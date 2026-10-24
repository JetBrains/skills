# Craft audit protocol (registry reference layer)

The craft detectors live in the first-party rule registry: [`../../design-playbook/references/rules.md`](../../design-playbook/references/rules.md) (`CRAFT-01` … `CRAFT-08`, all `advisory` / `first-party`, plus the cross-cutting `A11Y-01`, `RESP-01`, and the placeholder `I18N-01`, `PERF-01`, `SEC-01` entries). This file is the thin execution reference: how to evaluate an entry's applicability predicate and how to write its audit row. The registry is the single authority for detector definitions — do not duplicate entry text here.

Run every registry craft entry whose applicability predicate evaluates to `applicable` for implemented UI. Inspect rendered UI at declared target viewports and relevant source. Generic registry outcomes never override a verified project baseline; safety, usability, and explicit declarations still do.

Record exactly one seven-column row per registry craft entry whose applicability predicate evaluates to `applicable` in `.scratch/<run>/craft-guard.md`:

| ID@ver | Applicability | Predicate reason / missing proof | Result | Rendered evidence | Source evidence | Exception check | Positive fix |
| --- | --- | --- | --- | --- | --- | --- | --- |
| CRAFT-01@1 | applicable | - | clear\|hit | observable or missing proof | source location or missing proof | applied exception or none | required for hit; `-` otherwise |

- `Applicability` is the entry's three-state predicate outcome: `applicable`, `not-applicable`, or `blocked`. Old single-status rows (`clear|hit|blocked|N/A`) are no longer valid in this format.
- `not-applicable` requires an observable reason in the reason column (blank is invalid); it is never a silent skip.
- `blocked` names the missing proof in the reason column (for example motion source absent from review input).
- `Result` is `clear|hit` only when applicable; otherwise `-`. `Positive fix` is required on hit rows.
- Rows are advisory: do not assign declaration source, severity, or verdict. `ui-evaluator` owns those decisions.

---
name: ui-evaluator
description: Run evidence-backed UI acceptance. Use after generating a page, or when the user wants a design review or recirculatable critique against declared goals and success criteria.
metadata:
  short-description: "Point-back UI acceptance"
  author: Bandersnatch0x
  source: https://github.com/Bandersnatch0x/design-playbook/tree/main/packages/design-playbook/skills/ui-evaluator
---

# ui-evaluator

**Evaluator** contract: turn **declarations** into checks. Do not invent new taste standards. Every issue **points back** to a declaration.

## Steps

### 1. Bind declarations

Identify which of these apply to this surface (repo files, prior turns, or design-playbook defaults):  
`spec` · `domain` · `craft` · `design` · `components` · `template`.

When a verified `.scratch/<run>/design-baseline/state.json` binds a baseline (`status: ready`, path from `baseline.path`), include it as the project-specific visual declaration. It can support design-drift findings but is never L6 runtime proof by itself. An explicit `status: waived` disables baseline-drift checks for that run; it does not waive `spec`, accessibility, or craft checks.

When `.scratch/<run>/reference/contract.md` exists (ADR-0011), you **may** use it as supporting context for findings about copied brand chrome, distinctive illustration, or other **Do not copy** breaches (`source` = `reference` or the owning declaration). It is **never** L6 proof and never a Pass/Fail gate by itself.

**Done when:** the check set is named; if `spec` L6 exists, every criterion and its required proof are on the list.

### 2. Run checks

Walk every applicable row (exhaustive for bound declarations):

| Check | Source |
| --- | --- |
| Empty / loading / error / permission | `spec` |
| Risk color, secrets, dangerous ops | `domain` |
| AI slop, hierarchy, purposeless motion | `craft` |
| New surface drifts from confirmed project visual roles/patterns | bound `<binding.path>` |
| Raw hex / px / ms (unlogged) | `design` |
| Badge/Tag, Dialog/Drawer, … | `components` |
| Shell matches scene | `template` |
| Each L6 acceptance item | `spec` |
| Required proof exists for each L6 item | `spec` |

Dimension selection, 回流示例, the preview-seam health check (required when the run produced `preview/` artifacts), and the **observe\* mirror-surface** finding (required when any manifest capture notes `surface: mirror`): [`references/rubric.md`](references/rubric.md).

When L6 proof includes an `a11y tree` artifact, interpret it with [`references/a11y-tree.md`](references/a11y-tree.md) (names, roles, states, keyboard path, focus, material omissions). Record accessibility evidence on the owning user-risk criterion; do not invent separate taste standards.

When `.scratch/<run>/craft-guard.md` exists, consume exactly one seven-column audit row for every registry craft entry whose applicability predicate evaluates to `applicable` (full catalog, or every **enabled** subset row when the contract declares a smaller set — registry: [`../design-playbook/references/rules.md`](../design-playbook/references/rules.md)). Summarize the applicable count, and warn when every evaluated entry is `not-applicable`, any `not-applicable` row lacks an observable reason, or the `blocked` rate is abnormal. Audit rows are advisory: verify their rendered/source evidence and exception checks, then use the authoritative recirculate map below to choose declaration source and assign severity. A detector never decides source, severity, or verdict. Carry every `blocked` row into evaluation as a craft proof gap. Implemented UI cannot claim complete craft Pass while required rendered or source proof is blocked; planning-only work records an explicit `not-applicable` reason without claiming rendered inspection. Keep craft detector rows out of the G6 manifest and L6 evidence ledger: they are craft-stage audit records, not runtime artifacts or criterion results.

Record an evidence ledger before writing findings. Every L6 criterion has exactly one row:

```text
criterion: L6.<n>
required:  <declared proof>
observed:  <artifact path, interaction, check result, or missing>
result:    pass|fail|blocked|N/A
```

`observed` is either an **artifact path** (relative to the run root, e.g. `evidence/L6.3-error.png`) when a runtime capture was bound by a manifest, or **free-text** describing a manual observation. Both are legitimate; the machine seam (G6) only validates artifact-path references. When using an artifact path, keep the path as the **leading token** of the line (e.g. `observed: evidence/L6.3-error.png`); trailing commentary is tolerated by G6 — it reads the leading token, breaking on whitespace, `(` / `（`, or `,` / `，` / `:` / `：`. Other punctuation (em dash, slashes, etc.) will be treated as part of the path, so put elaboration on a separate `note:` line for clarity when unsure.

Evidence is captured, not judged. A manifest entry records that an artifact was collected at a state — it does not say the criterion passed. `pass`/`fail` is this evaluator's verdict against `required` vs `observed`; a screenshot can prove a criterion false. Three ledgers, each one authority: `spec` L6 names **what to prove**; the manifest records **what happened**; this ledger decides **what it means**. Providers produce artifacts; the manifest binds them to criteria; the evaluator decides.

For implemented UI, visible-state proof is a rendered inspection at the declared target viewport; behavior proof is an interaction trace or automated check; code-health proof is the relevant available test, type/lint, or affected build result. Planning-only proof is declaration coverage and must not claim a render or test occurred. Non-L6 declaration checks may be supporting observations or findings; they do not enter the machine ledger.

The host model may have no vision (text-only input). Reading a screenshot would break such a session — never make viewing an artifact a review action. Render artifacts stay bound as path references (manifest + ledger `observed`), and machine assertions judge the **text face**: HTML/CSS source, `a11y tree` text, and interaction-trace JSON. A no-vision run reviews this way end to end without degrading the protocol; note it once in the Limitations statement ("this run was reviewed on text-face evidence").

**Done when:** every bound row was considered; every L6 criterion has exactly one non-empty `criterion / required / observed / result` row keyed as `L6.<n>`; results use only `pass|fail|blocked|N/A`; unavailable required proof is `blocked`, not skipped.

### 3. Emit point-back findings

```text
issue:    <observable>
source:   <declaration>
fix:      <next edit>
severity: S3|S2|S1|S0
```

Severity is the **consequence axis** (S3 blocking-severity / S2 major / S1 minor / S0 positive or info), graded by user-visible impact and referencing the affected L6 / primary-path node. The legacy values `high (blocking)|high|med|low` are **no longer legal** (alias period ended, v0.20.0 breaking change): they are structural errors at G2 — write the axis values directly (`high (blocking)`→S3 + `disposition: blocking`, `high`→S2, `med`/`low`→S1).

Additional field lines (machine-tolerated; validated when present) complete the review axis:

```text
track:       product|interaction|cross-cutting
confidence:  high|medium|low   (evidence layers x reproducibility x judging subject)
disposition: blocking|advisory|info   (severity x fact/judgment class x confidence)
evidence:    <artifact path or source ref — may repeat>
assumes:     <assumed contract field paths the finding depends on, if any>
rule:        <registry ID@version refs, when a registry rule is involved>
dd:          <decision-report entry ref, when a design decision is challenged — never on positive (S0) findings>
```

Severity and disposition are **two axes**: a judgment-class S3 (subjective / semantic / representativeness) is never directly blocking — list it in the Limitations "pending user adjudication" sub-block with the three options (change declaration / accept risk / promote to the rule-registry queue). Only fact-class S3 (reproducible, evidence-bound) takes `disposition: blocking` and enters G4 closure.

Order: **blocking** first (broken L5/L6, unsafe dangerous ops, removed focus rings), then polish.

**Done when:** every finding has all four fields; no “generally improve the design” lines; additional fields use only their declared value sets.

### 4. Verdict

- Emit exactly one `## Verdict` section containing exactly one anchored verdict: `Pass` or `Recirculate`.
- **Pass:** zero blocking; every L6 criterion has exactly one evidence row; every required evidence row passes (every evidence result is `pass`); token gaps are logged or fixed.
- **Recirculate:** each blocking `source` names the step/declaration to reopen in design-playbook; `fail` or `blocked` evidence remains visible.

For a repaired blocker, record exactly one closure line whose issue text is identical to the finding:

```text
- closes: <exact issue value> -> recirculate -> fix -> re-eval -> 0 blocking
```

**Done when:** the explicit verdict is structurally unique; blocking sources are non-empty; every blocking finding has exactly one matching closure before `Pass`. A blocking finding cannot be waived inside a Pass artifact. Without a user in the loop, blocking findings remain in recirculate and the run requests a decision; only after an explicit user decision that updates the owning declaration or severity — recorded against the user's statement or decision record — may the evaluator re-evaluate; the final Pass artifact contains no blocking severity.

### 5. Six-block report structure

The report artifact remains `point-back.md` (no new file). The machine face is unchanged — four-field findings, four-field ledger rows, closure lines, verdict semantics — and existing parsers tolerate the new blocks. The full structure:

```text
## Evidence ledger          (one row per L6; required rows may note assumed deps)
## Findings                 (four fields + additional field lines)
## Positive findings        (S0/info rows + pattern-level positives; AC-level
                             positives are the ledger pass rows themselves)
## Coverage statement       (exhaustive-review completion / sampling + reasons /
                             explicit unreviewed list; G11 checks existence)
## Limitations statement    (judgment-class dimensions, no-user-evidence scope,
                             pass scope, assumed dependencies, machine-face
                             boundary, text-face review note when no visual
                             evidence was read, pending-user-adjudication
                             sub-block)
## Verdict                  (exactly one Pass|Recirculate + closure lines)
```

Coverage levels: **exhaustive** (primary path + required rare paths + per-page five-state matrix — no exceptions), **sampled** (edge cases by five-state x page matrix, reasons recorded), **explicit unreviewed** (everything else — never defaults to pass). Unreviewed is not pass: it produces no pass contribution.

Positive findings are the acceptance-evidence face, not decoration: every L6 `pass` on implemented UI requires at least one bound rendered or interaction artifact (measurement/source corroborate but never carry a pass alone); planning-only passes rest on declaration coverage and must not claim a render or test occurred. Missing evidence is `blocked` (unverifiable), never `fail` — "not tested" and "tested and failed" are different facts.

**Done when:** all six blocks are present; the Coverage statement names the exhaustive completion status and the explicit unreviewed list (G11); every pass row cites bound evidence; limitations name the judgment-class dimensions and assumed dependencies.

After Recirculate, use [`references/repair.md`](references/repair.md) for the smallest owning declaration, the R1-R5 second-hop route, and which evidence to invalidate.

The artifact shape behind this verdict is machine-checkable: `scripts/validate_run.py` gates L1-L6, ordered `Given -> When -> Then` in every top-level L6 item, one non-empty four-field evidence row per `L6.<n>`, allowed evidence results, all-pass evidence for `Pass`, four non-empty finding fields, one explicit verdict, and one exact issue-linked closure per blocking finding. These checks are the completion criteria above, not extra prose.

After Recirculate, use [`references/repair.md`](references/repair.md) for the smallest owning declaration and which evidence to invalidate.

## Recirculate map (authoritative)

Single source of truth for the observable -> declaration routing. The orchestrator and other skills point here; do not duplicate it. Routing is two hops: first hop observable -> declaration artifact, second hop declaration artifact -> R1-R5 repair target (see [`references/repair.md`](references/repair.md) for the full second-hop table and the `invalidated:` evidence-set block).

| Observable | Declaration | Second hop (default) |
| --- | --- | --- |
| Happy path only; empty/fail/auth missing | `spec` | R2 (interaction model: five-state / path rows) |
| Wrong business meaning / risk / secrets | `domain` | R1 (requirement subtree reopen when undeclared) |
| AI slop, flat hierarchy, purposeless motion | `craft` | R4 (implementation) |
| New UI visually conflicts with confirmed existing-product baseline | bound `<binding.path>` | R3 (design decision) |
| Scattered hex/px/ms | `design` | R4 |
| Badge↔Tag, Dialog↔Drawer mixups | `components` | R4 |
| Wrong page shell (e.g. list as card wall) | `template` | R4 |
| Desktop app feels like a web page / wrong seam | `native-craft` | R4 |
| Copied third-party brand / Do not copy breach | `reference` (supporting) → fix in Fill / re-intake | R4 |
| Undeclared product requirement / unjudgeable criterion / falsified assumption | re-open `ux-spec` shaping | R1 |
| Capture plan cannot answer the criterion / provider absent | observe* seam | R5 (evidence plan) |
| Critique with no owner | re-run `ui-evaluator` | — |

Fix only the owning layer (minimum owning set; repair order R1→R2→R3→R4, R5 may append after any layer), then resume from the pipeline step that consumes it.

## Guard

Prefer positive fixes in `fix`. Reserve bans for non-negotiables (e.g. open dangerous action without confirm) and always pair with the required behavior.

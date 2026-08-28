# Design-decision entries (DD blocks)

Protocol reference for versioned decision records appended to `decision-report.md`. The top block (scene/density/template/regions/components/baseline-changes/risks) is the Fill consumption face and stays byte-identical; DD entry blocks are its evidence layer, appended after it in the same field-block style as the rule registry. Machine face: `scripts/g10_design_decisions.py` (G10, orchestrated by `validate_run.py`; fires only when DD blocks are present).

## Tiers and triggers

Tiering grades the recording and confirmation duty, never the decision authority — R/C stay agent-decided, and the user may always take any decision personally.

| Tier | Trigger | Decision | Recording duty |
| --- | --- | --- | --- |
| record (R) | single reasonable choice, or local implementation inside confirmed declarations (easy-mix pair resolution, token role assignment, copy tweak, density following the baseline) | agent | minimal entry: selection + one-line rationale + constraint reference |
| compare (C) | 2-3 substantive candidates inside the baseline, no E criterion hit | agent | entry with candidates, comparison axes, trade-off statement, selection + rejection reasons |
| explore (E) | any criterion below | **user** | full entry + user confirmation record |

E-tier criteria (any one hit → E):

1. **Identity drift** — candidate deviates from the bound baseline's declared visual role, atmosphere, density, or motion conventions (cite the DESIGN.md section).
2. **Composition change** — candidate reorganizes the region set or weight allocation, not just filling inside a region.
3. **Upstream route** — a T3 visual-direction open question arrives from shaping (registered there, decided here).
4. **Re-entry** — an R3 finding challenges an existing decision's assumptions or unrecorded trade-offs.
5. **Baseline conflict** — candidate conflicts with the baseline or hard constraints and needs an explicit trade (report `baseline-changes != none`).

Reverse guard: baseline-internal small choices never trigger full exploration — criteria 1/2/5 all bound the decision to identity/composition/conflict. Upgrades mid-comparison (C hitting an E criterion) complete the matrix and add user confirmation; nothing already recorded is discarded.

## Entry schema

```yaml
id: DD-0003                      # run-unique DD-#### ; cross-run refs <run>/DD-0003
tier: explore                    # record | compare | explore
question: <one-line question>
status: confirmed-user           # open | compared | confirmed-agent | confirmed-user
                                 #   | superseded | invalidated
constraints:
  baseline: DESIGN.md sha256:<digest>   # or waived:<reason> (top-block syntax)
  spec: [l1.scenes, l6.c1]
  rules: [PERF-01@1]             # pinned ID@version, cross-checked with the registry
candidates:                      # C/E: 2-3 flow-map items; R: 0-1 (selection line is enough)
  - {id: A, source: agent, created_at: <ts>, fidelity: description,
     summary: <one line>, deviations: none, assets: []}
  - {id: B, source: provider-adapter, adapter: provider-a, created_at: <ts>,
     fidelity: sketch, summary: <one line>, deviations: none,
     assets: [candidates/B.html sha256:<digest>]}
comparison:                      # C/E required; R omits
  axes:
    - {axis: <dimension> (<source ref>), A: <fact/statement>, B: <fact/statement>}
  tradeoffs: "A trades X for Y; B trades P for Q"
selection:
  candidate: B
  rationale: <points back at an axis or the trade-off>
  rejected:                      # C/E required — rejection reasons rank with the rationale
    - {candidate: A, reason: <why rejected>}
confirmation:                    # E required (kind: user); R/C record kind: agent
  kind: user                     # user | agent
  via: preview-round-1 decision_id:<id>   # or report-batch | agent-record
  confirmed_at: <ts>
  decision_log_id: D-0009        # only when projected to the decision log
supersedes: null                 # revision points at the retired entry id
stale: <reason + ts>             # optional: baseline drift marked this entry
stale_review: {exit: keep, note: <review line + new sha256>}  # keep | revise | escalate
```

Rules: ids are zero-padded `DD-####` and never repeat inside a run; flow-map values must not contain ASCII commas or braces (use full-width punctuation in prose); a `- {…}` item may fold onto following lines while its braces stay unbalanced, breaking at a comma — the folded line must end with a comma before the continuation (the schema above shows the fold — single-line items are canonical; a comma-less break or an unterminated fold is a G10 structural error, never a silent merge); comparison cells carry facts and statements with source references — **numeric scores, weighted sums, and ranking points are forbidden**; incommensurable axes get an explicit trade-off line instead.

## Candidates and providers

Candidate sources (equal footing, all recorded): `agent` (drafted against constraints), `provider-adapter` (external generator behind an anonymous adapter handle — `adapter: provider-a`; named products are never recorded), `user` (brought in directly — still goes through the same comparison and record). Every candidate records `source` and `created_at`; a non-empty `deviations` flags possible E-criterion 1/5 and forces a tier re-check. Provider input/output contract is data-shape only: input `{question, constraints{baseline, spec, exclusions}, fidelity, budget}`, output candidate descriptors `{label, summary, constraints_honored, deviations, assets, preference_note}`. Providers produce candidates and artifacts — never evidence, never scores, never a verdict. Candidate assets (sketches/wireframes/prototypes) live in `.scratch/<run>/candidates/` and are referenced as `path + sha256` — a discardable reference layer, never manifest evidence; losing assets loses replay material, never decision provenance.

## Confirmation

E tier is confirmed by the user in batches (≤2 items, 2-3 candidates each, verdict per item: confirm / reject / revise; confirmed items are immutable). When the preview adapter is present and candidates are renderable, confirmation rides the existing transaction: `options` = ordered candidate labels, `report_ref` = this report, `summary` = the question; the entry's `confirmation.via` records `preview-round-<n> decision_id:<id>` — G5 checks the confirm record, G10 checks the linkage. When the adapter is absent the confirmation block records `kind: user` + `report-batch` directly in the entry — the protocol never degrades because an adapter is missing (record the skip in the run-profile skip list). Preview confirms the direction choice, not final implementation acceptance — criteria still belong to L6 evidence and review. Persistent projection is opt-in only: the decision log gets an entry only when the user declares a project convention (`decision_log_id` backfilled); baseline changes go through `design-baseline confirm`, never direct writes.

## Re-entry (R3) and baseline drift

An R3 finding (failed assumption, unrecorded trade-off, baseline conflict) names the challenged entry with an additional field line `dd: DD-0003` (same backward-compatible channel as `rule:` lines). The field rides only non-positive findings (S1-S3): a positive observation records its decision link as prose (e.g. an evidence note line) — `dd:` on an S0 finding is a G10 structural error, never a challenge signal. Revision = a new entry with `supersedes: DD-0003`; the old entry is retired (`status: invalidated`) and stays parsable — history is never rewritten. The minimal invalidation set is the entry + the Fill surface consuming it + the evidence depending on its assumptions (recorded in the point-back `invalidated:` block); re-review runs only that set plus adjacent main path, inside the same run. A revision re-grades by current criteria — a challenged direction is still direction-level, so the revision lands at E tier with a fresh user confirmation (a new preview round when riding, `round_n` incremented). Baseline-conflict revisions have exactly two legal exits: pick a baseline-conforming candidate, or take an explicit `baseline-changes` approval.

When `design-baseline verify` detects source-hash drift, entries citing the old sha are marked `stale: <reason>` and re-reviewed under the new baseline with exactly one exit recorded in `stale_review`: **keep** (review line citing the new sha256 — clears the mark), **revise** (a superseding entry, confirmed per its tier), or **escalate** (the drift returns the question to direction level — user decision). No calendar expiry: stale is triggered by structural events only. G10 checks that drifted entries carry the mark and that every marked entry records a valid exit.

---
name: ui-picker
description: UI shell and component semantics. Use when scaffolding a product page (list, dashboard, settings, detail, agent-admin), or when the wrong template/Badge-Tag pairing is about to be coded.
metadata:
  short-description: "Shell and component decisions"
  author: Bandersnatch0x
  source: https://github.com/Bandersnatch0x/design-playbook/tree/main/packages/design-playbook/skills/ui-picker
---

# ui-picker

Before code: map the job to a **template** (shell) and **component semantics**. Appearance follows meaning.

## Steps

### 1. Density + scene

Choose density (console-tight vs marketing-loose) and scene class (list / detail / settings / dashboard / editor / agent-admin / …).

When a verified `.scratch/<run>/design-baseline/state.json` binds a baseline (`status: ready` from `design_baseline.verify`), read that binding path first (`baseline.path`, usually `DESIGN.md` or `.stitch/DESIGN.md`). It is the project-specific authority for atmosphere, visual roles, density, layout, motion, and component conventions. Preserve it unless the requested change explicitly revises the baseline.

Collect read-only component candidates from two sources, in this order:

1. observed component paths in a verified baseline's `## Component Stylings` declaration, when that declaration exists;
2. non-duplicate paths from run-local `design-baseline/evidence.json` `components`, when that file exists.

Use the second source when it is present, including waived or draft runs that never produced a `status: ready` binding. Those paths remain candidates only — they never substitute for a ready baseline or become design authority.

When `.scratch/<run>/reference/contract.md` exists (ADR-0011), read its **Visual cues for ui-picker**, Keep/Change, and Do not copy / exclusions. Use them as input for density, scene, region weight, and risks — never as hex tokens or as a license to copy brand chrome.

**Done when:** one scene label and one density choice are explicit; a bound baseline is cited by path + SHA-256; if a reference contract exists, the decision report's risks or exclusions surface its Do not copy / brand risks (path citation is enough).

### 2. Template

Read [`references/template.md`](references/template.md). Assign main / side / action / status regions.

**Done when:** each region maps to a duty from `spec` L2 (or a stated gap in the spec).

### 3. Components

Read [`references/components.md`](references/components.md). For each field/action, pick by **role** (status vs category vs confirm vs detail).

For each material role, match the candidate list against source evidence and
repository conventions, then record one of these outcomes inside the existing
`components:` value in the decision report:

- `reuse <path> (<one-line matching reason>)` when the declared component fits;
- `extend <path> (<one-line missing-variant reason>)` when it is close but needs
  a documented variant; or
- `new (<one-line gap reason>)` when no trustworthy candidate exists.

Include the candidate path in the value when one exists. A weak or missing
candidate takes the explicit `new` path; do not invent a reuse claim. Do not
edit, move, publish, extract, or commit a component during this step, and do
not add a new top-level decision-report key or component repository.

Load only if needed:

- business risk / desensitize → [`references/domain.md`](references/domain.md)
- token roles while deciding surfaces → verified `<binding.path>` first, then generic fallback [`references/design.md`](references/design.md)

**Done when:** every primary datum/action has a named component role; each material role records `reuse`, `extend`, or `new` inside `components:` with a candidate path or an explicit gap reason; easy-mix pairs (Badge/Tag, Dialog/Drawer, Dropdown/Menu/Command) are resolved in writing.

### 4. Decision report

Write, then wait for confirmation if the user is in the loop:

```text
design-baseline: <binding.path> sha256:<digest> | waived:<reason>
scene:
density:
template:
regions: …
components: primary-action -> reuse src/ui/Button.tsx (matching primary variant); status -> extend src/ui/Badge.tsx (needs warning state); empty-state -> new (no declared candidate)
baseline-changes: none | <explicitly approved change>
risks: …
```

`baseline-changes: none` tolerates a trailing same-line note (the machine face reads the `none` value token); substantive commentary goes on its own line instead.

**Done when:** the report exists, records the bound baseline or explicit waiver, and coding has not started without it.

#### DD entry blocks (append after the top block)

The top block above is the Fill consumption face and stays byte-identical. When a choice needs a record a reviewer could challenge, append versioned DD entry blocks after it (`## DD-0001 — <question>` + one yaml field block each; format and gate rules in [`references/decisions.md`](references/decisions.md)). Tier every recorded decision first:

| Tier | Trigger | Who decides | Recording duty |
| --- | --- | --- | --- |
| record (R) | single reasonable choice, or local implementation inside confirmed declarations | agent | one-line rationale + constraint reference |
| compare (C) | 2-3 substantive candidates inside the baseline, no E criterion hit | agent | candidates + comparison axes + trade-off + rejection reasons |
| explore (E) | any E criterion: identity drift from the bound baseline, region/weight re-composition, a T3 route from shaping, an R3 challenge, or a baseline conflict | **user** | full entry + user confirmation |

Baseline-internal small choices never trigger full exploration. E-tier confirmation rides the preview transaction when the adapter is present (`options` = candidate labels, `report_ref` = this report; link the entry to the transaction `decision_id`); when absent, record `kind: user` + `report-batch` in the entry's confirmation block. R3 challenges revise by a new entry that `supersedes` the retired one — history is never rewritten. `scripts/g10_design_decisions.py` (G10, via `validate_run.py`) checks the machine face.

### Branch — structure still open

If template is underdetermined, offer 2–3 IA variants (same `spec`, different main-region weight), one-line tradeoff each, pick one, then complete step 4. This branch is the C tier above: record it as a `tier: compare` DD entry instead of leaving the trade-off in conversation only.

## Defaults that hold

- Brand color as token/role, not a hex literal.  
- Easy-mix pair semantics and shell 禁用 rules live in `references/components.md` and `references/template.md` — resolve against those tables, not from memory.

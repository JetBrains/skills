---
name: ux-spec
description: Declaration-first UI spec (six-layer spec.md) shaped through an S0-S6 interactive session. Use when turning a short product/UI ask into six-layer spec.md, or when goal, edge-state, acceptance, or evidence requirements are missing before build.
metadata:
  short-description: "Six-layer UX specification"
  author: Bandersnatch0x
  source: https://github.com/Bandersnatch0x/design-playbook/tree/main/packages/design-playbook/skills/ux-spec
---

# ux-spec

Write a **six-layer `spec.md`**: the functional **declaration** for what must be true. Visual skin, tokens, and Badge-vs-Tag choices are out of scope.

Shaping runs as a session state machine (S0-S6) with append-only artifacts under `.scratch/<run>/shaping/`:

- `shaping-log.jsonl` — append-only event log (process authority; mirrors the decision-log philosophy)
- `queue.json` — derived state (pending questions / staged assumptions / open confirmations), always rebuildable from the log

Events (closed enum): `asked / answered / assumption_staged / confirm_presented / item_confirmed / item_rejected / item_revised / projected / suspended / resumed / superseded_by / archived`.

## Steps

### 0. Bind project contract — S0 intake assembly

When the host project has a persistent contract v1 (`contract.json` + optional `decisions.jsonl`), **bind-first** via `scripts/contract_v1.py` before writing L1–L6. Resurface every `assumed` / `open` field and any source-hash drift; `open` blocks dependent work; `assumed` needs explicit per-run acknowledgement. Reject unknown `schemaVersion` values. Do not invent layered inheritance or partial overrides (ADR-0019). Accepting a run spec may promote fields only as `assumed`/`open` — never as `decided` without named user confirmation in the decision log (ADR-0017).

S0 assembles the session inputs: persistent contract + decision log (bind semantics pre-check), project design baseline state, existing spec, reference contracts — and records the request **verbatim** as the first shaping-log event. Any state may `suspended`/`resumed`; on resume, rebuild `queue.json` from the log and re-ask only `asked`-without-`answered` items — already-`item_confirmed` values are never re-asked (revise only via `supersedes`).

**Done when:** either no project contract exists, or bind-first recorded contract/decision-log SHAs and every unresolved or stale field was surfaced before authoring; the session log exists with the request recorded verbatim.

### 1. Shape requirements — S1-S6 session state machine

**S1 GRADE** — grade gaps on four consequence tiers: T1 consequential (goal / target user / success criteria / non-goals), T2 structural (materially different IA or primary-path alternatives), T3 visual-identity (register and route to the design-decision track; shaping never adjudicates), T4 local (inside confirmed declarations — agent-autonomous, never asked). Produce the question queue (T1 first; ≤3 questions per batch; every question names the downstream field(s)/L6 it changes) and the assumption plan. Questions with no downstream impact are not asked — they become T4 assumptions.

**S2 CLARIFY** — present batches (≤3), user answers; log `asked`/`answered` with each question's impact note. A refused T1 answer downgrades to an **explicit-risk assumption** into CP-C (never a silent `assumed`). After 2 consecutive batches without new T1 information, remaining T2/T3 items must convert to the assumption plan (fatigue cap; session soft cap 9 questions).

**S3 DRAFT** — draft L1-L6 from confirmed values + registered assumptions. A materially different IA / primary-path alternative surfaced while drafting becomes a T2 question back to S2 (max one such jump per drafting round); T4 local choices land directly in the draft. Draft the L2-L5 structured fields (per-page duty table, path table, per-page five-state matrix — see the template).

**S4 CONFIRM** — present confirmation batches: **CP-A** intent (≤4 items: goal / target user / success criteria wording / non-goals), **CP-B** structural (≤2 items, ≤3 options each; chosen value writes an `l2.*` field), **CP-C** assumptions (≤5 items, each with reason + risk + fallback). Per-item confirm / reject / revise — log `confirm_presented` / `item_confirmed` / `item_rejected` / `item_revised`. Rejected items re-enter the S2 queue alone; confirmed items are immutable (revision only via `supersedes`).

**S5 PROJECT** — project in the strict function order: `promote_fields` (registers `assumed` fields only) → `append_decision` per user-confirmed item → `apply_decisions` → write `spec.md` → `bind_first` (produces `contract-bind.json` + the assumed-ack list) → G7 drift check. Log one `projected` event carrying the mapping rows (decision id ↔ contract field ↔ spec section) — this record is what G9 checks.

**S6 EXIT** — all five hold before the session may close: (a) bound subset has zero `open` fields; (b) every `assumed` field has this run's explicit ack; (c) L1 five fields + L6 criteria all valued and traceable (decided or acknowledged-assumed); (d) decisions.jsonl and contract have no unrecorded drift (G7); (e) spec.md has all six layers (G1). Then log `archived`. There is no silent-downgrade exit: either S6 passes or the session suspends with the open queue and reasons saved.

From the ask, fix the user-visible goal, target user, in-scope scenes, **non-goals**, and always/ask/never boundaries. Ask only when a missing answer materially changes one of them; otherwise record a conservative assumption (CP-C visible). When `.scratch/<run>/reference/contract.md` exists (ADR-0011), **read it before writing L1–L6**. Fold its functional constraints, non-goals implied by Do not copy, and always/ask/never hints into L1 (and later L5/L6 edges). Cite the path; do not re-derive the screenshot from memory. The reference contract is input only — it does not replace any L1–L6 heading.

**Done when:** all five L1 fields are explicit — goal, target user, in-scope scenes, non-goals, and always/ask/never boundaries — with each assumption labeled as such; none left blank or implied; if a reference contract exists, its functional constraints are reflected (or an explicit rejected-with-reason note is recorded); and the S6 exit conditions hold with the session archived.

### 2. Expand L2–L4

- L2 regions and duties, plus the **Page duties** table (one owner duty per page)
- L3 states and transitions, plus the **Paths** table (ordered path rows `P1…Pn`)
- L4 control behavior per relevant state

Use the headings in [`references/spec-template.md`](references/spec-template.md).

**Done when:** every primary user job has a state path; every region has an owner duty; every page in the L5 matrix has a duty row.

### 3. Force L5–L6

- L5: empty, loading, error, permission — each with what the user can do next — plus the per-page **five-state matrix** (initial / loading / success / failure / empty enumerable per page)
- L6: checkable acceptance; every top-level item explicitly contains `Given`, then `When`, then `Then`, with the proof required for that item and a `(path: P<n>)` reference into the L3 path table

**L6 granularity (ADR-0016):** one top-level L6 item = one independently blocking **user-visible risk or outcome**. Three to seven items is a soft authoring budget — write more only with an explicit rationale that each extra item is independently blocking. There is **no** numeric validator gate. Accessibility and multi-stack proof attach to an existing criterion when they test the same risk; create a standalone accessibility L6 item only when the failure is independently blocking. Do **not** auto-generate accessibility or multi-stack L6 seeds.

Evidence is criterion-shaped: visible states require rendered inspection at named target viewports; behavior requires an interaction trace or automated check; implementation health uses the relevant tests, type/lint checks, or affected build when available. Planning-only work names the future proof instead of claiming it exists. Where the proof is a runtime state, name the **capture seed** — the state to capture (e.g. "error-state screenshot") and the capture type. This is the seed the `observe*` step derives a capture plan from (`Given`/`When` → state+actions, `Then` → required); do not write selectors, URLs, or actions here — those are derived later. Capture contract v1 requires `schemaVersion: 1` and an explicit viewport at observe time (ADR-0018).

The host model may have no vision (text-only input): render artifacts are bound by **path reference** (manifest + ledger), never by viewing them. Machine assertions for review use the **text face** — HTML/CSS source, `a11y tree` text, interaction-trace JSON. A no-vision run follows this mode end to end; it is not a protocol downgrade, so do not write L6 proof that requires the composing model to look at a screenshot.

**Done when:** L5 is not a single word (“loading”); every L6 item is a top-level list item that uses `Given -> When -> Then` in that order, can be ticked pass/fail without taste debate, and says what evidence will prove it (naming the capture seed where the proof is a runtime state); every L6 item references a reachable path row; L6 items stay user-risk units rather than one row per evidence type.

### 4. Emit

Output the full `spec.md` using the template structure. Stop. Do not scaffold UI or pick components here. Refresh `shaping/queue.json` from the log before stopping.

**Done when:** one markdown spec exists containing every L1–L6 heading from the template, with steps 1–3 Done-when criteria still holding in the emitted file, ready for the next pipeline step (`ui-picker` / fill).

## Scope fence

| In | Out → |
| --- | --- | --- |
| Functional truth, flows, edges, acceptance | Color/type/motion → `design` / `craft-guard` |
| | Risk/secrets meaning → `domain` |
| | Component identity → `ui-picker` / `components` |

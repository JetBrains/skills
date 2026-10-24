---
name: design-playbook
description: Route and orchestrate outcome-first product UI work. Use for answer, review, diagnosis, plan, prototype, build, or fix asks involving a page, dashboard, list, or settings surface, and for recirculating failed design review through declarations and evidence.
metadata:
  short-description: "Design I/O pipeline orchestrator"
  author: Bandersnatch0x
  source: https://github.com/Bandersnatch0x/design-playbook/tree/main/packages/design-playbook/skills/design-playbook
---

# design-playbook

**Design I/O** — same process every run: inject **declarations** (what good is), run **contracts** (how work enters the pipeline), **recirculate** failures to the declaration that owns them.

Not a style library. For palettes/type catalogs use other packs; here the product pipeline and acceptance are the product.

## Run contract

Keep each control in one authoritative place:

| Control | Single source | Required content |
| --- | --- | --- |
| **Goal** | `spec` L1 | User-visible outcome, target user and scene, non-goals |
| **Success** | `spec` L6 | Observable pass/fail criteria; every top-level L6 item is `Given -> When -> Then` |
| **Evidence** | `spec` L6 + evaluator ledger | Exactly one `L6.<n>` ledger row per criterion; planning-only uses declaration coverage, implementation uses rendered states, interaction/test results, and applicable code checks |
| **Stop** | this orchestrator | Pass; smallest missing decision; unavailable required evidence or authority; repeated blocker |
| **Confirm** | this orchestrator + user decision | Any consequential action not already authorized |
| **Tier** | `plan.md` run-profile block | Tier (P1/P2/P3) + grading checklist + skip list + upgrade events |

## Run profile (tier grading)

Apply this section only after entry routing returns `design-run`; `no-run`
creates no profile or run artifacts.

Project the router's initial tier and criteria once, up front (LR1). Three tiers share one state machine and one artifact set — the tier only changes how deep each step goes:

- **P1 point-fix** — bind fast path + assumed acknowledgement; no design-decision entry or shaping session; R2 row-level spec additions remain allowed.
- **P2 standard** — full `ux-spec` shaping session (S0-S6, G9), R/C-tier design decisions, and standard evidence/review obligations.
- **P3 full** — full declaration, alternative, interaction, and applicability coverage.

The executable router proposes the initial grade and the user confirms it **once** (may fold into the request reply). **Upgrade is automatic** the moment a correction signal appears (R1 finding, structural R2, cross-layer blocking, E-tier judgment) — record the upgrade event in the run-profile block and walk the added steps; **downgrade requires the user** (over-compliance already performed is kept). Write the block into `plan.md` as a structured field block (`tier: P1|P2|P3`, grading checklist, `confirmed_by: user + <ts>`, skip list with one-line reasons, upgrade events). The profile block is mandatory for every run — skipping the rest of the plan body is legal, skipping the profile block is not. Every skipped step keeps the one-line skip narration rule below. Audit-preference tier waivers follow the SSOT section below.

On a `design-run`, ask the smallest question only when the answer changes the goal, scope, platform, success criteria, or authority; otherwise record a conservative assumption in L1. Whether a request is `no-run` or `design-run` is only the router decision from step 1 — including durable review, diagnosis, or plan work.

Pause for explicit confirmation before an external, destructive, costly, or scope-expanding action that the request did not already authorize. This includes adding a dependency, changing an API/backend/data contract, deploying or publishing, and accepting a blocking finding. When required evidence or authority is unavailable, stop with the exact blocker and the smallest next decision. If the same blocking finding survives two repair -> re-evaluate cycles without new evidence, stop recirculating and report it.

## Audit preferences (ADR-0033)

The audit/acceptance stages — `craft-guard`, `observe*`, `ui-evaluator` — are user-selectable. Fill and the preview confirmation (ADR-0008 floor) never are. Preferences are execution trimming **after** routing: `run_profile.py route` receives no preference input and this section never feeds it.

**Read (one deep module — consume its output, never reconstruct its precedence):**

```
python <plugin>/scripts/audit_preferences.py plan --repo-root <target-repo> [--declaration '{"craft_guard": false}']
```

The printed payload is the only trimming authority: `stages.<stage>.runs` decides whether the stage executes, `source` (`default` / `local` / `repo` / `run`) narrates where the choice came from, and `asked` projects the merged asked bit. Each `invalid_files` entry names one corrupt layer that was treated as absent; decide whether to ask from `asked`, since another valid layer may still preserve `asked: true`. A run declaration (this run's user statement, natural language mapped to the three booleans) is passed via `--declaration` and outranks both preference files.

**First ask (folded into tier confirmation):** when the payload reports `asked: false`, owe the user the one-time audit-scope question — merge it into the tier-confirmation exchange above so the run suffers one interruption, not two. Collect the `craft_guard` / `observe` / `ui_evaluator` choices together with the tier confirmation; they become the run declaration for this run. Repository values are team-authored input, not proof of this user's choice: when any effective stage has `source: repo` and audit scope has not yet been confirmed in this session, include one confirmation of those stored choices in the same exchange and pass the answer as the run declaration. Never silently treat repository `asked: true` as current-user consent.

**Write-back (remember the answer):** persist the answered declaration through the module — `write_back(repo_root, declaration, scope=..., this_run_only=...)` from `design_playbook.scripts.audit_preferences` (bootstrap: `sys.path.insert(0, '<plugin>')`). Scope `repo` writes `.design-playbook/preferences.yaml` (team-shared default, version-controlled); scope `local` writes `.design-playbook/preferences.local.yaml` and automatically ensures `.design-playbook/preferences.local.yaml` is listed in the target repository's `.gitignore`. When the user says "this run only", pass `this_run_only=True`: the choice applies to this run but is not persisted, and the asked bit is still consumed. The write-back also sets the asked bit, so the first-use question is not repeated; repository-sourced choices still receive the per-session confirmation above.

**Trim + skip-list recording:** for every stage whose `runs` is false, skip the step and record it in the run-profile skip list with a one-line reason naming the source (e.g. `craft-guard: skipped by user audit preference (source: run)`) — silent skips are illegal, and the one-line skip narration rule still applies. The run artifacts carry a limitation statement naming what was not audited: absence of evidence is never presented as evidence.

**Skeleton point-back (`ui-evaluator` skipped):** `point-back.md` is a machine hard dependency, so a skipped audit still emits one — generated by the module's `skeleton_pointback(spec_text)`, marked `audited: false` with a fixed limitation sentence, e.g.:

```
python -c "import sys; sys.path.insert(0, '<plugin>'); from pathlib import Path; from design_playbook.scripts.audit_preferences import skeleton_pointback; run = Path('.scratch/<run>'); (run / 'point-back.md').write_text(skeleton_pointback((run / 'spec.md').read_text(encoding='utf-8')), encoding='utf-8')"
```

Never edit out the `audited: false` marker or upgrade the skeleton's verdict: `validate_run --strict` / `--require-evidence` / `--require-coverage` reject skeleton runs, `run_status` projects *not audited*, and `aggregate_runs` surfaces them as unaudited (ADR-0033 D12). Optionality is a convenience for honest users, not a forgery channel.

**Tier-obligation waiver:** an explicit user skip of `craft-guard` authorizes the corresponding P2/P3 obligation downgrade (full-catalog evaluation, G11 sampling matrix) automatically; the waiver is recorded in the skip list (ADR-0033 D8). The "downgrade needs the user" rule guards against agent self-demotion — the user's own declaration is the authorization.

## Steps

> **Stage-list mirror:** packaged `scripts/stages.py` -> `STAGES` mirrors this section's steps and artifact filenames for status/resume narration. `tests/test_stages_registry.py` holds the two in lockstep — when a step or artifact filename changes here, update `STAGES` until that test is green.

Do in order. Data flow:

`design-baseline? → reference-intake? → ux-spec? → plan? → (native-craft?) → ui-picker → (preview*) → fill → craft-guard → (observe*) → ui-evaluator`

- `?` = conditional entry/route
- `*` = run only when the matching MCP tool is available (`preview_prototype` for preview, `execute_capture_plan` for observe); otherwise skip
- `craft-guard`, `observe*`, and `ui-evaluator` are additionally user-selectable via **Audit preferences** (ADR-0033); trimming there never changes this order
- When you skip a step, say so in one line — step name + reason + how to enable, with the gate label when one applies. Matters most for `preview*`/`observe*` adapter absence, e.g. `-> preview*: adapter absent, skipped (G5 not triggered; enable via packages/design-playbook/mcp/preview/ or host MCP)`. Other conditional skips may use the same shape; entry lines are optional (keep output lean). Narration only — not a run-contract control.
- Do not code a pretty shell until the active step’s completion criterion is met

### 1. Entry routing

**Executable routing authority:**
`<plugin>/scripts/run_profile.py route`. This skill owns fact
normalization and orchestration; `commands/design-io.md` only invokes it.

Normalize the request and repository facts, call `run_profile.py route`, and
keep its returned `mode`, tier, criteria, and prerequisite flags as the only
initial route decision.

- `no-run`: respond directly. Do not create `.scratch/<run>/`, `plan.md`, or a
  run-profile. A vision-capable host may inspect an attached image directly;
  a text-only host records the metadata and states that visual inspection is
  unavailable once, then — if the user supplied **only an image without
  accompanying text** — must **ask the user for a short written description**
  before finishing the response and continue with that description. Stating
  the limitation once is not a stop condition when text material is missing.
  Neither path copies the temporary image.
- `design-run`: project the returned tier and criteria into the existing v1
  run-profile, then satisfy the independently returned prerequisites.

Treat a router error as a stop and correct the normalized facts; do not
reconstruct its decision table in prose. The router does not decide later
preview/observe adapter availability or evaluator verdicts.

- `requires_baseline` → step **1A. `design-baseline`**; otherwise skip it.
- `requires_reference_contract` → step **2. `reference-intake`**; otherwise skip it.
- `requires_spec` → step **3. `ux-spec`**; otherwise continue to step **4. plan**.

**Done when:** `run_profile.py route` returned a decision that this skill consumed without reconstructing its table; `no-run` created no `.scratch/<run>/` artifacts; `design-run` projected the returned tier, criteria, and prerequisite flags.

### 1A. `design-baseline` (when required)

Invoke **design-baseline** before reference intake or specification work. Call the deep module `prepare` → (user confirm/waive) → `confirm` as needed; immediately before Fill call `verify`. Discover and validate project `DESIGN.md`; if missing or incomplete, generate only run-local `DESIGN.draft.md` + `evidence.json` from first-party UI evidence and wait for confirmation before a durable write.

Gate artifact is only `.scratch/<run>/design-baseline/state.json` (`schema: design-baseline/v1`). A valid existing baseline becomes `status: ready` with `decision.kind: existing`. A generated draft requires `confirm(..., "accept")` (`ready` + `accepted`) or explicit `confirm(..., "waive", reason=...)` (`waived`). `needs_confirmation` and `ambiguous` block Fill. A draft alone is not authority.

**Done when:** **design-baseline**'s own completion criteria hold (that skill is SSOT). Smoke: `state.json` exists; candidate conflicts and stale source hashes are exposed by prepare/verify; status is `ready` or `waived`; no valid baseline was silently replaced.

### 2. `reference-intake` (when `requires_reference_contract`)

Invoke **reference-intake**. Produce `.scratch/<run>/reference/contract.md` + `reference/manifest.json` (ADR-0011).

**Done when:** **reference-intake**'s own completion criteria hold (that skill is SSOT). Smoke: sources inventoried; observed vs inferred labeled; Keep/Change/Do not copy present for product-analogy, third-party URL, **and third-party screenshot/design**; license/brand risks recorded. Does not write `spec.md` or a decision report.

Then continue to **3. `ux-spec`** (or **4. plan** when spec already exists).

### 3. `ux-spec` (when spec is missing)

Invoke **ux-spec**. Produce six-layer `spec.md` with the L2-L5 structured field blocks. For P2/P3 runs the skill runs as a shaping session (S0-S6 with CP batches; append-only artifacts under `.scratch/<run>/shaping/` — `shaping-log.jsonl` + derived `queue.json`; G9 gates the session exit). P1 runs skip the session and use the bind fast path. When `.scratch/<run>/reference/contract.md` exists, **ux-spec** must read it first (functional constraints, non-goals, always/ask/never) — do not wait for plan.

**Done when:** **ux-spec**'s own completion criteria hold (that skill is SSOT). Smoke: L1–L6 present; L5 substantive, not "show loading"; every top-level L6 item uses ordered `Given -> When -> Then`, names its evidence, and names the capture seed where the proof is a runtime state; reference constraints folded when a contract exists.

Then continue to **4. plan**.

### 4. plan (pipeline step — pure orchestration)

Not a run-contract control beyond the **Tier** row and **not** a machine gate. Does not become Goal / Success / Evidence / Stop / Confirm SSOT.

Write a light handoff at `.scratch/<run>/plan.md` (required on disk). It **must open with the `run-profile` structured block** (see *Run profile* above): `tier: P1|P2|P3`, the grading checklist, `confirmed_by: user + <ts>`, the skip list (step + one-line reason; silent skips are illegal), and upgrade events. Skipping the rest of the plan body is legal; skipping the profile block is not.

When `.scratch/<run>/reference/contract.md` exists, the handoff must point to it (path only; do not paste the full contract) and fold its functional constraints into the description→spec map and its visual cues/exclusions into the ui-picker input pack. Minimum three blocks:

1. **本次 run 范围** — pointers to L2 / scenes / non-goals (do not copy L1–L6 wholesale)
2. **用户描述 → spec 映射** — which L1/L2/L6 this ask touches; unmapped items → conservative assumptions
3. **ui-picker 输入包** — scene hints, constraints, explicit exclusions

**禁止:** paste the full spec; pre-write a decision report inside plan.

**描述 × spec 分轨:**

- **Structural conflict** (L1 outcome, L6 criteria, platform/permission/data contract, overturned non-goal) → stop; revise `ux-spec` or user Confirm of an exception recorded in plan
- **Presentation preference** (scene density, region weight, component role preference without L6 change) → put in the ui-picker input pack; `ui-picker` decides
- **Unmapped description** → mapping table as conservative assumption; do not silently edit L1

**Done when:** `plan.md` exists with the three blocks; ui-picker can consume the input pack without re-deriving scope from chat.

### 5. Shell → conditional `native-craft` → `ui-picker`

Native desktop order: `ux-spec` → `native-craft` → `ui-picker` → `fill` → `craft-guard` → `ui-evaluator`. (Conditional entry `?` and optional adapters `preview*`/`observe*` are shown in the full sequence above, step 0.)

- Invoke **native-craft** only for an explicit native-desktop target or a request for native-feel. Web and mobile Web skip `native-craft`; a Web UI that merely resembles a desktop admin tool is still Web.
- If the target platform is unclear, ask once before choosing the route. Do not assume native desktop.
- For native desktop, require the native decision gate and render-surface seam before invoking **ui-picker**. If `native-craft` cannot load or does not produce them, stop and report what is missing; do not silently choose a Web shell.
- Pass the decision gate and seam to **ui-picker** as required shell context. The caller does not reconstruct or reinterpret them.

Invoke **ui-picker**. Map scene → template + component semantics. Read its `references/` only as that skill directs. When `.scratch/<run>/reference/contract.md` exists, pass its visual cues / exclusions (via plan input pack and/or direct path) into **ui-picker**.

**Done when:** **ui-picker**'s own completion criteria hold (that skill is SSOT). Smoke: the decision report names scene, density, template, regions, components, and risks; coding has not started before that report exists. For native desktop, it also consumes the declared render-surface seam.

`ui-picker` **stops at the decision report** — it has no preview step.

### 6. preview* (optional external MCP adapter)

After the decision report exists, probe MCP `tools/list` for **`preview_prototype`**. Load operating detail only when present: [`references/load-map.md`](references/load-map.md) + [`references/preview-ops.md`](references/preview-ops.md).

- **Absent** → skip preview; go to Fill. Narrate step + reason + enable path (G5 not triggered).
- **Present** → follow `preview-ops.md` (prototype → HITL → floor → confirm). Proceed to Fill only with `confirmed=true` and `floor_pass=true`.

**Native desktop:** still run Web preview when the adapter exists; coverage is **render-surface seam and above** only. Note that limitation once in `preview/log.md`. Do not skip preview solely because the route is native (skip only when the adapter is missing).

**Hard boundary:** never copy `preview/round-*.html`, preview-only assets, fake data shells, **`reference/assets/`**, or **`reference/example.html`** (or any other file under `.scratch/<run>/reference/`) into the Fill source tree. Fill consumes report + `spec` semantics, not prototype or reference media files.

**Re-Fill signal (preview after Fill already exists):** if a Fill surface (code under the host tree or `filled-ui.*`) already exists and a later preview round **revises the decision report** (new round, structural/component change absorbed into `decision-report.md`), you **must re-Fill** (or explicitly record user acceptance that the existing Fill already matches the new report) **before** observe* / ui-evaluator. Do not run observe against a Fill that predates the current confirmed report. Log the re-Fill (or acceptance) once in `preview/log.md`.

**Done when:** either preview was skipped (no adapter), or a `confirm-round-*.json` with `confirmed: true` **and `floor_pass: true`** matches the current decision report (G5 when `validate_run.py` is given `--preview-dir` / `--decision-report`). A confirmed record without `floor_pass` fails G5 — empty/garbage feedback is a silent false-pass that must not reach Fill. When Fill already existed, the re-Fill signal above is satisfied.

### 7. Fill

Implement structure from the decision report + `spec` + confirmed project `DESIGN.md` when bound. Prefer project tokens: visual values via `var(--*)`; missing tokens → `gaps.log` (or project equivalent), not raw hex/px/ms.

**Hard boundary (design baseline):** for existing-product UI work, do not enter Fill unless `design_baseline.verify(project_root, run_root)` succeeds with `status` `ready` (bound path + sha256) or `waived` (non-empty reason). A draft alone is not authority.

**Hard boundary (reference):** never copy `.scratch/<run>/reference/assets/`, `reference/example.html`, or third-party brand media inventoried by reference-intake into the host Fill tree. Honor Do not copy via report + `spec` only.

If a reused host component conflicts with spec L5, record the conflict and recirculate to `spec` via the authoritative map in `ui-evaluator` before choosing a minimal patch or explicit acceptance.

**Fill artifact location:** the Fill surface may live in the host tree (product side) instead of the run root; a run-root Fill lands as `filled-ui.html` + `filled-ui.md`. When the surface lives in the host tree, register the path(s) in `plan.md` as `fill: <path>` field lines (one per line; run-root-relative or host-project-relative; unfenced column-0 lines — fenced example/prose blocks are never read as declarations) — `run-status` judges the fill stage on those declared paths in addition to `filled-ui.html` / `filled-ui.md` in the run root. An out-of-run Fill surface with no registered path leaves the fill stage unchecked.

Load on demand (only if the fill needs them):

- domain / risk / sensitive fields → `ui-picker/references/domain.md`
- token roles / gaps → `ui-picker/references/design.md`
- component pairs → `ui-picker/references/components.md`

**Done when:** with a codebase — main flow renders and every L5 state named in the spec has a concrete UI path (not a blank region); planning-only — every L5 state has a named concrete UI landing, no blank region.

### 8. Craft → `craft-guard`

When the audit-preferences plan reports `craft_guard.runs: false`, skip this step: record the skip-list reason (source from the plan payload), keep the one-line skip narration, and carry the waiver into the limitation statement (see *Audit preferences*). Otherwise:

Invoke **craft-guard**. Apply loading tiers, motion purpose, hierarchy, CJK type. Craft rules live in the first-party registry (`references/rules.md`): evaluate each entry's applicability predicate (P1: touch-surface subset; P2/P3: full catalog) and write seven-column audit rows to `.scratch/<run>/craft-guard.md`. For native desktop, `craft-guard` owns shared UI above the render-surface seam and defers to `native-craft` below it. If a finding crosses the seam, split it into separate point-backs to the owning declarations.

**Done when:** **craft-guard**'s own completion criteria hold (that skill is SSOT). Smoke: every wait/fail path maps to a loading tier; every animation states its purpose; L4 interactive-zone affordance resolved; residual issues handed to `ui-evaluator` with source `craft`.

### 9. observe* (optional external MCP adapter)

After craft, probe MCP `tools/list` for **`execute_capture_plan`**.

- **Skipped by audit preference** (`observe.runs: false` in the audit-preferences plan) → skip exactly like adapter absence; narrate step + reason (`user audit preference, source: <source>`) and record it in the skip list.
- **Absent** → skip; `ui-evaluator` ledger `observed` stays free-text (current behavior). G6 not triggered.
- **Present** → for each L6 criterion whose proof is a runtime state, run the evidence loop in this orchestrator (not inside any skill):
  1. **Derive** a capture plan from L6 `Given -> When -> Then` (in memory, not on disk): `Given`/`When` → `state` + `actions`; `Then` → required proof (already in the ledger `required` field). Do not add or remove verification intent; L6 wins on conflict.
  2. **Execute**: call `execute_capture_plan` under **capture contract v1** (ADR-0018): required `schemaVersion: 1`, explicit `viewport` (`width`, `height`, `devicePixelRatio`, `colorScheme`), plus `url`, `type`, `state`, `actions`, `artifact_path`. Optional `freeze` defaults to `{enabled: true, waitFonts: true, networkIdle: false}` — freeze is on by default in observe*. Missing/unknown schema versions fail closed with a recapture instruction; there is no dual-read for unversioned evidence. The provider returns `{artifact, observed_state, result, error, written_path, request}` and never sees the criterion. Prefer `written_path` (absolute) when locating the file; if it points outside `.scratch/<run>/`, fix `DESIGN_PLAYBOOK_RUN_ROOT` / cwd before binding. `artifact_path` must start with `evidence/` (e.g., `evidence/empty-state.png`, not `empty-state.png`) — the provider resolves it under `<run_root>/evidence/` and refuses absolute paths, `..` segments, or anything that escapes that subtree (`mcp/evidence/server.py` `_resolve_artifact_path`); a bare filename is rejected because it would land outside the evidence subtree. **Async-init timing**: when the page has an async init (skeleton/loading before `body[data-state]` reaches the target state), include a `wait_for_state` action for that state before the capture action. A capture that lands mid-init records the loading state honestly (`observed_state: loading`), which proves the wrong criterion (dogfood 2026-08-01 settings run).
  3. **Bind** (orchestrator owns the manifest; provider never writes it). After **each** successful or failed capture, **immediately append** one line to `.scratch/<run>/evidence/manifest.jsonl` — do not batch-rewrite the file at the end. Rules:
     - **`observed_state` / `result` / `error`**: copy the provider return **verbatim**. If the provider returns `unknown`, write `unknown` — never overwrite with the requested `state` (request intent lives only under `capture.state`).
     - **Embedded capture snapshot**: store the full call parameters used (`schemaVersion`, `viewport`, `freeze`, `url` including query string, `type`, `state`, `actions`, `artifact_path`) and echo the provider `request` object. Omit nothing that would be needed to re-run the capture.
     - **`ts`**: wall-clock of **this** capture's completion (ISO-8601). Distinct captures must not share one batch timestamp.
     - Also record: criterion ref, `artifact` (run-root-relative), optional `artifact_sha256`, optional `written_path` from the provider.
  4. **Manual provider**: when no ecosystem provider is present but a human operates + screenshots to `artifact_path`, write the same-format manifest entry (`capture.provider: "manual"`) **including schemaVersion=1 and viewport**; `observed_state` is what the human actually saw, not the planned label.
- v1 capture types: `screenshot` / `a11y tree` / `interaction trace`.

**Capture surface (url choice — honesty, not a machine gate):**

1. **Prefer the live host** — running Fill surface (dev server route / real app URL) that implements the decision report.
2. **Semantic mirror** (static HTML / fixture that only *looks like* Fill) is allowed only when the live host is unavailable or unsafe. Then **all** of:
   - every manifest entry's capture snapshot includes `note` (or equivalent) with **`surface: mirror`** and a one-line reason;
   - **ui-evaluator** must emit a finding (severity at least **low**) that observe used a mirror, `source` = `observe* seam` (or preview*/observe* seam), and the fix is "re-capture on live host when available";
   - do **not** claim G6/process Pass as proof that the Fill tree was runtime-verified.
3. **Mirror `data-state` (recommended):** when using a semantic mirror, set the page state the provider can read so `observed_state` is not forced to `unknown`. The evidence adapter reads `body[data-state]` or `[data-state]` (see `mcp/evidence/server.py` `_read_observed_state`). Example:

   ```html
   <body data-state="empty">
     <!-- empty-state UI for L6 empty criterion -->
   </body>
   ```

   Prefer one root marker that matches the capture plan's `state` intent. Still **never invent** `observed_state` in the manifest — copy the provider return verbatim (unknown stays unknown).

Evidence is captured, not judged — copy provider returns verbatim here; `pass`/`fail` authority is the evaluator's (step 9 / `ui-evaluator`). Full authority model (three ledgers: spec names what to prove, manifest what happened, evaluator what it means): SSOT `ui-evaluator` step 2.

**Done when:** either observe was skipped (no provider, ledger `observed` free-text), or each runtime-proven criterion has a manifest entry whose artifact exists (G6 when `validate_run.py` is given `--evidence-dir`); and if any capture used a mirror surface, the point-back includes the required mirror finding.

### 10. Accept → `ui-evaluator`

When the audit-preferences plan reports `ui_evaluator.runs: false`, do not invoke **ui-evaluator**: emit the module-generated skeleton `point-back.md` (`audited: false`, see *Audit preferences*) so the machine chain never breaks, record the skip in the skip list, and show the run artifact index below as usual. `run_status` projects *not audited* for such a run; never present the skeleton as an audit result.

Otherwise invoke **ui-evaluator**. Issues must **point back** to a declaration. The report is the six-block `point-back.md` (ledger / findings / positive findings / coverage statement / limitations statement / verdict); recirculated blockers route through the two-hop map (declaration artifact -> R1-R5) and record the `invalidated:` evidence set.

**Done when:** either the audit ran — the report includes the criterion-shaped evidence ledger (`criterion / required / observed / result`) and findings as `issue / source / fix / severity`, and the authoritative verdict completion criterion in `ui-evaluator` is met — or the audit was skipped by preference and the skeleton point-back (`audited: false`) replaced the report; **and** you show the user a short **run artifact index** (paths under `.scratch/<run>/`) so declaration products are discoverable — at minimum: `design-baseline/` (if triggered), `reference/` (if any), `spec.md`, `plan.md`, `decision-report.md`, `preview/` (if any), `shaping/` (if a session ran), Fill surface path, `evidence/` (if any), `point-back.md`. One block is enough; do not only leave paths buried in tool logs.

Cross-run review of multiple `.scratch/<run>/` runs lives in command **run-review** (cross-run, not a step of this run).

Machine seam (optional local check): `python scripts/validate_run.py <spec.md> <point-back.md> [--preview-dir <preview/>] [--decision-report <report>] [--evidence-dir <evidence/>] [--run-root <run>]`.

### 11. Static run handoff (optional, on request)

Build the delivery credential only when the user asks to hand this run off to implementation. It is **not** a pipeline obligation and never gates acceptance — the authoritative verdict is `point-back.md`.

```bash
python -c "from design_playbook.mcp.evidence.handoff import build_static_handoff; \
  from pathlib import Path; \
  r = build_static_handoff(Path('.scratch/<run>'), Path('<fill-surface>'), round_n=<n>, summary='<one line>'); \
  print(r.index_html)"
```

The builder reads durable run artifacts only — its lifetime is independent of any review round, so it needs no review server, no browser session, and no port (ADR-0034). Everything lands under `.scratch/<run>/evidence/static-handoff/`: `index.html` (the delivery page, package-owned and CDN-free), `disclosure-review.json`, `static-handoff.zip`, `snapshots/`.

Three honesty rules the builder enforces — do not paper over any of them:

- **`confirmed` comes from `confirm-round-*.json`**, never from a choice label or your own reasoning. No record → `confirmationSource: unsubstantiated` and the verdict stays `Pending`. Report that as-is.
- **Capture targets the Fill surface itself**, not the review shell. A zero measured dimension marks the viewport `blocked` and blocks the verdict.
- **An untriggered conditional gate is `not-applicable`, not passed.** Delivery reads "every gate `pass` or `not-applicable`" — never an "8/8" count.

**Done when:** the user asked for a handoff; `index.html` exists under the run tree; and you reported `verdict` / `authority` / `confirmationSource` verbatim, including a `Pending` one.

## Recirculate

When a finding has no owner or you need the observable -> declaration routing, use the **authoritative recirculate map in `ui-evaluator`** (do not duplicate it here). Fix only the owning layer, then resume from the step that consumes it.

## Contracts on this pipeline

| Contract | Skill |
| --- | --- |
| Discover/validate/generate the project visual baseline | `design-baseline` |
| Reference intake (observed/inferred + Keep/Change/Do not copy) | `reference-intake` |
| Write the functional declaration | `ux-spec` |
| Choose shell + component meaning | `ui-picker` |
| Craft / feedback quality | `craft-guard` |
| Native-feel desktop declaration (render seam + conventions) | `native-craft` |
| Acceptance + point-back critique | `ui-evaluator` |

`plan`, `preview*`, and `observe*` are orchestrator steps (plus optional external MCPs for preview and observe), not rows in this table. `design-baseline?` is a conditional existing-product orchestrator gate (ADR-0012); `reference-intake?` is a conditional skill step (ADR-0011), not a machine gate.

Greenfield first-run route and pause table: [`references/first-run.md`](references/first-run.md).

Slash (installed plugin, namespaced): `/design-playbook:design-io` · `/design-playbook:ux-spec` · `/design-playbook:ui-review` · `/design-playbook:run-status` · `/design-playbook:doctor`.
With `claude --plugin-dir` the same command files apply under the plugin namespace.

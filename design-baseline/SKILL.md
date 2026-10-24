---
name: design-baseline
description: Discover, validate, or draft a project-owned DESIGN.md before adding or revising UI in an existing frontend. Use during design-playbook initialization when a repository already has pages, components, themes, tokens, or styles; when new UI looks inconsistent with existing surfaces; when DESIGN.md may be missing, incomplete, conflicting, or stale; or when the user asks to generate a design system from existing frontend source. Produces a provenance-backed draft and requires confirmation before creating or replacing the durable baseline.
metadata:
  short-description: "Project design baseline discovery"
  author: Bandersnatch0x
  source: https://github.com/Bandersnatch0x/design-playbook/tree/main/packages/design-playbook/skills/design-baseline
---

# design-baseline

Establish one project-owned visual authority before UI decisions or Fill. Treat existing code and rendered pages as evidence, not as permission to silently rewrite `DESIGN.md`.

## Authority boundary

| This skill owns | Does not own |
| --- | --- |
| Discovering and structurally validating project `DESIGN.md` | Functional success criteria (`spec` L1–L6) |
| Extracting a draft from first-party frontend source | Third-party reference Keep/Change/Do not copy (`reference-intake`) |
| Source hashes, observed/inferred labels, confidence, unresolved gaps | Component/template selection (`ui-picker`) |
| Confirmation before a durable baseline write | Pass/Fail verdict (`ui-evaluator`) |

Canonical authority is `<project-root>/DESIGN.md`. Accept `.stitch/DESIGN.md` as a compatibility candidate only. If both exist with different content, stop for an explicit user choice; never merge them silently.

## Deep module (SSOT for deterministic work)

All path resolution, scanning, drafting, hashing, durable write, and re-verification live in one module:

[`scripts/design_baseline.py`](scripts/design_baseline.py)

Public interface:

```python
prepare(project_root, run_root) -> state
confirm(project_root, run_root, decision, reason=None) -> state
verify(project_root, run_root) -> state
```

CLI:

```text
python scripts/design_baseline.py prepare <project_root> <run_root>
python scripts/design_baseline.py confirm <project_root> <run_root> --decision accept|waive [--reason ...]
python scripts/design_baseline.py verify  <project_root> <run_root>
```

State is a cache, not authority. Every public call resolves paths against the supplied project root. `verify` re-hashes the bound baseline and its first-party sources before returning a downstream binding. Fill and other consumers may only use a binding that just passed `verify`.

| `status` | Meaning |
| --- | --- |
| `ready` | Bound baseline (`decision.kind` = `existing` or `accepted`) |
| `needs_confirmation` | Provenance-backed draft awaits accept/waive |
| `waived` | Explicit user waiver with non-empty reason |
| `ambiguous` | Conflicting candidates; human choice required |

## Workflow

### 1. Classify the project

Apply the existing-product gate when the orchestrator returned `requires_baseline`, including durable review, diagnosis, or plan work on an existing product. Skip when entry routing returned `no-run` or did not set `requires_baseline`. Do not independently reclassify the repository as greenfield to skip after routing. Narrate the skip in one line.

**Done when:** the run records `existing-product` or `greenfield`, with the file signals used for that classification.

### 2. Prepare (`prepare`)

Run `prepare(project_root, run_root)`. Deterministic code:

- discovers `DESIGN.md` / `.stitch/DESIGN.md` (rejects escaping symlinks);
- validates a complete existing baseline in place;
- or scans first-party theme/token/style/component/page sources, writes `evidence.json` + `DESIGN.draft.md`, and returns `needs_confirmation`;
- writes `.scratch/<run>/design-baseline/state.json` (`schema: design-baseline/v1`).

Agent work after prepare:

- if `status` is `ready` → cite path + sha256 and continue;
- if `ambiguous` → stop for the smallest user decision; never invent a third authority;
- if `needs_confirmation` → review the draft; optionally enrich only material claims with `[inferred confidence=…]` **in the draft file**, then re-run prepare if structure/sources changed (do not hand-edit hashes).

Never write or overwrite project `DESIGN.md` in this step.

**Done when:** `state.json` exists with one of the four statuses above; drafts carry source paths + SHA-256 and observed/inferred labels.

### 3. Confirm or waive (`confirm`)

Show a compact summary: atmosphere, core tokens, typography, layout, primitives, conflicting evidence, inferred claims. Ask before the durable write.

- **Accept:** `confirm(..., decision="accept")` atomically writes canonical `<project-root>/DESIGN.md` from the bound draft and returns a `ready` state.
- **Waive:** `confirm(..., decision="waive", reason=<user reason>)` does not write `DESIGN.md`. Existing-product Fill may continue only after this explicit waiver.
- **Revise:** edit only the draft (or fix sources), then `prepare` again.

Never infer acceptance from silence. Never replace a valid baseline merely because extraction found different implementation details; report the drift for a decision.

**Done when:** `state.json` is `ready` (existing or accepted) or `waived` with a non-empty reason.

### 4. Verify before Fill (`verify`)

Immediately before Fill (and any time a consumer needs a binding), call `verify(project_root, run_root)`.

- Re-checks path containment, baseline hash, source freshness, and provenance alignment.
- Rejects forged `state.json`, stale sources, candidate conflicts, and symlink escape.
- On success, returns the binding: baseline path + sha256 (or an explicit waiver).

Downstream may only consume this verified result — not a hand-edited confirm file and not a draft.

**Done when:** `verify` returns without error; the decision report can cite `design-baseline: <path> sha256:<digest>` or `waived:<reason>`.

### 5. Bind downstream consumers

| Consumer | Required behavior |
| --- | --- |
| `ui-picker` | Cite the verified baseline in the decision report; preserve visual roles, density, layout, and component conventions unless a declared change is approved |
| Fill | Use project tokens and primitives; log missing roles instead of inventing raw values; gate on successful `verify` |
| `craft-guard` | Treat obvious baseline drift as a craft failure, not merely personal taste |
| `ui-evaluator` | Point observable drift back to `DESIGN.md`; the baseline is supporting declaration evidence, never L6 runtime proof by itself |

Third-party or sample `DESIGN.md` files remain `reference-intake` inputs. They never become project authority automatically.

## Artifacts

```text
<project-root>/DESIGN.md                            # durable authority after accept
.scratch/<run>/design-baseline/state.json           # gate cache (schema design-baseline/v1)
.scratch/<run>/design-baseline/evidence.json        # extraction evidence (when drafted)
.scratch/<run>/design-baseline/DESIGN.draft.md      # proposal; never authority by itself
```

The deep module `prepare`/`confirm`/`verify` is the sole gate surface. An adopted existing `DESIGN.md` only needs to carry verifiable source provenance (path + SHA-256 under `## Source Evidence & Confidence`) to be bound; the other section names in [`references/design-template.md`](references/design-template.md) are draft guidance, not a structural contract imposed on hand-written baselines.

## Scope fence

| In | Out → |
| --- | --- |
| First-party visual baseline and provenance | Functional behavior → `ux-spec` |
| Existing-source extraction | External inspiration → `reference-intake` |
| Confirmation before durable write | Scene/template/component decision → `ui-picker` |
| Baseline drift source for point-back | Runtime evidence judgment → `ui-evaluator` |

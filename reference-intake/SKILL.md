---
name: reference-intake
description: Reference intake contract from screenshot, URL, design file, or product analogy. Use when the user supplies visual/product reference before build, or when Keep/Change/Do not copy boundaries are missing for a design ask.
metadata:
  short-description: "Screenshot and URL reference contract"
  author: Bandersnatch0x
  source: https://github.com/Bandersnatch0x/design-playbook/tree/main/packages/design-playbook/skills/reference-intake
---

# reference-intake

Turn raw reference material into a **run-local declaration input** the pipeline can consume without re-deriving from chat.

Not a style library. Not a code export. Not a Pass/Fail gate.

Authority boundary (ADR-0011):

| This skill owns | Does not own |
| --- | --- |
| Observed vs inferred evidence about the reference | `spec.md` L1→L6 |
| Keep / Change / Do not copy | decision report / Fill source |
| License and brand risk notes | evaluator verdict |

## When to apply / skip

**Apply** when the ask includes at least one of:

- local screenshot / mock / design export path
- URL of a live product or design
- existing in-repo design artifact the user points at
- explicit product or brand analogy ("like Linear", "参考飞书设置页")

**Skip** when the ask is text-only product requirements with no reference material. Narrate once: `-> reference-intake?: no reference materials, skipped`.

## Steps

### 1. Inventory sources

List every reference source. For each source record:

- `kind`: `screenshot` | `url` | `design_file` | `product_analogy` | `other`
- locator (path or URL or product name)
- for files: SHA-256 when the file is readable; for URLs: the exact URL string
- `captured_at` (ISO-8601) and `tool` (how it was collected)

Write `manifest.json` under `.scratch/<run>/reference/` using the shape in [`references/contract-template.md`](references/contract-template.md). Copy durable local media into `reference/assets/` only when needed for later human review; never into the host Fill tree.

For a temporary PNG, JPEG, WebP, or GIF that enters a design run, call
[`scripts/reference_sources.py`](scripts/reference_sources.py)
`ingest_ephemeral_image(...)`. Pass `kind: screenshot` for a captured
interface and `kind: other` for any other raster; keep the helper's
detected `media_type`. Cite the returned source id and run-relative
locator. Keep `provider` as a provider label, never a path. Record host
temporary paths nowhere in the manifest or contract. Author URLs, design
files, and product analogies directly; field names and locator rules live
in [`references/contract-template.md`](references/contract-template.md).

Treat Figma's official MCP, a Lanhu export, an HTML export, a URL, and a local
design file as upstream collection methods. Normalize their outputs into this
provider-neutral source contract; do not add a vendor conversion layer or let
provider output write `spec.md`, the decision report, Fill source, or a verdict.

**Done when:** every cited source appears in `manifest.json` with kind + locator; file sources that exist on disk carry `sha256`.

### 2. Separate observed from inferred

Read the text accompanying each source — user-provided notes, URL page text, file facts; **never the image bytes** (image sources were registered in step 1, not read). Fill **every required heading** from [`references/contract-template.md`](references/contract-template.md) (SSOT for section names and bullet prompts). Do not invent alternate headings.

The host model may have no vision (text-only input): image sources are registered by **preserved locator and metadata only** (`locator`, `sha256`, `captured_at` in `manifest.json`) — reading the image is never a required intake action. Materialize an ephemeral image with the helper first and never record its host temporary path. The observed/inferred split then rides the text the session can actually cite (user-provided notes, URL page text, file facts); visual points nobody can verify stay `inferred` or move to Unresolved questions. A no-vision host runs this skill end to end without degrading the protocol.

**Mandatory no-vision fallback:** when the host has no vision and the user supplied **only screenshots without accompanying text**, do not stop after stating the limitation — **first ask the user for a short written description** (what each screenshot shows, and which points to keep or change), then continue intake with that description as citable observed text. Ask once; it is a required step, not an optional courtesy. When the user already supplied text alongside the screenshots, do not ask. A vision-capable host may inspect the image directly and never triggers this ask.

Mark every claim as **observed** or **inferred**. Unlabeled claims are invalid; rewrite them before emit.

**Done when:**

- `contract.md` has every template heading (including always/ask/never hints and Unresolved questions)
- Keep / Change / Do not copy are each non-empty whenever any source is a product analogy, third-party URL, or third-party screenshot/design (first-party user-owned assets may put `none — first-party owned` under Do not copy only, with an ownership note)
- at least one license/brand risk line exists (`none identified` only for pure first-party assets the user owns)

### 3. Emit and stop

Write:

```text
.scratch/<run>/reference/contract.md
.scratch/<run>/reference/manifest.json
.scratch/<run>/reference/assets/   # optional
```

Optional disposable `example.html` may be generated under `reference/` only as a later preview input. It is **not** a Fill source (same hard boundary as `preview/round-*.html`).

Stop. Do not write `spec.md`, do not pick components, do not implement UI.

**Done when:** both `contract.md` and `manifest.json` exist; steps 1→2 Done-when criteria still hold in the files.

## Scope fence

| In | Out → |
| --- | --- |
| Source inventory + hashes | L6 acceptance → `ux-spec` |
| Keep / Change / Do not copy | template/component identity → `ui-picker` |
| Functional constraints derived from reference | coding / Fill |
| License and brand risk notes | visual similarity score as gate |
| | third-party skill or brand-kit port into the plugin |

## Handoff

After emit, the orchestrator continues to `ux-spec?` (or `plan?` when spec already exists). Consumers must cite `reference/contract.md` rather than re-describing the screenshot from memory.

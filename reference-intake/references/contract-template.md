# Reference contract template

Emit as `.scratch/<run>/reference/contract.md`. Headings are required.

## Source summary

- Ask (one line):
- Sources (ids matching `manifest.json`):
- Captured at (ISO-8601; may match `manifest.json` top-level `captured_at`):

## Evidence (observed)

- ...

No-vision filling rule: observed rows ride **only text the session can cite** — user-provided notes (including a requested written description of a screenshot), URL page text, file facts. A host without vision never derives observed claims from image bytes; visual points nobody can verify move to **Inferred (labeled)** or **Unresolved questions**.

## Inferred (labeled)

- ... | confidence: high|medium|low | why:

## Keep

- ...

## Change

- ...

## Do not copy

- ...

## Functional constraints for ux-spec

- Goal / scene hints:
- States or edges implied:
- Non-goals implied by Do not copy:
- always / ask / never hints:

## Visual cues for ui-picker

- Density:
- Scene class hints:
- Region weight / hierarchy:
- Explicit exclusions:

No-vision filling rule: fill each cue only from cited text (user notes describing the reference, URL page text); leave an item blank-and-noted or move it to **Inferred (labeled)** / **Unresolved questions** when nobody can verify it. Never invent cues from image bytes.

## License / brand risks

- ...

## Unresolved questions

- ... (or `none`)

---

## manifest.json shape

Write beside this contract as `.scratch/<run>/reference/manifest.json`:

```json
{
  "schema": "design-playbook.reference.manifest/v1",
  "run_id": "<run>",
  "captured_at": "2026-07-22T00:00:00+08:00",
  "tool": "reference-intake",
  "sources": [
    {
      "id": "src-1",
      "kind": "screenshot",
      "locator": "reference/assets/hero-<digest-prefix>.png",
      "sha256": "<hex or null>",
      "media_type": "image/png",
      "storage": "copied",
      "acquired_via": "attachment",
      "provider": "optional-host-tool-name",
      "captured_at": "2026-07-22T00:00:00+08:00",
      "note": "optional"
    }
  ]
}
```

Rules:

- `kind` is one of: `screenshot`, `url`, `design_file`, `product_analogy`, `other`
- copied ephemeral raster locators are run-root-relative under `reference/assets/`; existing authored fixtures may retain `assets/...` relative to the reference directory
- URL locators are absolute strings; `sha256` is null for pure URLs and product analogies
- `storage` is `copied`, `linked`, `remote`, or `symbolic`; `acquired_via` is `attachment`, `local-file`, `host-tool`, `export`, `url`, or `analogy`
- `media_type`, `provider`, and source-level `captured_at` are optional additive fields; `provider` is a non-path label and never changes core routing
- do not put host Fill paths into `sources`

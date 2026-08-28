# Narrow repair map

After Recirculate, change the **smallest owning declaration**, re-evaluate only affected L6 criteria, and recapture only invalidated evidence.

| Failure class | Owning declaration | Affected evidence |
| --- | --- | --- |
| Wrong goal / non-goal / always-ask-never | `spec` L1 | All criteria that assumed the old L1 |
| Missing edge path | `spec` L5 | Criteria that name that edge |
| Broken acceptance criterion | `spec` L6.<n> | Only that L6.<n> row + its artifacts |
| Component role mixup | `components` / decision report | Criteria that depend on that role |
| Craft hierarchy / motion | `craft` | Craft-backed criteria only |
| Capture contract / viewport mismatch | observe* seam | Recapture those artifacts (overwrite or revision name) |
| Stale screenshot after Fill change | Fill + observe* | Invalidate bound criteria whose observed UI changed |

## Second hop: declaration -> R1-R5

Routing is two hops (first hop above; second hop picks the repair target). Repair order follows declaration-layer dependency R1 -> R2 -> R3 -> R4; R5 (evidence plan) may append after any layer. Take the **minimum owning set**; multi-layer findings may carry multiple `source:` values.

| Route | Repair target | Trigger (finding features) | Landing action |
| --- | --- | --- | --- |
| R1 | requirement (reopen shaping subtree) | No owning declaration exists (ownerless finding); criterion unjudgeable (Given/When/Then gap); an `assumed` field falsified; non-goal boundary disputed | Reopen only the falsified subtree via a new shaping session (`superseded_by`); revise decisions via `supersedes`; never invent product requirements in review |
| R2 | interaction model (`spec` L2-L5 structured fields) | Five-state missing; path break; decision point / return-preservation unmodeled; page duty missing | Patch the structured spec rows; re-evaluate affected L6 |
| R3 | design decision (decision report) | Direction-level assumption failed; trade-off unrecorded; visual direction conflicts with baseline | Revise / append the decision report; re-confirm preview when it applies (G5) |
| R4 | implementation (Fill) | Implementation deviates from the confirmed model: action missing, wrong state, token scatter, component misuse | Fix the implementation; resume from the step consuming the declaration |
| R5 | evidence plan (observe* seam) | Method cannot answer the criterion (capture seed mismatch); provider absent; sample/environment mismatch; evidence insufficient while implementation is right | Fix the capture plan / provider / recapture; implementation and declarations untouched |

## Evidence freshness

- Fill changes that can affect a bound criterion **invalidate** prior evidence for that criterion.
- Replacement artifacts use `overwrite=true` or a new revision filename; the latest manifest entry wins.
- If the ledger still cites a superseded artifact name, emit a **warning** (not a hard Pass) with owner/expected/actual/repair — then recapture or update the ledger.
- Unaffected criteria keep their evidence.

## Invalidated evidence set (`invalidated:` block)

Record the minimal invalidated set in `point-back.md` when recirculating. The set is computable: directly affected criteria plus criteria derived from a falsified contract field (via its `notes` source chain); everything else keeps its evidence. History is preserved — superseded artifacts keep overwrite/revision naming and the latest manifest entry wins.

```text
invalidated:
  - criterion: L6.2
    artifacts: [evidence/L6.2-cap-error.png, evidence/L6.2-a11y-tree.json]
    reason: Fill fix changed the cap-limit toast; rendered evidence stale
  - criterion: L6.3
    artifacts: []
    reason: capture provider session loss; R5 capture-plan revision, no artifacts existed
```

Re-evaluation runs the invalidated set plus adjacent primary-path nodes only (minimum repair); the two-cycle stop policy for a repeated blocker stays in force.

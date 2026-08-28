# First-party UX rule registry

Product-level declaration shared by the whole pipeline (read-only at run time; runs only produce audit rows and findings that reference it). Entry blocks below are structured field blocks validated by the product-level G8 self-check (`validate.py`, repo scripts).

- `schemaVersion: 1`
- Split rule: when the registry exceeds 30 entries or 3 families, split by family into per-family files plus an index and migrate the G8 cross-file checks at the same time.
- Machine-checkable face: `id / version / capability-domain / executes-in / authority / applicability-* / check-type / evidence-layers / severity-default / owner / provenance / status / related / overrides / supersedes`. Protocol face (read by reviewers, not gated): `title / statement / check-inputs / signals-* / evidence-method / exceptions / false-positives / fix / history`.
- Rule text is first-party original. External product names and third-party rule text never enter this registry.

## CRAFT-01 — Primary hierarchy

```yaml
id: CRAFT-01
version: 1
title: Primary hierarchy
statement: Each viewport presents exactly one primary action or region that leads the scan (observable); competing primaries or no primary leave the target user unable to locate the main action at a glance (user impact).
capability-domain: D4
executes-in: D4:interaction
authority: advisory-aesthetic
applicability-applicable: run has Fill output and the surface contains actions or regions that can claim primacy (contract detector subset does not disable this entry)
applicability-not-applicable: planning-only run, or a display surface with no action or region claiming primacy (observable reason required)
applicability-blocked: rendered or source evidence surface unavailable
check-type: protocol-check
check-inputs: rendered interface walkthrough; source primary variants and emphasis token usage
signals-rendered: more than one element claims primary emphasis, or no action or region leads the scan
signals-source: multiple primary variants, equivalent emphasis tokens, or page structure without a main landmark
evidence-layers: rendered>=1, source>=1
evidence-method: expert-review
severity-default: S2 / judgment
exceptions: deliberate equal-choice comparison supported by spec or a verified baseline (for example a side-by-side plan selection page)
false-positives: verified-baseline dual-primary layouts; a verified baseline outranks generic craft defaults and is recorded as an exception
owner: craft -> R4; template -> R4|R3
provenance: first-party
status: advisory
fix: preserve one scene-appropriate primary and make secondary actions recede through placement, density, or neutral treatment
related: CRAFT-06@1
history: 1 | 2026-08-14 | docs | initial registry registration, migrated from the craft-guard detector protocol six-field block
```

## CRAFT-02 — Repeated card wall

```yaml
id: CRAFT-02
version: 1
title: Repeated card wall
statement: List and collection surfaces present a scanning hierarchy that matches the comparison task (observable); undifferentiated card walls hide comparison columns and task grouping (user impact).
capability-domain: D4
executes-in: D4:interaction
authority: advisory-aesthetic
applicability-applicable: run has Fill output and the surface contains a list or collection display
applicability-not-applicable: run has no list or collection display surface (observable reason required)
applicability-blocked: rendered or source evidence surface unavailable
check-type: protocol-check
check-inputs: rendered interface walkthrough; source card wrapper usage on records and regions
signals-rendered: most content appears as equal floating cards with no scanning hierarchy or task grouping
signals-source: repeated card wrappers applied to unrelated regions or to list and table records without a card-specific interaction
evidence-layers: rendered>=1, source>=1
evidence-method: expert-review
severity-default: S2 / judgment
exceptions: browsable collections where each item is a genuinely independent object and the verified baseline uses cards
false-positives: none recorded yet
owner: template -> R4; craft -> R4
provenance: first-party
status: advisory
fix: use list, table, band, or unframed grouping that matches comparison and action needs; reserve cards for independent items
related: CRAFT-03@1
history: 1 | 2026-08-14 | docs | initial registry registration, migrated from the craft-guard detector protocol six-field block
```

## CRAFT-03 — Nested or floating containers

```yaml
id: CRAFT-03
version: 1
title: Nested or floating containers
statement: Container framing follows interaction semantics rather than decoration (observable); nested borders and detached panels obscure which surface owns the content (user impact).
capability-domain: D4
executes-in: D4:interaction
authority: advisory-aesthetic
applicability-applicable: run has Fill output and the surface combines sections or containers
applicability-not-applicable: run has a single flat region with no container composition (observable reason required)
applicability-blocked: rendered or source evidence surface unavailable
check-type: protocol-check
check-inputs: rendered interface walkthrough; source nesting of card primitives and section wrappers
signals-rendered: multiple nested borders, radii, shadows, or detached section surfaces obscure ownership
signals-source: card components nested for spacing, or full-width sections wrapped in decorative card primitives
evidence-layers: rendered>=1, source>=1
evidence-method: expert-review
severity-default: S2 / judgment
exceptions: a real framed tool, modal, or repeated item nested within an unframed page region
false-positives: none recorded yet
owner: template -> R4; components -> R4
provenance: first-party
status: advisory
fix: flatten page regions, group with spacing and dividers, and retain a frame only where interaction semantics require one
related: CRAFT-02@1
history: 1 | 2026-08-14 | docs | initial registry registration, migrated from the craft-guard detector protocol six-field block
```

## CRAFT-04 — One-note palette

```yaml
id: CRAFT-04
version: 1
title: One-note palette
statement: Unrelated semantic roles use distinguishable color roles (observable); one hue family carrying surfaces, accents, and states erases semantic contrast and hierarchy (user impact).
capability-domain: D4
executes-in: D4:interaction
authority: advisory-aesthetic
applicability-applicable: run has Fill output and the surface carries multiple semantic roles
applicability-not-applicable: run has a single-semantic display surface with no state or role variation (observable reason required)
applicability-blocked: rendered or source evidence surface unavailable
check-type: protocol-check
check-inputs: rendered interface walkthrough; source color ramp and semantic token assignment
signals-rendered: backgrounds, surfaces, accents, and states rely on variations of one hue family
signals-source: one color ramp fills unrelated semantic roles, or repeated gradients replace neutral surfaces
evidence-layers: rendered>=1, source>=1
evidence-method: expert-review
severity-default: S2 / judgment
exceptions: verified monochrome brand systems with sufficient state, contrast, and hierarchy differentiation
false-positives: none recorded yet
owner: design -> R3; craft -> R4
provenance: first-party
status: advisory
fix: keep brand color selective, restore neutral surfaces, and use semantic colors only for named roles
related:
history: 1 | 2026-08-14 | docs | initial registry registration, migrated from the craft-guard detector protocol six-field block
```

## CRAFT-05 — Shape and pill overuse

```yaml
id: CRAFT-05
version: 1
title: Shape and pill overuse
statement: Control and tag geometry is specific to the control identity (observable); uniform pill geometry across unrelated controls erodes recognition (user impact).
capability-domain: D4
executes-in: D4:interaction
authority: advisory-aesthetic
applicability-applicable: run has Fill output and the surface contains control or tag geometry variation
applicability-not-applicable: run has no control or tag geometry surface in scope, or one restrained standard radius declared for all controls (observable reason required)
applicability-blocked: rendered or source evidence surface unavailable
check-type: protocol-check
check-inputs: rendered interface walkthrough; source radius tokens and text-control primitives
signals-rendered: most labels, actions, and containers share pill geometry or oversized rounding
signals-source: large border-radius tokens applied globally, or text-in-rounded-rectangle controls replacing icons, toggles, tabs, or badges
evidence-layers: rendered>=1, source>=1
evidence-method: expert-review
severity-default: S1 / judgment
exceptions: verified brand geometry, or semantic chips and tags whose shape communicates grouping or status
false-positives: none recorded yet
owner: components -> R4; design -> R4
provenance: first-party
status: advisory
fix: use control-specific primitives and restrained radii; reserve pills for semantics that need compact grouping
related:
history: 1 | 2026-08-14 | docs | initial registry registration, migrated from the craft-guard detector protocol six-field block
```

## CRAFT-06 — Type-scale mismatch

```yaml
id: CRAFT-06
version: 1
title: Type-scale mismatch
statement: Type role matches container and workflow density (observable); hero-sized copy in compact panels crowds the content it labels (user impact).
capability-domain: D4
executes-in: D4:interaction
authority: advisory-aesthetic
applicability-applicable: run has Fill output and the surface contains dense container typography
applicability-not-applicable: run has no dense container typography surface in scope (observable reason required)
applicability-blocked: rendered or source evidence surface unavailable
check-type: protocol-check
check-inputs: rendered interface walkthrough; source type token usage per container
signals-rendered: hero-sized copy dominates compact panels, or hierarchy depends on oversized text instead of structure
signals-source: display tokens used in cards, toolbars, dashboards, or narrow controls without a true hero context
evidence-layers: rendered>=1, source>=1
evidence-method: expert-review
severity-default: S1 / judgment
exceptions: literal landing-page hero, or a verified expressive product surface with stable responsive fit
false-positives: none recorded yet
owner: design -> R4; craft -> R4
provenance: first-party
status: advisory
fix: match type role to container and task density, using structure and weight before display scale
related: CRAFT-01@1
history: 1 | 2026-08-14 | docs | initial registry registration, migrated from the craft-guard detector protocol six-field block
```

## CRAFT-07 — Text-as-control and icon misuse

```yaml
id: CRAFT-07
version: 1
title: Text-as-control and icon misuse
statement: Frequent low-risk actions use the most recognizable control primitive (observable); verbose text pills where familiar symbols communicate better consume space and slow recognition (user impact).
capability-domain: D4
executes-in: D4:interaction
authority: advisory-aesthetic
applicability-applicable: run has Fill output and the surface contains repeated high-frequency operations
applicability-not-applicable: run has no repeated operation surface in scope (observable reason required)
applicability-blocked: rendered or source evidence surface unavailable
check-type: protocol-check
check-inputs: rendered interface walkthrough; source control primitives for repeated actions
signals-rendered: repeated undo, close, save, formatting, color, or binary actions consume space as text buttons
signals-source: text buttons replace available icon buttons, swatches, segmented controls, toggles, or checkboxes
evidence-layers: rendered>=1, source>=1
evidence-method: expert-review
severity-default: S2 / judgment
exceptions: unfamiliar, high-risk, or ambiguous actions that require explicit text; accessibility names remain required for icons (a missing accessible name is a factual sub-signal the evaluator may rate independently on the severity axis)
false-positives: none recorded yet
owner: components -> R4; craft -> R4
provenance: first-party
status: advisory
fix: use familiar icon or control primitives with accessible names and tooltips where recognition needs support
related:
history: 1 | 2026-08-14 | docs | initial registry registration, migrated from the craft-guard detector protocol six-field block
```

## CRAFT-08 — Purposeless motion

```yaml
id: CRAFT-08
version: 1
title: Purposeless motion
statement: Every animation names the state change it explains and does not destabilize interaction (observable); motion without a state explanation draws attention away from the task (user impact).
capability-domain: D4
executes-in: D4:interaction
authority: advisory-aesthetic
applicability-applicable: run has Fill output and the surface contains motion
applicability-not-applicable: run has no motion on the audited surface (observable reason required)
applicability-blocked: motion source or interaction trace was not included in review input
check-type: protocol-check
check-inputs: rendered motion observation; source motion implementation and reduced-motion handling; interaction trace when available
signals-rendered: bounce, elastic, looping, or entrance motion draws attention without communicating state
signals-source: animation targets layout properties, lacks reduced-motion handling, or has no named state transition
evidence-layers: rendered>=1, source>=1
evidence-method: expert-review
severity-default: S2 / judgment
exceptions: expressive game or immersive scene motion declared by product intent and kept clear of task controls
false-positives: micro-interaction transitions misread as decoration; decide by whether a named state change is present
owner: craft -> R4; design -> R4
provenance: first-party
status: advisory
fix: remove decorative motion or replace it with short transform or opacity feedback tied to a named state change
related: PERF-01@1
history: 1 | 2026-08-14 | docs | initial registry registration, migrated from the craft-guard detector protocol six-field block
```

## A11Y-01 — Accessible names, roles, and states

```yaml
id: A11Y-01
version: 1
title: Accessible names, roles, and states
statement: Interactive elements expose accessible name, role, and state plus a reachable keyboard path at the declared viewport (observable); missing names or keyboard dead ends block assistive-technology users from perceiving and operating the surface (user impact).
capability-domain: D4
executes-in: D4:cross-cutting
authority: hard-constraint
applicability-applicable: run has Fill output containing interactive elements
applicability-not-applicable: planning-only run, or a static non-interactive display surface (observable reason required)
applicability-blocked: a11y tree capture or keyboard walkthrough evidence unavailable
check-type: protocol-check
check-inputs: a11y tree capture; keyboard interaction walkthrough; source semantic bindings
signals-rendered: nodes without accessible name or role; keyboard traps; focus loss; material omissions the tree cannot prove (contrast, hit area, motion safety are findings, never silent passes)
signals-source: interactive elements without semantic bindings or focus management
evidence-layers: rendered>=1, source>=1
evidence-method: runtime-observation
severity-default: S3 / fact
exceptions: explicit legacy-surface exemptions recorded in the spec (each requires an owning declaration)
false-positives: decorative imagery correctly unnamed; hidden-at-runtime controls flagged by naive scans
owner: components -> R4; spec -> R2
provenance: first-party
status: advisory
fix: give each interactive element an accessible name and role, keep the keyboard path reachable, and attach findings to the owning user-risk L6 (ADR-0016 attach rule; do not auto-generate a11y L6 seeds)
related:
history: 1 | 2026-08-14 | docs | initial registry registration, first-party cross-cutting entry (accessibility)
```

## RESP-01 — Declared-viewport coverage

```yaml
id: RESP-01
version: 1
title: Declared-viewport coverage
statement: The implemented UI remains usable at every viewport the contract declares (observable); a surface that only works at one viewport fails users on declared target viewports (user impact).
capability-domain: D4
executes-in: D4:cross-cutting
authority: project-declaration
applicability-applicable: contract or capture contract declares target viewports and the run has Fill output
applicability-not-applicable: planning-only run with no Fill output (observable reason required)
applicability-blocked: no declared target viewport (the gap itself is a finding routed to D1/D2), or viewport capture unavailable at a declared viewport
check-type: protocol-check
check-inputs: capture contract viewport group; rendered captures per declared viewport; source responsive behavior
signals-rendered: layout breakage or unreachable actions at a declared viewport
signals-source: single-viewport-only implementation, fixed widths beyond declared maxima
evidence-layers: rendered>=1, source>=1
evidence-method: runtime-observation
severity-default: S2 / fact
exceptions: viewports explicitly declared out of scope in the contract
false-positives: pixel-perfect equality across viewports; only usable reachability is required
owner: spec -> R2; template -> R4
provenance: first-party
status: advisory
fix: implement or fix layout at each declared viewport; declare target viewports in the contract when the declaration is missing
related:
history: 1 | 2026-08-14 | docs | initial registry registration, first-party cross-cutting entry (responsive)
```

## I18N-01 — Interface language and localizability consistency

```yaml
id: I18N-01
version: 1
title: Interface language and localizability consistency
statement: User-visible copy keeps language, terminology, and formats consistent and does not block localization (observable); inconsistent terms and hard-coded formats raise localization and comprehension cost (user impact).
capability-domain: D4
executes-in: D4:cross-cutting
authority: platform-convention
applicability-applicable: contract declares an i18n field (such as i18n.*) or spec L1 declares a multilingual user base
applicability-not-applicable: single-language declaration with no locale-related field (observable reason required, e.g. single-language document interface with no i18n declaration)
applicability-blocked: copy inventory evidence needed for the decision is unavailable (for example rendered capture absent)
check-type: protocol-check
check-inputs: rendered copy sampling; source hard-coded copy locations; spec L1 terminology list
signals-rendered: mixed terminology; layout that cannot absorb copy expansion
signals-source: hard-coded date or number formats; user-visible copy outside the resource layer
evidence-layers: rendered>=1, source>=1
evidence-method: static-inspection
severity-default: S2 / judgment
exceptions: declared single-language product with a confirmed single-language user base
false-positives: in-code comments and developer-facing copy that is not user-visible (to be verified)
owner: spec -> R2; components -> R4
provenance: placeholder
status: advisory
fix: unify terminology to the spec L1 word list; route user-visible copy through the resource layer instead of hard-coding
related:
history: 1 | 2026-08-14 | docs | initial registry registration, placeholder entry with explicit applicability predicate (never silently skipped)
```

## PERF-01 — Perceived-performance feedback

```yaml
id: PERF-01
version: 1
title: Perceived-performance feedback
statement: Declared async operations carry perceived feedback proportionate to duration plus a timeout exit (observable); unindicated waits invite repeated triggers and erode trust (user impact).
capability-domain: D4
executes-in: D4:cross-cutting
authority: measured-threshold
applicability-applicable: spec L4 declares async operations and the run measurement layer is capturable
applicability-not-applicable: no async operation declared (observable reason required)
applicability-blocked: async declaration exists but the measurement provider is absent (perceived performance needs runtime measurement)
check-type: protocol-check
check-inputs: measurement-derived timing; interaction traces; spec L4 declarations
signals-rendered: unindicated long waits; feedback that does not match duration; no timeout exit
signals-source: trigger controls without busy or disabled bindings for in-flight operations
evidence-layers: measurement>=1, interaction>=1
evidence-method: runtime-observation
severity-default: S2 / judgment
exceptions: declared background tasks that report completion through notification instead of inline feedback
false-positives: operations shorter than the perception threshold being required to show feedback (to be verified)
owner: spec -> R2|R5
provenance: placeholder
status: advisory
fix: tier feedback by duration aligned with the craft Loading tiers declaration; timeouts get retry or cancel exits; in-flight triggers get busy state
related: CRAFT-08@1
history: 1 | 2026-08-14 | docs | initial registry registration, placeholder entry with explicit applicability predicate (never silently skipped)
```

## SEC-01 — Sensitive-operation safety experience

```yaml
id: SEC-01
version: 1
title: Sensitive-operation safety experience
statement: Sensitive data and dangerous operations have confirmation, undo, or audit exits (observable); unguarded dangerous actions cause irreversible loss (user impact).
capability-domain: D4
executes-in: D4:cross-cutting
authority: hard-constraint
applicability-applicable: domain or spec declares sensitive data or dangerous operations
applicability-not-applicable: declared scope has no sensitive surface (observable reason required, e.g. no sensitive operation added)
applicability-blocked: sensitivity cannot be determined from existing declarations (the declaration gap is itself a finding routed to D1)
check-type: protocol-check
check-inputs: interaction traces of dangerous operations; source confirmation and undo bindings; domain declarations
signals-rendered: dangerous operations without confirmation; sensitive values in clear text
signals-source: no confirm or undo binding on destructive triggers; no audit exit
evidence-layers: interaction>=1, source>=1
evidence-method: runtime-observation
severity-default: S3 / fact
exceptions: declared controlled-environment rehearsal operations backed by a domain declaration
false-positives: operations with a global undo stack being required to confirm twice (to be verified)
owner: domain -> R1; components -> R4
provenance: placeholder
status: advisory
fix: add confirmation or undo to dangerous operations; mask sensitive values; record audit exits in the domain declaration
related:
history: 1 | 2026-08-14 | docs | initial registry registration, placeholder entry with explicit applicability predicate (never silently skipped)
```

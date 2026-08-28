# Existing UI extraction guidance

Use source precedence to separate intended design rules from accidental one-offs.

## 1. Discover the frontend shape

Identify framework and styling signals from manifests and configuration. Map pages/routes, shared components, global styles, themes/tokens, fonts, assets, and stories/screenshots. Ignore generated output, dependencies, caches, and vendored UI.

Classify the project as existing-product only when meaningful first-party UI exists. A dependency on React/Vue/Svelte alone is not enough.

## 2. Read sources in precedence order

1. Explicit tokens and theme files.
2. Global CSS variables, Tailwind/theme configuration, font declarations.
3. Shared primitives: buttons, inputs, cards, navigation, dialogs.
4. Two to five representative pages covering dominant and edge-case layouts.
5. Rendered evidence when the app can be inspected safely.

Higher-precedence sources express intent. Lower-precedence sources prove what shipped. Record conflicts; do not hide them by averaging values.

## 3. Extract by functional role

- Colors: background, surface, text hierarchy, interaction, semantic state.
- Typography: family, scale, weight, line height, letter spacing, usage.
- Layout: container width, grid, density, spacing unit, responsive collapse.
- Components: shape, variants, states, focus, elevation, domain primitives.
- Motion: duration, easing, state change, reduced-motion handling.
- Accessibility: contrast intent, focus visibility, keyboard, touch targets.

Consolidate near-duplicate values only when their role is demonstrably the same. Keep unexplained divergence under Known Gaps & Exceptions.

## 4. Preserve provenance

For every high-signal source included in the baseline, record its project-relative path and SHA-256. Label design claims `[observed]` when directly supported and `[inferred]` when synthesized. Give inferred claims a confidence level and expose unresolved contradictions.

## 5. Draft, then confirm

Generate only `.scratch/<run>/design-baseline/DESIGN.draft.md`. Compare it with representative existing pages. Present the meaningful design rules and conflicts to the user. A draft becomes `<project-root>/DESIGN.md` only after explicit confirmation.


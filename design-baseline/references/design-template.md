---
name: "<project-name>"
colors:
  background: "<#hex-or-token>"
  surface: "<#hex-or-token>"
  text: "<#hex-or-token>"
  primary: "<#hex-or-token>"
---

# Design System: <project-name>

## Visual Theme & Atmosphere

- [observed] <dominant visual character and evidence>
- [inferred] <design intent, with confidence>

## Color Palette & Roles

### Foundation

- `<token or value>` — <background/surface role>

### Interactive and Functional

- `<token or value>` — <primary/success/warning/error role>

### Text Hierarchy

- `<token or value>` — <primary/secondary/muted role>

## Typography Rules

- Font families: <families and fallbacks>
- Hierarchy: <display/heading/body/label sizes, weights, and line heights>
- Usage: <where each role appears>

## Component Stylings

- Buttons: <shape, variants, states, spacing>
- Containers/cards: <radius, border, elevation, padding>
- Navigation: <layout and active states>
- Inputs: <shape, focus, validation, touch target>
- Domain primitives: <project-specific components>

## Layout Principles

- Container/grid: <width, columns, alignment>
- Spacing: <base unit and density>
- Responsive behavior: <breakpoints and collapse rules>

## Motion & Interaction

- Timing/easing: <tokens or observed values>
- State transitions: <hover/focus/pressed/loading>
- Reduced motion: <behavior>

## Accessibility

- Contrast and color semantics: <rules>
- Focus and keyboard: <rules>
- Touch targets and text scaling: <rules>

## Source Evidence & Confidence

- path: `src/path/to/high-signal-file`
  sha256: `<64-lowercase-hex-digest>`
  captures: <tokens/theme/layout/components>
  confidence: <high|medium|low>

## Known Gaps & Exceptions

- <contradiction, unverified inference, legacy override, or missing state>


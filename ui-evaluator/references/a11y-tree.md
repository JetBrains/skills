# Accessibility-tree interpretation (evaluator reference)

Use when an L6 criterion is proven (in part or wholly) by an `a11y tree` capture. This is judgment guidance for `ui-evaluator`, not a capture schema and not an automatic L6 seed generator.

## What the tree is good for

- Accessible **name** presence and usefulness for interactive controls
- **Role** identity for the control the user is trying to operate
- **State** that the user must perceive (expanded/collapsed, selected, disabled, invalid, busy)
- **Keyboard path** implications when the tree exposes focusable order or missing tab stops
- **Focus** ownership for dialogs, drawers, and error recovery
- Material **omissions**: unlabeled icon buttons, decorative noise announced as content, required fields without names

## Judgment checklist

1. Name the user outcome under test (usually the same L6 criterion that owns the risk).
2. Locate the control or region in the tree by role + name, not by CSS class.
3. Confirm the state the user must perceive is present and not contradicted by sibling text.
4. Confirm keyboard/focus implications: if the criterion claims a keyboard path, the tree must not show a dead end (no name, no role, or focus trapped without escape).
5. Record material omissions as findings with owner declarations (`spec` L5/L6, `craft`, or Fill component semantics).

## Attachment rule (ADR-0016)

- Prefer attaching accessibility proof to an **existing** user-risk L6 criterion when it tests the same outcome.
- Create a standalone accessibility L6 item only when the accessibility failure is an independently blocking user-visible risk.
- Do not auto-generate a11y or multi-stack L6 seeds.

## Honest limits

- Tree presence does not prove visual contrast, hit-target size, or motion safety.
- Missing tree nodes may mean the surface was not reached; prefer recapture over inventing N/A without reason.

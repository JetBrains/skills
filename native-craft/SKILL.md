---
name: native-craft
description: Native-feel declaration for desktop apps. Use when a cross-platform desktop app (macOS+Windows) must feel indistinguishable from native. Invoked before ui-picker for native-desktop targets, not web pages.
metadata:
  short-description: "Native desktop feel declaration"
  author: Bandersnatch0x
  source: https://github.com/Bandersnatch0x/design-playbook/tree/main/packages/design-playbook/skills/native-craft
---

# native-craft

**Native-feel** is a Design I/O **platform declaration**: it fixes the render-surface seam and the native conventions before any web shell is picked. It is the native-desktop twin of `craft-guard` (which owns web craft); the two do not overlap.

Inspired by yetone/native-feel-skill (MIT) - see `NOTICE`. Depth lives in [`references/native-feel.md`](references/native-feel.md); do not load it until this skill applies.

## When to apply / skip

- **Apply** when the target is a desktop app that must feel native on macOS + Windows (launcher, system utility, all-day productivity tool).
- **Skip** when: one OS only (build pure native), Electron-is-fine polish bar, internal tool, cold-start < 100 ms hard, or pure web/mobile. The decision gate in `references/native-feel.md` rules this stack **out** for those shapes - say so directly.

## The declaration: place the seam at the rendering surface

The cross-platform boundary is drawn at the **WebView surface**, nowhere else.

- **Below the seam** - windowing, hotkeys, materials, file dialogs, tray, a11y, input methods, context menu - **native**, written twice in idiomatic Swift / C#. No abstraction is fast or correct enough.
- **Above the seam** - React tree, business logic, extension API, AI orchestration - **shared**, written once in TS.

Test every platform decision: *is this above or below the rendering surface?* Below -> write it twice. Above -> write it once. Refuse to draw the line elsewhere.

## Native-feel tenets (cite by short name when advising)

- **Seam at render surface** - the boundary altitude where neither side can mimic the other.
- **Adopt the platform, don't compete** - the OS draws blur, scrollbars, dark mode, focus rings better than you. "Let the OS do it" *is* the implementation; custom is last resort.
- **Perception is performance** - users feel promises kept (keystroke, frame, latency), not MB/FPS. Define the perception target before optimizing.
- **Cross boundaries intentionally** - every IPC is a design decision: async, batched, schema-typed, observable. Never treat IPC like a function call.
- **Iteration loop is the product** - hot-reload (~200 ms) vs native recompile (~30 s) is 150x; the cross-platform tax buys this, protect it.

## Pipeline integration

`native-craft` runs at **plan / shell**, before `ui-picker`:

1. Run the decision gate (`references/native-feel.md`). If it rules native-feel **out**, stop - tell the user to use Electron / pure native / web.
2. Declare the seam: what is native (below) vs shared TS (above).
3. Declare native conventions (materials, cursor, windowing, keyboard, drag-drop) - the audit in `references/native-feel.md`.
4. Hand to `ui-picker` for the shared UI shell; `craft-guard` still applies **above** the seam but must defer to native conventions below it (e.g. no `cursor: pointer`, no web toasts, OS materials).

## Done when

- The decision gate has been run and native-feel is either confirmed in-scope or ruled out (with the reason stated).
- The seam is declared: every platform-touching feature labeled native (below) or shared (above), no "convenient" third line.
- Native conventions that block native-feel are listed for the surface in scope (cursor, context menu, materials, window controls, scroll, motion, keyboard, drag-drop).
- `craft-guard` rules above the seam and native conventions below it are reconciled - no conflict where web idiom would telegraph "web app".

## Recirculate

| Observable | Declaration |
| --- | --- |
| App feels like a web page (cursor:pointer, web toasts, box-shadow windows) | `native-craft` conventions |
| Wrong seam (UI in native, or platform code in TS) | `native-craft` seam |
| Slow perception despite low memory | `native-craft` perception tenet |
| Used this stack for a one-OS / Electron-fine / <100ms app | `native-craft` decision gate (rule out) |

Depth: decision gate + native-conventions audit -> [`references/native-feel.md`](references/native-feel.md). Full evidence (WebView survival, IPC contract, memory truths) -> original `native-feel-skill` (user installs separately).

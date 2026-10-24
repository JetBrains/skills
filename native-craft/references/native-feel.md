# native-feel - decision gate + conventions audit

Condensed in our voice from yetone/native-feel-skill (MIT). Full depth (WebView survival, IPC contract, memory truths, Raycast evidence) lives in the original skill - install it separately if you need it.

## Decision gate - run first, rule OUT honestly

| Question | Ruled OUT (use instead) |
| --- | --- |
| One OS only? | Pure native (Swift/AppKit or C#/WinUI) - cross-platform tax not worth it |
| "Nice but Electron is fine"? | Electron + good designer - this stack's polish budget is 5-10x Electron |
| Internal tool, no end-user polish? | Web app / Electron |
| Cold start must be < 100 ms? | Pure native - WebView+Node boot floor is ~150-300 ms even prewarmed |
| Pure web or mobile? | Not this skill |

Proceed only when: macOS + Windows (optionally Linux), native-feel is a competitive differentiator, and warm-start < 50 ms / cold < 500 ms is acceptable.

## The seam (the one structural decision)

The below/above split is declared in `SKILL.md` — that list is authoritative. This reference adds the IPC rule:

One schema for all IPC -> generate typed clients per runtime (UniFFI for Rust<->Swift/Kotlin/C#). Hand-written marshalling drifts in a sprint.

## Native-conventions audit - the "not a web page" checklist

Each item telegraphs "web app" when wrong. None changes a benchmark; all change what the user feels. A skeptic should conclude "regular Mac/Windows app" in 30 s.

### Input & cursor
- No `cursor: pointer` on hoverable rows. Native list rows don't change cursor.
- No text selection on chrome (labels, button text, headings) - only content areas select.
- Native context menu, not WebKit's: override `willOpenMenu` (Mac) / `CoreWebView2.ContextMenuRequested` (Win).
- No spellcheck underlines / dictionary popups / link previews on chrome.
- IME composition window at the caret, not above the WebView (test Pinyin/kana).

### Windowing & focus
- Native window behavior: ⌘W / Alt-F4 close, ⌘M / Win+Down minimize.
- Dock/Taskbar click re-shows last window, doesn't spawn a new one.
- Settings in a native window (⌘, on Mac), not an in-app modal.
- Native `NSAlert` / `MessageBox` for confirms - no backdrop-blur modal overlays.
- OS notification center - no web toasts.
- Real title bar / chromeless region; drag works on full title bar.
- Traffic lights left (Mac) / min-max-close right (Win); Mac green = zoom not fullscreen.
- Window remembers size/position across launches, per-screen.

### Materials & visual
- Platform material for window background: Mac `NSVisualEffectView` / `NSGlassEffectView` (Liquid Glass, macOS 26+); Win `DWMWA_SYSTEMBACKDROP_TYPE` mica / acrylic.
- Dark mode follows system, no per-frame flicker.
- Accent color follows system accent - don't hardcode brand blue.
- System font: Mac `-apple-system, BlinkMacSystemFont`; Win `'Segoe UI Variable', 'Segoe UI'`.
- No `box-shadow` for window shadows, no `border-radius` for window rounding - the OS draws those.

### Scrolling
- Overlay scrollbars on Mac (fade out) - let WebKit do it.
- No smooth-scroll JS polyfills (`behavior: 'auto'`).
- Scroll position persists across in-window navigation.

### Motion
- Cut between views by default - no route fades.
- Honor `prefers-reduced-motion`.
- Window resize animated by OS, not JS layout animations.
- No loading skeletons for sub-200 ms ops (web idiom) - spinner or nothing.
- No spring/bounce on simple state changes; reserve spring for grab-and-drag.

### Keyboard
- Full keyboard nav; focus rings match platform.
- Native shortcuts (⌘F Mac, Ctrl+F Win); Escape always does something.
- Type-ahead in lists.

### File / drag-and-drop
- Native drag-and-drop with file URLs (`NSPasteboard` / `IDataObject`), not browser drag API.
- Drop files onto dock icon opens them (`application:openFiles:`).

## How to use this reference

1. Run the decision gate. If ruled out, stop and say so.
2. If in scope, declare the seam for the feature in question.
3. Walk the audit for the surface in scope; each unchecked item is a native-feel bug.
4. Reconcile with `craft-guard`: craft rules apply above the seam, native conventions below it. Where they'd conflict (e.g. craft says skeleton, native says no skeleton <200ms), native-feel wins below the seam.

Full depth (WebKit/WebView2 throttling fixes, IPC typing, memory measurement truths, Raycast binary evidence) -> install `native-feel-skill` and consult its `references/03-webview-survival.md`, `04-ipc-contract.md`, `05-memory-truths.md`, `07-evidence-raycast.md`.

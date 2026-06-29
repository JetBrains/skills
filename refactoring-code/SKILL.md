---
name: refactoring-code
description: Use when semantic refactoring is needed in Rider-supported solutions and projects, including .NET/C#, F#, VB, C++, Unity, Unreal Engine, XAML, Razor, and other GameDev or mixed-language projects. Trigger when edits must update declarations and usages across IDE-resolved references — rename symbols, move types or namespaces, safe-delete unused code, extract interface/base class/method, change signatures, or reorganize namespaces. Do not use for file-only moves, config keys, strings, comments, prose-only edits, generated output, or logic changes with unchanged names/signatures.
allowed-tools: execute_tool
metadata:
  author: JetBrains
---

# Code Refactoring

Rider/ReSharper-driven semantic refactorings across solutions opened in Rider. Each tool resolves the target through the IDE's reference index and rewrites every affected site — language-aware usages such as `nameof`, XML-doc `<see cref>`, XAML `x:Name`, Razor, C++/Unreal references, and ordinary references that grep cannot resolve — instead of a grep + text replace.

## Invoking a tool

Always call a refactoring tool **through `execute_tool`** — direct calls are not routed:

```
execute_tool(command="<tool> --arg value [--flag true]")
```

[reference/tools.md](reference/tools.md) lists every available tool; the linked `reference/tools/<tool>.md` gives the exact parameters, response shape, outcomes, and error kinds for the one tool you are about to call. Read only that one — do not pre-load the whole tree.

## Shared rules

1. **Prefer the MCP tool.** It resolves references semantically across the solution; a text edit cannot. On any error you cannot fix by adjusting the input, or when `execute_tool` is unavailable, fall back to a text-based edit.
2. **Trust the success signal.** When a tool reports success, do not re-read the mutated files to verify. When a tool reports a count of touched/affected sites, a value of **1** for a type, method, property, field, or namespace is suspicious (declaration only, no callers) — surface it to the user.
3. **Do not retry blindly.** Adjust the arguments (query, name, file path, namespace, member selection) between calls; a second identical call fails the same way.
4. **Argument hygiene.** Parameter names are **camelCase** — hyphenated variants like `--new-name` are rejected. Pass real values or omit optional parameters entirely; never encode an omitted value with a placeholder such as `""`, `"/"`, `"__omit__"`, or a fake path. Treat file paths and identifiers as opaque: copy them from `search_*` / `get_symbol_info` output (solution-relative, `/` separators) rather than synthesizing them.
5. **Library / external symbols cannot be refactored** — the change must be made upstream. Confirm with the user.

## Preview before applying

When a tool offers an analysis-only mode — rename's `--preview true` (the older `--dryRun` is a deprecated alias) returns the blast radius in `affects` without mutating files — use it first for:

- public / protected API changes;
- targets with many call sites or cross-language references (XAML, Razor, C++/Unreal, `<see cref>`);
- any change where the user signalled hesitation.

Audit `affects`, then re-issue without `--preview` to apply. Preview resolves the symbol and counts usages only — it does **not** run conflict analysis, so a clean preview can still come back with conflicts on apply. Which tools expose an analysis mode is documented per tool in the reference; a tool without one (e.g. a namespace move) applies directly and reports no blast-radius counts, so for a high-impact call narrate what you are about to change before issuing it.

## Conflicts and ambiguity

Tools that rewrite or remove use sites (rename, safe-delete, the extract family) can refuse the operation and report **conflicts**; a symbol query can also match more than one candidate (**ambiguous**). Both come back in the tool's response — see its reference file for the exact fields. A non-empty `conflicts` list always means **no files were touched**.

Each conflict entry is `{kind, file, context}`: `kind` mirrors ReSharper's `ConflictType` (snake_case); `context` is the engine's localized description; `file` is the first occurrence's path, or `null` for a textual-only conflict or a clash with a compiled-only symbol.

| Kind | Meaning | Action |
|------|---------|--------|
| `same_name_conflict` | New name collides with an existing member in the same scope. | Pick a different name and retry. |
| `default_element_conflict` | Generic element-level conflict without a more specific category. | Read `context`; if it names a colliding member, treat like `same_name_conflict`, else escalate. |
| `cannot_update_usage_conflict` | A call site can't be rewritten safely — downstream code would break. | Escalate to user; do not auto-retry. |
| `cannot_remove`, `cannot_delete_safely`, `cannot_inline_usage_conflict`, `will_be_removed` | Engine refuses to remove/delete/inline/rewrite a use site. | Escalate to user; do not auto-retry. |
| `not_accessible` | The symbol becomes invisible from one or more use sites after the change. | Escalate to user — a visibility regression. |
| `will_be_made_public` | Access widens as a side-effect (e.g. promoted from private). | Narrate the trade-off to the user before retrying. |
| `hierarchy_conflict` | Override/implementation hierarchy clashes with the result. | Escalate to user; cross-type effect. |
| `text_only`, `default_conflict`, `invalid` | Generic textual conflict the engine couldn't categorise structurally. | Read `context`; decide from the message. |

Escalate to the user (instead of auto-deciding) when there are more than 5 conflicts, or any entry is `cannot_update_usage_conflict`, `cannot_remove`, `cannot_delete_safely`, `cannot_inline_usage_conflict`, `will_be_removed`, `not_accessible`, or `hierarchy_conflict`. For a rename, also escalate when `affects.callSites == 0` for a public/protected symbol — it usually means the query resolved to the wrong target.

For **ambiguous** matches, each candidate carries `name`, `kind`, `fqn`, `file`, and `line`. Re-issue with a more specific query — a fully qualified path like `Namespace.Type.Method`, or `Type._field` for a private member (bare short-name lookup prioritises type-level entries and misses private fields/methods). The candidate list is capped at 5; if you see 5, the query is too broad — narrow it rather than expecting the full set.

## Known limitations

These hold across the toolset and are not reported in tool output:

- **String and comment occurrences** of an old name or namespace are not updated. Fix them with a regular edit pass afterward.
- **XML-doc `<see cref>` references**: a method rename rewrites `<see cref="Type.Method(int, int)"/>` to `<see cref="NewName"/>`, dropping the containing type and parameter list. Surface to the user after method renames and offer a separate edit pass.
- **Moving a type does not move its file** — only the namespace declaration and references change. If the user also wants the file relocated to match, do that as a separate file operation.

## Tool reference

[reference/tools.md](reference/tools.md) is the auto-generated index of every tool in the Rider refactoring toolset, each entry linking to its full parameter and response contract. It is regenerated from the live MCP descriptors, so it — not this file — is the source of truth for tool names, signatures, and response shapes. This skill carries only the workflow and judgment the contracts don't.

---
name: code-search
description: Use when locating code in a Rider-supported solution (.NET/C#, F#, VB, C++, Unity, Unreal, XAML, Razor, mixed-language) — find where a symbol is declared, find usages/text/regex matches, or find files by glob. Trigger instead of shell grep/ripgrep/glob/find and instead of opening files to hunt, because these tools query the IDE index and return exact locations only. Do not use for non-Rider projects, for editing/refactoring code, or when you already hold the exact path you need.
allowed-tools: execute_tool
metadata:
  author: JetBrains
---

# Code Search

IDE-indexed search over a solution open in Rider. Each tool resolves through the index and returns
**locations only** — `filePath` plus 1-based line/column, never file contents. Prefer it over shell
`grep`/`rg`/`glob`/`find` and over opening files to look: one indexed query replaces many
read-and-scan rounds, and `search_symbol` matches declaration *names*, so it never drowns in the
comments, strings, usages, and generated/migration noise that text search returns.

## Invoke a tool

Call through `execute_tool` — the first token is the exact tool name, then `--flag value` pairs.
Map each need to exactly one tool and issue it directly; the required flag is below, so no lookup
is needed first.

```
execute_tool(command='search_symbol --q AppUserSmartFilter --paths ["API/Entities/**"]')
```
`--paths` is a JSON array: the globs go inside `[ ]` with each pattern in double quotes.

| Need | Tool | Required flag |
|---|---|---|
| Where a class/method/field is **declared** | `search_symbol` | `--q` |
| Every **textual occurrence / usage** of a string | `search_text` | `--q` |
| **Regex** matches | `search_regex` | `--q` |
| **Files** by name/glob | `search_file` | `--q` |

- Every tool also takes `--paths` — a **single-quoted JSON array** of project-relative globs (NOT a
  bare glob or a comma-separated list); `!` excludes, trailing `/`→`**`, no `/`→`**/pattern`. E.g.
  `--paths '["API/Entities/**"]'` or `--paths '["API/**","!**/Migrations/**"]'`. Also `--limit`
  (default 1000). `search_symbol` also takes `--include_external true` (SDK/library symbols, off by
  default); `search_file` also takes `--includeExcluded true`.
- Results carry **no symbol name or kind** — only paths + coordinates. Disambiguate by the returned
  **path**: for a name that collides across layers pick `'["API/Entities/**"]'` for the entity,
  `'["API/DTOs/**"]'` for the DTO, `'["API/Entities/Enums/**"]'` for the enum, etc.
- **For a common member name** (`Update`, `Delete`, `Attach`) do **not** scope `--paths` to a single
  file — `search_symbol` is name-ranked and its candidate list is capped *before* path filtering, so an
  over-narrow scope can return `[]` even when the symbol is there. Instead search the **declaring type**
  (e.g. `--q ISeriesRepository`) or search the member unscoped/broadly and pick the owning file from the
  returned paths.
- Only for a genuinely unclear parameter or output field, read `reference/tools/<tool>.md` once
  (index: `reference/tools.md`). There is no `--describe`.

## Rules

1. **Use the tool; don't grep/find or read files to hunt.** Reach for `search_*` before shell
   search and before opening a file to locate something. Fall back to shell search only if
   `execute_tool` isn't in your toolset, or a call actually ran and failed in a way no input change
   can fix.
2. **Trust the results — never re-verify.** The returned locations **are** the answer. Do **not**
   then run `grep`/`rg`, list the tree, or open files "to confirm the match" — it only burns tokens.
   Open a returned file **only** when you actually need its contents for the task (e.g. to read a
   type's namespace). If `more:true`, the list was truncated — narrow with `--paths`/`--limit`, don't
   switch to grep.
3. **Right tool for the job** — declarations → `search_symbol`; occurrences/usages →
   `search_text`/`search_regex`; files → `search_file`. Using text search to find a declaration is
   the classic waste this skill exists to prevent.
4. **Scope broad queries.** Add `--paths` for common names and disambiguate by the returned path.
   For `search_symbol`, if the result is empty retry once with `--include_external true`, then trust
   the empty result.
5. **Don't retry blindly** — change an argument between calls. Flags need real values; quote values
   with spaces; omit optional params rather than passing `""`.

## Notes & edge cases

`search_symbol` matches names fuzzily (camel-hump) and returns them **relevance-ranked**, so the
exact/prefix hit is at the top even when the list is long. Text/regex results are ordered by file.
Empty-query, invalid-regex, and out-of-project-path handling plus the full response schema are in
[reference/tools.md](reference/tools.md) and the per-tool files — read them only when a call behaves
unexpectedly.

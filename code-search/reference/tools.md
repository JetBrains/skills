# Tools

> **Legend.** `*` after a field marks a property required by the JSON schema. For inputs the caller
> must pass it; for outputs it is always present. All four tools share one response shape
> (`SearchResult`) so client parsing stays uniform.

## SearchToolset

- [search_symbol](tools/search_symbol.md) — Find where symbols (classes, methods, fields) are declared, by identifier fragment. Semantic, index-backed; project-only unless `include_external=true`.
- [search_text](tools/search_text.md) — Find a literal text substring in project files, with match coordinates.
- [search_regex](tools/search_regex.md) — Find regex matches in project files, with match coordinates.
- [search_file](tools/search_file.md) — Find files by glob pattern within the project.

## Shared response — `SearchResult`

| Name | Type | Description |
| --- | --- | --- |
| items* | array[SearchItem] | Matches (see below). Empty array when nothing matched. |
| more | boolean | Present only when `true`: the result was truncated (limit reached or timed out) — narrow with `--paths`/`--limit`. |
| partialResultReason | string? | Set when indexing was in progress, etc. |

### `SearchItem`
| Name | Type | Description |
| --- | --- | --- |
| filePath* | string | Project-relative path, `/` separators. |
| startLine | integer? | 1-based. Populated for content/symbol matches; omitted for `search_file` (path-only). |
| startColumn | integer? | 1-based. |
| endLine | integer? | 1-based; end exclusive. |
| endColumn | integer? | 1-based; end exclusive. |

There is **no** symbol name or kind field — disambiguate results by `filePath`.

# search_symbol
Find where symbols (classes, methods, fields) are declared, by identifier fragment.<br/>Semantic lookup via the IDE symbol index (Choose-By-Name over Goto Class + Goto Symbol) — it matches declaration **names**, not usages/comments/strings, so it ignores the text-search noise. Matching is fuzzy (camel-hump) and results come back **relevance-ranked**, exact/prefix hits first.<br/>Searches **project** symbols by default; if nothing suitable is found, retry with `include_external=true` to also search SDK and library symbols.<br/>Returns locations only (`filePath` + 1-based coordinates), no symbol name/kind — disambiguate by path. Response is the shared `SearchResult` (see reference/tools.md).<br/>A blank `q` is an MCP error. If `more:true` the list was truncated — narrow with `paths`/`limit`.

## Parameters
| Name | Type | Description |
| --- | --- | --- |
| q* | string | Symbol query text: a class/method/field name or identifier fragment. |
| paths | array[string]? | Project-relative glob patterns to scope results. `!` excludes; trailing `/`→`**`; no `/`→`**/pattern`; empty strings ignored. e.g. `["API/Data/Repositories/**"]`. |
| include_external | boolean | Include SDK/library symbols. Off by default — retry with `true` only if nothing project-local matches. |
| limit | integer | Max results (default 1000). |

## Output
See `SearchResult` / `SearchItem` in [../tools.md](../tools.md). `startLine`/coordinates point at the declaration.

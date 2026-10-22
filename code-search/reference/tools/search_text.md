# search_text
Find a literal text substring within project files, with match coordinates.<br/>Backed by Find-in-Project; case-sensitive, whole-file (not whole-word). Use for occurrences/usages of a string — NOT to locate a declaration (use `search_symbol` for that).<br/>Returns the shared `SearchResult`; each `SearchItem` carries 1-based start/end line/column (end exclusive). A blank `q` is an MCP error. `more:true` = truncated; narrow with `paths`/`limit`.

## Parameters
| Name | Type | Description |
| --- | --- | --- |
| q* | string | Text to search for (literal substring). |
| paths | array[string]? | Project-relative glob scope. `!` excludes; trailing `/`→`**`; no `/`→`**/pattern`. |
| limit | integer | Max results (default 1000). |

## Output
See `SearchResult` / `SearchItem` in [../tools.md](../tools.md); coordinates mark each match.

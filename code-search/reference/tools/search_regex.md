# search_regex
Find regex matches within project files, with match coordinates.<br/>Same Find-in-Project backend as `search_text` but the query is a regular expression. Use when you need pattern matching with coordinates; an invalid pattern returns an MCP error (`Invalid regex pattern: <q>`).<br/>Returns the shared `SearchResult`; each `SearchItem` carries 1-based start/end line/column (end exclusive). `more:true` = truncated; narrow with `paths`/`limit`.

## Parameters
| Name | Type | Description |
| --- | --- | --- |
| q* | string | Regex pattern to search for. |
| paths | array[string]? | Project-relative glob scope. `!` excludes; trailing `/`→`**`; no `/`→`**/pattern`. |
| limit | integer | Max results (default 1000). |

## Output
See `SearchResult` / `SearchItem` in [../tools.md](../tools.md); coordinates mark each match.

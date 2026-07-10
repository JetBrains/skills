# search_file
Find files by glob pattern within the project.<br/>Uses project indexes for content roots; set `includeExcluded=true` to also scan excluded/ignored roots. Use when you need to match file PATHS — not file contents (use `search_text`/`search_regex`) and not symbols (use `search_symbol`).<br/>Returns the shared `SearchResult`; for `search_file` only `filePath` is populated (no coordinates — there is no text match to report). A blank `q` is an MCP error. `more:true` = truncated; narrow with `paths`/`limit`.

## Parameters
| Name | Type | Description |
| --- | --- | --- |
| q* | string | Glob pattern, project-root-relative. Examples: `**/*.cs`, `API/**/Foo*.cs`, `Kavita.sln`. A pattern without `/` is treated as `**/pattern`. |
| paths | array[string]? | Additional project-relative glob filters. `!` excludes; trailing `/`→`**`. |
| includeExcluded | boolean | Include excluded/ignored files (default false). |
| limit | integer | Max results (default 1000). |

## Output
See `SearchResult` / `SearchItem` in [../tools.md](../tools.md); only `filePath` is set.

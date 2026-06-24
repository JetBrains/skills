# rename_refactoring
Counts in `affects` come from ReSharper's reference index, so they include language-aware usages such as `nameof(...)`, XML doc `<see cref>`, XAML `x:Name`, Razor, C++/Unreal references, and ordinary references.<br/>Pre-apply checks: newName must be a valid identifier for the target language; the backend applies language-specific escaping and validation.<br/>preview does NOT run conflict analysis — it resolves the symbol and counts `affects` only, so a clean preview can still fail on apply. `dryRun` is a deprecated alias of `preview`.<br/>Response: {ok, applied, touched, affects, files (first 20, solution-relative, '/' separators), moreFiles, conflicts, ambiguous, resolvedSymbol, note, error}.<br/>Outcomes:<br/>  * ok=true, applied=true, touched=N — rename applied; N = declarations + call sites the engine updated.<br/>  * ok=true, applied=false, touched=0 — preview succeeded; `affects` shows the blast radius the apply would touch.<br/>  * ok=false, ambiguous=[…] — query matched multiple candidates; use a more specific query (e.g. fully qualified name).<br/>  * ok=false, conflicts=[…] — a ReSharper conflict refused the rename; no files were touched. Adjust `newName` or refine `query`.<br/>  * ok=false, error={kind, hint} — kind is one of new_name_invalid, no_renamable_symbol, new_name_matches_current, symbol_invalidated, unknown.<br/>Only a blank query/newName is an MCP error.<br/>Use preview=true to audit `affects` before committing on public API renames or any rename that touches many cross-language references.

## Parameters
| Name | Type | Description |
| --- | --- | --- |
| query* | string | Symbol query: identifier or dotted path (e.g. 'Foo' or 'Namespace.Type.Method'). |
| newName* | string | Target identifier. The backend applies language-specific escaping and validation. |
| preview | boolean | Analyze only: resolve the symbol and return `affects` without mutating any files. Does NOT run conflict analysis. |
| dryRun | boolean | Deprecated alias of preview. |
| rootFolder | string | The path to the root folder of the Rider solution or project. Pass this value ALWAYS if you are aware of it. It reduces numbers of ambiguous calls.<br/>In the case you know only the current working directory you can use it as the root folder path.<br/>If you're not aware about the root folder path you can ask user about it. |

## Output
| Name | Type | Description |
| --- | --- | --- |
| ok* | boolean |  |
| applied* | boolean |  |
| touched* | integer |  |
| affects | object? |  |
| &nbsp;&nbsp;files* | integer |  |
| &nbsp;&nbsp;callSites* | integer |  |
| files* | array[string] |  |
| moreFiles* | integer |  |
| conflicts* | array[object] |  |
| &nbsp;&nbsp;[].kind* | string |  |
| &nbsp;&nbsp;[].file | string? |  |
| &nbsp;&nbsp;[].context* | string |  |
| ambiguous | array[object] |  |
| &nbsp;&nbsp;[].name* | string |  |
| &nbsp;&nbsp;[].kind* | string |  |
| &nbsp;&nbsp;[].fqn | string? |  |
| &nbsp;&nbsp;[].file* | string |  |
| &nbsp;&nbsp;[].line* | integer |  |
| resolvedSymbol | object? |  |
| &nbsp;&nbsp;name* | string |  |
| &nbsp;&nbsp;kind* | string |  |
| &nbsp;&nbsp;fqn | string? |  |
| &nbsp;&nbsp;file* | string |  |
| &nbsp;&nbsp;line* | integer |  |
| note | string? |  |
| error | object? |  |
| &nbsp;&nbsp;kind* | string |  |
| &nbsp;&nbsp;hint | string? |  |


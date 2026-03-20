# Tags, Links & Time Tracking

## Tags
- List all: `yt_get "tags?fields=id,name&query=<NAME>&$top=<N>"`
- List on issue: `yt_get "issues/PROJ-123/tags?fields=id,name"`
- Add: `yt_post "issues/PROJ-123/tags?fields=id,name" '{"id":"<TAG_DB_ID>"}'`
- Remove: `yt_delete "issues/PROJ-123/tags/<TAG_DB_ID>"`

## Issue links
- Link types: `yt_get "issueLinkTypes?fields=id,name,sourceToTarget,targetToSource,directed"`
- Get links for issue: `yt_get "issues/PROJ-123/links?fields=id,direction,linkType(name,sourceToTarget,targetToSource),issues(idReadable,summary)"`
- Add link: `yt_post "issues/PROJ-123/links/<LINK_ID>/issues?fields=idReadable,summary" '{"id":"<TARGET_ISSUE_DB_ID>"}'`

- Remove link: `yt_delete "issues/PROJ-123/links/<LINK_ID>/issues/<TARGET_ISSUE_DB_ID>"`

## Time tracking / work items
- List: `yt_get "issues/PROJ-123/timeTracking/workItems?fields=id,author(login,name),text,type(id,name),duration(minutes,presentation),date,created&$top=<N>&$skip=<N>"`
- Create: `yt_post "issues/PROJ-123/timeTracking/workItems?fields=id,duration(minutes,presentation),date,type(name),text" '{"duration":{"minutes":90},"text":"Implemented X","date":<TS_MS>,"type":{"id":"<WORK_TYPE_ID>"}}'`
- Update: `yt_post "issues/PROJ-123/timeTracking/workItems/<ITEM_ID>?fields=id,duration(minutes,presentation),text" '{"duration":{"minutes":120},"text":"Edited"}'`
- Delete: `yt_delete "issues/PROJ-123/timeTracking/workItems/<ITEM_ID>"`

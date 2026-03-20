# Issues, Drafts & Comments

- Search: `yt_get "issues?fields=idReadable,summary&$top=<N>&$skip=<N>" -G --data-urlencode "query=<QUERY>"`
- Get: `yt_get "issues/PROJ-123?fields=idReadable,summary,description,customFields(name,value(name)),tags(name)"`
- Create: `yt_post "issues?fields=idReadable,summary" '{"project":{"id":"<PROJECT_DB_ID>"},"summary":"Title","description":"Body"}'`
- Update summary/description: `yt_post "issues/PROJ-123?fields=idReadable,summary" '{"summary":"New","description":"Body"}'`
- Update custom fields: `yt_post "issues/PROJ-123?fields=customFields(name,value(name))" '{"customFields":[{"name":"State","$type":"StateIssueCustomField","value":{"name":"In Progress"}}]}'`
- Delete: `yt_delete "issues/PROJ-123"`

## Drafts
- Create: `yt_post "users/me/drafts?fields=id,idReadable,summary" '{"project":{"id":"<PROJECT_DB_ID>"},"summary":"Draft title"}'`
- Publish: `yt_post "issues?draftId=<DRAFT_DB_ID>&fields=idReadable,summary" '{}'`
- Read/update/delete: use issue endpoints with draft DB id (e.g., `2-24`).

## Comments
- List: `yt_get "issues/PROJ-123/comments?fields=id,text,author(login,name),created,updated&$top=<N>&$skip=<N>"`
- Get: `yt_get "issues/PROJ-123/comments/<COMMENT_ID>?fields=id,text,author(login,name),created,updated"`
- Create: `yt_post "issues/PROJ-123/comments?fields=id,text,author(login,name),created" '{"text":"Comment"}'`
- Update: `yt_post "issues/PROJ-123/comments/<COMMENT_ID>?fields=id,text,updated" '{"text":"Edited"}'`
- Delete: `yt_delete "issues/PROJ-123/comments/<COMMENT_ID>"`

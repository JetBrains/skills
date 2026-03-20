# Field reference

## Common `fields` presets
- Minimal issue: `idReadable,summary`
- Standard issue: `idReadable,summary,description,reporter(login,name),created,updated,resolved,customFields(name,value(name)),tags(name)`
- With comments: add `comments(id,text,author(login,name),created)`
- With links: add `links(id,direction,linkType(name),issues(idReadable,summary))`
- Comment: `id,text,author(login,name),created,updated`
- Work item: `id,author(login,name),text,type(name),duration(minutes,presentation),date,created`
- Tag: `id,name`
- User: `id,login,fullName,email`
- Group: `id,name,usersCount`

## Custom field `$type` values
- Single enum (Priority/Type): `SingleEnumIssueCustomField`
- Multi enum: `MultiEnumIssueCustomField`
- Single user (Assignee): `SingleUserIssueCustomField`
- Multi user: `MultiUserIssueCustomField`
- State: `StateIssueCustomField`
- Date: `DateIssueCustomField`
- Period: `PeriodIssueCustomField`
- Text: `TextIssueCustomField`
- Simple string/int/float: `SimpleIssueCustomField`

## Fetching allowed values for a project field

State and enum bundles are project-specific. Before updating a State or enum field, fetch the allowed values:

```
yt_get "admin/projects/<PROJECT_DB_ID>/customFields?fields=field(name),bundle(values(name))"
```

## Value shapes
- User fields: `{"login": "username"}`
- Enum/state fields: `{"name": "Value Name"}`

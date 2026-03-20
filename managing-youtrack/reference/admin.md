# Field Schema, Saved Queries, Users, Groups & Projects

## Custom fields
- Global list: `yt_get "admin/customFieldSettings/customFields?fields=id,name,fieldType(id)&$top=100"`
- Project fields + bundle values: `yt_get "admin/projects/<PROJECT_DB_ID>/customFields?fields=field(id,name,fieldType(id)),bundle(id,values(id,name))&$top=100"`
- Bundle values (enum/state/etc): `yt_get "admin/customFieldSettings/bundles/enum/<BUNDLE_ID>?fields=id,name,values(id,name,description)"`

## Saved queries
- List: `yt_get "savedQueries?fields=id,name,query&$top=<N>"`
- Get: `yt_get "savedQueries/<QUERY_ID>?fields=id,name,query"`

## Users
- Me: `yt_get "users/me?fields=id,login,fullName,email"`
- By login: `yt_get "users/<LOGIN>?fields=id,login,fullName,email"`
- List: `yt_get "users?fields=id,login,fullName&$top=<N>&$skip=<N>"`

## Groups
- List: `yt_get "groups?fields=id,name,usersCount&$top=<N>&$skip=<N>"`
- Details: `yt_get "groups/<GROUP_ID>?fields=id,name,usersCount,users(id,login,fullName),projects(id,name)"`

## Projects
- List: `yt_get "admin/projects?fields=id,name,shortName&$top=<N>&$skip=<N>"`
- Get: `yt_get "admin/projects/<PROJECT_DB_ID>?fields=id,name,shortName,lead(login,name),fields(field(name),defaultValues(name))"`

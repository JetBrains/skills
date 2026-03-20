# Output Formats

Most commands support multiple output formats for different use cases.

## Available Formats

| Format          | Flag          | Use Case                       |
|-----------------|---------------|--------------------------------|
| Table (default) | none          | Human-readable, colored output |
| Plain text      | `--plain`     | Scripting, parsing             |
| JSON            | `--json`      | Programmatic access            |
| No color        | `--no-color`  | Logs, CI environments          |
| No header       | `--no-header` | Clean output for piping        |

## JSON Output

**Default JSON (all fields):**
```bash
teamcity run list --json
```

**List available fields:**
```bash
teamcity run list --json=
```

**Select specific fields:**
```bash
teamcity run list --json=id,status,webUrl
```

**Nested fields (dot notation):**
```bash
teamcity run list --json=id,buildType.name,triggered.user.username
```

## Available JSON Fields by Command

| Command        | Example fields                                                                                                                                                                                        |
|----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `run list`     | `id`, `number`, `status`, `state`, `branchName`, `buildTypeId`, `buildType.name`, `buildType.projectName`, `triggered.type`, `triggered.user.name`, `agent.name`, `startDate`, `finishDate`, `webUrl` |
| `job list`     | `id`, `name`, `projectName`, `projectId`, `paused`, `href`, `webUrl`                                                                                                                                  |
| `project list` | `id`, `name`, `description`, `parentProjectId`, `href`, `webUrl`                                                                                                                                      |
| `queue list`   | `id`, `buildTypeId`, `state`, `branchName`, `queuedDate`, `buildType.name`, `triggered.user.name`, `webUrl`                                                                                           |
| `agent list`   | `id`, `name`, `connected`, `enabled`, `authorized`, `pool.name`, `webUrl`                                                                                                                             |
| `pool list`    | `id`, `name`, `maxAgents`                                                                                                                                                                             |

Run `teamcity <command> --json=` to see all available fields for that command.

## Scripting Examples

**Get build IDs of failed builds:**
```bash
teamcity run list --status failure --plain --no-header | awk '{print $1}'
```

**JSON with jq:**
```bash
teamcity run list --json | jq '.[] | {id, status, branch}'
```

**Get build IDs that failed (JSON):**
```bash
teamcity run list --status failure --json=id | jq -r '.[].id'
```

**Export runs to CSV:**
```bash
teamcity run list --json=id,status,branchName | jq -r '.[] | [.id,.status,.branchName] | @csv'
```

**Filter builds by pattern:**
```bash
teamcity run list --json | jq '.[] | select(.branch | contains("feature"))'
```

**Count builds by status:**
```bash
teamcity run list --json | jq 'group_by(.status) | map({status: .[0].status, count: length})'
```

**Get web URLs for queued builds:**
```bash
teamcity queue list --json=webUrl | jq -r '.[].webUrl'
```

## Environment Variables

For non-interactive use (CI/CD, scripts):

```bash
export TEAMCITY_URL="https://teamcity.example.com"
export TEAMCITY_TOKEN="your-api-token"

# Commands will use these automatically
teamcity run list
```

Environment variables always take precedence over config file settings.

## Combining with Other Tools

**Open in browser:**
```bash
teamcity run view <id> -w
```

**Pipe to less with color:**
```bash
teamcity run list | less -R
```

**Watch and notify:**
```bash
teamcity run watch <id> && notify-send "Build complete"
```

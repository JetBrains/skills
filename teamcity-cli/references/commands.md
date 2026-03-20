# Command Reference

## Authentication (`teamcity auth`)

| Command                        | Description                       |
|--------------------------------|-----------------------------------|
| `teamcity auth login -s <url>` | Authenticate with TeamCity server |
| `teamcity auth logout`         | Log out from current server       |
| `teamcity auth status`         | Show auth status and server info  |

Login options:
- `-s, --server <url>` - TeamCity server URL
- `-t, --token <token>` - Access token
- `--insecure-storage` - Store token in plain text config file instead of system keyring

## Builds/Runs (`teamcity run`)

| Command                          | Description              |
|----------------------------------|--------------------------|
| `teamcity run list`              | List recent builds       |
| `teamcity run view <id>`         | View build details       |
| `teamcity run start <job-id>`    | Start a new build        |
| `teamcity run cancel <id>`       | Cancel a build           |
| `teamcity run restart <id>`      | Restart a build          |
| `teamcity run watch <id>`        | Watch build in real-time |
| `teamcity run log <id>`          | View build log           |
| `teamcity run tests <id>`        | View test results        |
| `teamcity run changes <id>`      | View VCS changes         |
| `teamcity run artifacts <id>`    | List artifacts           |
| `teamcity run download <id>`     | Download artifacts       |
| `teamcity run pin <id>`          | Pin build                |
| `teamcity run unpin <id>`        | Unpin build              |
| `teamcity run tag <id> <tags>`   | Add tags                 |
| `teamcity run untag <id> <tags>` | Remove tags              |
| `teamcity run comment <id>`      | Manage comments          |

### Flags for `teamcity run list`

Shows all branches and all build states (including canceled, personal, composite sub-builds) by default — matching the TeamCity UI. Use `--branch` to narrow to a specific branch.

- `-j, --job <id>` - Filter by job
- `-b, --branch <name>` - Filter by branch
- `--status <status>` - Filter: success, failure, running, error, unknown
- `-u, --user <name>` - Filter by user
- `-p, --project <id>` - Filter by project
- `-n, --limit <n>` - Limit results (default: 30)
- `--since <time>` - Since time (e.g., 24h, 2026-01-01)
- `--until <time>` - Until time (e.g., 12h, 2026-01-02)
- `--json` - JSON output (use `--json=` to list fields, `--json=f1,f2` for specific)
- `--plain` - Plain text output for scripting
- `--no-header` - Omit header row (use with --plain)
- `-w, --web` - Open in browser

### Flags for `teamcity run start`

- `-b, --branch <name>` - Branch to build
- `--revision <sha>` - Pin build to a specific Git commit SHA
- `-P, --param <k=v>` - Build parameter (repeatable)
- `-S, --system <k=v>` - System property (repeatable)
- `-E, --env <k=v>` - Environment variable (repeatable)
- `-t, --tag <tag>` - Add tag (repeatable)
- `-m, --comment <text>` - Run comment
- `--watch` - Watch after starting
- `--clean` - Clean checkout
- `--agent <id>` - Run on specific agent
- `--personal` - Run as personal build
- `-l, --local-changes` - Include local changes (git, -, or path)
- `--no-push` - Skip auto-push of branch to remote
- `--rebuild-deps` - Rebuild all dependencies
- `--rebuild-failed-deps` - Rebuild failed/incomplete dependencies
- `--top` - Add to top of queue
- `-n, --dry-run` - Show what would be triggered without running
- `--json` - Output as JSON (for scripting)
- `-w, --web` - Open run in browser

### Flags for `teamcity run log`

- `--failed` - Show failure summary (problems and failed tests)
- `-j, --job <id>` - Get log for latest run of this job
- `--raw` - Show raw log without formatting

### Flags for `teamcity run watch`

- `-i, --interval <s>` - Refresh interval in seconds
- `--logs` - Stream build logs while watching
- `-Q, --quiet` - Minimal output, show only state changes and result
- `--timeout <duration>` - Timeout duration (e.g., 30m, 1h)

### Flags for `teamcity run view`

- `--json` - Output as JSON
- `-w, --web` - Open in browser

### Flags for `teamcity run tests`

- `--failed` - Show only failed tests
- `-j, --job <id>` - Get tests for latest run of this job
- `--json` - Output as JSON
- `-n, --limit <n>` - Maximum number of tests to show

### Flags for `teamcity run changes`

- `--json` - Output as JSON
- `--no-files` - Hide file list, show commits only

### Flags for `teamcity run artifacts`

- `-j, --job <id>` - List artifacts from latest run of this job
- `-p, --path <subdir>` - Browse artifacts under this subdirectory
- `--json` - Output as JSON

### Flags for `teamcity run download`

- `-a, --artifact <pattern>` - Artifact name pattern to download
- `-d, --dir <path>` - Directory to download artifacts to

### Flags for `teamcity run cancel`

- `--comment <text>` - Comment for cancellation
- `-f, --force` - Skip confirmation prompt

### Flags for `teamcity run restart`

- `--watch` - Watch the new run after restarting
- `-w, --web` - Open run in browser

### Flags for `teamcity run pin`

- `-m, --comment <text>` - Comment explaining why the run is pinned

### Flags for `teamcity run comment`

- `--delete` - Delete the comment

## Jobs (`teamcity job`)

| Command                              | Description               |
|--------------------------------------|---------------------------|
| `teamcity job list`                        | List build configurations      |
| `teamcity job view <id>`                   | View job details               |
| `teamcity job tree <id>`                   | Show snapshot dependency tree  |
| `teamcity job pause <id>`                  | Pause job                      |
| `teamcity job resume <id>`                 | Resume job                     |
| `teamcity job param list <id>`             | List parameters                |
| `teamcity job param get <id> <name>`       | Get parameter                  |
| `teamcity job param set <id> <name> <val>` | Set parameter                  |
| `teamcity job param delete <id> <name>`    | Delete parameter               |

### Flags for `teamcity job list`

- `--json` - JSON output (use `--json=` to list fields, `--json=f1,f2` for specific)
- `-n, --limit <n>` - Maximum number of jobs
- `-p, --project <id>` - Filter by project ID

### Flags for `teamcity job view`

- `--json` - Output as JSON
- `-w, --web` - Open in browser

### Flags for `teamcity job tree`

- `-d, --depth <n>` - Limit tree depth (0 = unlimited)
- `--only <type>` - Show only `dependents` or `dependencies`

### Flags for `teamcity job param list`

- `--json` - Output as JSON

### Flags for `teamcity job param set`

- `--secure` - Mark as secure/password parameter

## Projects (`teamcity project`)

| Command                                        | Description                  |
|------------------------------------------------|------------------------------|
| `teamcity project list`                        | List projects                |
| `teamcity project view <id>`                   | View project details         |
| `teamcity project tree [id]`                   | Show project hierarchy tree  |
| `teamcity project param list <id>`             | List parameters              |
| `teamcity project param get <id> <name>`       | Get parameter                |
| `teamcity project param set <id> <name> <val>` | Set parameter                |
| `teamcity project param delete <id> <name>`    | Delete parameter             |
| `teamcity project token put <id>`              | Store secret, get token      |
| `teamcity project token get <id> <token>`      | Retrieve secret              |
| `teamcity project settings export <id>`        | Export settings as ZIP       |
| `teamcity project settings status <id>`        | Show versioned settings sync |
| `teamcity project settings validate`           | Validate Kotlin DSL config   |

### Flags for `teamcity project tree`

- `-d, --depth <n>` - Limit tree depth (0 = unlimited)
- `--no-jobs` - Hide build configurations

### Flags for `teamcity project list`

- `--json` - JSON output (use `--json=` to list fields, `--json=f1,f2` for specific)
- `-n, --limit <n>` - Maximum number of projects
- `-p, --parent <id>` - Filter by parent project ID

### Flags for `teamcity project view`

- `--json` - Output as JSON
- `-w, --web` - Open in browser

### Flags for `teamcity project param list`

- `--json` - Output as JSON

### Flags for `teamcity project param set`

- `--secure` - Mark as secure/password parameter

### Flags for `teamcity project settings export`

- `--kotlin` - Export as Kotlin DSL (default)
- `--xml` - Export as XML
- `-o, --output <path>` - Output file path (default: projectSettings.zip)
- `--relative-ids` - Use relative IDs in exported settings

### Flags for `teamcity project settings status`

- `--json` - Output as JSON

### Flags for `teamcity project settings validate`

- `-v, --verbose` - Show full Maven output

### Flags for `teamcity project token put`

- `--stdin` - Read value from stdin

## Queue (`teamcity queue`)

| Command                       | Description           |
|-------------------------------|-----------------------|
| `teamcity queue list`         | List queued builds    |
| `teamcity queue remove <id>`  | Remove from queue     |
| `teamcity queue top <id>`     | Move to top of queue  |
| `teamcity queue approve <id>` | Approve waiting build |

### Flags for `teamcity queue list`

- `-j, --job <id>` - Filter by job ID
- `--json` - JSON output (use `--json=` to list fields, `--json=f1,f2` for specific)
- `-n, --limit <n>` - Maximum number of queued runs

### Flags for `teamcity queue remove`

- `-f, --force` - Skip confirmation prompt

## Agents (`teamcity agent`)

| Command                           | Description                       |
|-----------------------------------|-----------------------------------|
| `teamcity agent list`             | List build agents                 |
| `teamcity agent view <id>`        | View agent details                |
| `teamcity agent authorize <id>`   | Authorize agent to run builds     |
| `teamcity agent deauthorize <id>` | Revoke agent authorization        |
| `teamcity agent enable <id>`      | Enable agent                      |
| `teamcity agent disable <id>`     | Disable agent                     |
| `teamcity agent move <id> <pool>` | Move agent to different pool      |
| `teamcity agent jobs <id>`        | List compatible/incompatible jobs |
| `teamcity agent exec <id> <cmd>`  | Execute command on agent          |
| `teamcity agent term <id>`        | Open interactive shell on agent   |
| `teamcity agent reboot <id>`      | Reboot a build agent              |

### Flags for `teamcity agent list`

- `-p, --pool <name>` - Filter by agent pool
- `--connected` - Show only connected agents
- `--enabled` - Show only enabled agents
- `--authorized` - Show only authorized agents
- `-n, --limit <n>` - Limit results
- `--json` - JSON output (use `--json=` to list fields, `--json=f1,f2` for specific)

### Flags for `teamcity agent view`

- `--json` - Output as JSON
- `-w, --web` - Open in browser

### Flags for `teamcity agent jobs`

- `--incompatible` - Show incompatible jobs with reasons
- `--json` - Output as JSON

### Flags for `teamcity agent exec`

- `--timeout <duration>` - Command timeout

### Flags for `teamcity agent reboot`

- `--after-build` - Wait for current build to finish before rebooting
- `-y, --yes` - Skip confirmation prompt

## Agent Pools (`teamcity pool`)

| Command                          | Description              |
|----------------------------------|--------------------------|
| `teamcity pool list`                   | List agent pools         |
| `teamcity pool view <id>`              | View pool details        |
| `teamcity pool link <id> <project>`    | Link project to pool     |
| `teamcity pool unlink <id> <project>`  | Unlink project from pool |

### Flags for `teamcity pool list`

- `--json` - JSON output (use `--json=` to list fields, `--json=f1,f2` for specific)

### Flags for `teamcity pool view`

- `--json` - Output as JSON
- `-w, --web` - Open in browser

## Direct API (`teamcity api`)

For features not covered by specific commands. Endpoints always start with `/app/rest/`.

```bash
# GET request
teamcity api /app/rest/server

# POST request
teamcity api /app/rest/buildQueue -X POST -f 'buildType=id:MyBuild'

# With pagination
teamcity api /app/rest/builds --paginate --slurp

# Browse artifact subdirectory
teamcity api /app/rest/builds/id:BUILD_ID/artifacts/children/SUBPATH
```

### Flags

- `-X, --method <method>` - HTTP method
- `-H, --header <h>` - Custom header (repeatable)
- `-f, --field <k=v>` - Body field (builds JSON)
- `--input <file>` - Read body from file (use - for stdin)
- `--paginate` - Fetch all pages
- `--slurp` - Combine pages into array (requires --paginate)
- `--raw` - Output raw response without formatting
- `--silent` - Suppress output on success
- `-i, --include` - Include response headers in output

## Global Flags

Available on all commands:

- `-h, --help` - Help for command
- `-v, --version` - Version information
- `--no-color` - Disable colored output
- `-q, --quiet` - Suppress non-essential output
- `--verbose` - Show detailed output including debug info
- `--no-input` - Disable interactive prompts
- `-w, --web` - Open in browser (on view commands)

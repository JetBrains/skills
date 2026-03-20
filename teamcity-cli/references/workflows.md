# Common Workflows

## Inspecting a Build from a TeamCity URL

When a user provides a TeamCity URL, parse it and map to `teamcity` commands.

**Format 1: Specific build** — `https://host/buildConfiguration/ConfigId/12345`
```bash
# Extract build ID (last numeric path segment): 12345
teamcity run view 12345
# If failed:
teamcity run log 12345 --failed
teamcity run tests 12345 --failed
```

**Format 2: Build configuration** — `https://host/buildConfiguration/ConfigId`
```bash
# Extract config ID (last non-numeric path segment): ConfigId
teamcity run list --job ConfigId
```

**Format 3: Project** — `https://host/project/ProjectId`
```bash
# Extract project ID: ProjectId
teamcity job list --project ProjectId
```

Strip query params (`?mode=builds`) and fragments (`#all-projects`) before parsing.

## Investigating a Build Failure

When a build has **FAILURE** status, proactively suggest: `teamcity run log <id> --failed` (failure summary), `teamcity run tests <id> --failed` (failed tests), `teamcity run changes <id>` (triggering changes).

For **composite/matrix builds** (snapshot dependencies, no agent), find failed children with `teamcity run list --status failure` and appropriate filters.

1. **Find the failed build:**
   ```bash
   teamcity run list --status failure -n 10
   ```

2. **View build details:**
   ```bash
   teamcity run view <run-id>
   ```

3. **Check the build log:**
   ```bash
   teamcity run log <run-id>
   ```

   For failed steps only:
   ```bash
   teamcity run log <run-id> --failed
   ```

4. **View test results:**
   ```bash
   teamcity run tests <run-id>
   ```

   For failed tests only:
   ```bash
   teamcity run tests <run-id> --failed
   ```

5. **See what changes triggered the build:**
   ```bash
   teamcity run changes <run-id>
   ```

## Starting and Monitoring Builds

**Start a build:**
```bash
teamcity run start <job-id>
```

**Start with specific branch:**
```bash
teamcity run start <job-id> --branch feature/my-branch
```

**Start with parameters:**
```bash
teamcity run start <job-id> -P "param1=value1" -P "param2=value2"
```

**Start with env vars and system properties:**
```bash
teamcity run start <job-id> -P version=1.0 -S build.number=123 -E CI=true
```

**Start and watch:**
```bash
teamcity run start <job-id> --watch
```

**Start with comment and tags:**
```bash
teamcity run start <job-id> --comment "Release build" --tag release --tag v1.0
```

**Start with clean checkout and rebuild deps:**
```bash
teamcity run start <job-id> --clean --rebuild-deps --top
```

**Dry run (see what would be triggered):**
```bash
teamcity run start <job-id> --dry-run
```

**Watch an existing build:**
```bash
teamcity run watch <run-id>
```

**Stream logs while watching:**
```bash
teamcity run watch <run-id> --logs
```

**Watch with timeout:**
```bash
teamcity run watch <run-id> --timeout 30m --quiet
```

## Personal Builds (Local Changes)

**Run build with uncommitted git changes:**
```bash
teamcity run start <job-id> --local-changes
```

**Run build from a patch file:**
```bash
teamcity run start <job-id> --local-changes changes.patch
```

**Personal build with specific branch:**
```bash
teamcity run start <job-id> --personal --branch my-feature --watch
```

**Skip auto-push:**
```bash
teamcity run start <job-id> --local-changes --no-push
```

## Finding Jobs and Projects

**List all projects:**
```bash
teamcity project list
```

**List sub-projects:**
```bash
teamcity project list --parent <project-id>
```

**List jobs in a project:**
```bash
teamcity job list --project <project-id>
```

**View job details:**
```bash
teamcity job view <job-id>
```

**Search for a job by name:**
```bash
teamcity job list --json | jq '.[] | select(.name | contains("deploy"))'
```

## Working with Build Artifacts

**List artifacts from a build:**
```bash
teamcity run artifacts <run-id>
```

**List artifacts from latest build of a job:**
```bash
teamcity run artifacts --job <job-id>
```

**Download all artifacts:**
```bash
teamcity run download <run-id>
```

**Download to specific directory:**
```bash
teamcity run download <run-id> --dir ./artifacts
```

**Download specific artifact:**
```bash
teamcity run download <run-id> --artifact "*.jar"
```

**Browse artifact subdirectory:**
```bash
teamcity api /app/rest/builds/id:<run-id>/artifacts/children/html_reports
```

## Build Metadata

**Pin a build (prevent cleanup):**
```bash
teamcity run pin <run-id> --comment "Release candidate"
```

**Unpin a build:**
```bash
teamcity run unpin <run-id>
```

**Tag a build:**
```bash
teamcity run tag <run-id> deployed production
```

**Remove tags:**
```bash
teamcity run untag <run-id> deployed
```

**Add a comment:**
```bash
teamcity run comment <run-id> "Verified by QA"
```

**View existing comment:**
```bash
teamcity run comment <run-id>
```

**Delete a comment:**
```bash
teamcity run comment <run-id> --delete
```

## Managing the Build Queue

**View queued builds:**
```bash
teamcity queue list
```

**Filter queue by job:**
```bash
teamcity queue list --job <job-id>
```

**Move a build to top of queue:**
```bash
teamcity queue top <run-id>
```

**Remove from queue:**
```bash
teamcity queue remove <run-id>
```

**Approve a build waiting for approval:**
```bash
teamcity queue approve <run-id>
```

## Managing Job and Project Parameters

**List job parameters:**
```bash
teamcity job param list <job-id>
```

**Set a parameter:**
```bash
teamcity job param set <job-id> MY_PARAM "my value"
```

**Set a secure parameter:**
```bash
teamcity job param set <job-id> SECRET_KEY "****" --secure
```

**Get a parameter:**
```bash
teamcity job param get <job-id> MY_PARAM
```

**Delete a parameter:**
```bash
teamcity job param delete <job-id> MY_PARAM
```

Project parameters work the same way with `teamcity project param`.

## Validating Kotlin DSL Locally

**Always use `teamcity project settings validate`** to verify Kotlin DSL — never generic `mvn compile`.

Under the hood it runs `mvn teamcity-configs:generate` (or `./mvnw` when available) inside the `.teamcity/` directory, which is the only correct DSL validation step. Generic Maven commands like `mvn compile` do **not** validate TeamCity DSL and will give misleading results.

```bash
# Preferred — auto-detects .teamcity dir and Maven wrapper
teamcity project settings validate

# Explicit path
teamcity project settings validate ./path/to/.teamcity

# Show full Maven output for debugging
teamcity project settings validate --verbose
```

If you need the raw Maven command (e.g., in CI without the CLI installed):
```bash
./mvnw teamcity-configs:generate -f .teamcity/pom.xml   # prefer wrapper
mvn teamcity-configs:generate -f .teamcity/pom.xml       # fallback
```

## Project Settings (Export & Status)

**Check versioned settings sync status (requires server connection):**
```bash
teamcity project settings status <project-id>
```

**Export project settings as Kotlin DSL:**
```bash
teamcity project settings export <project-id>
```

**Export as XML:**
```bash
teamcity project settings export <project-id> --xml -o settings.zip
```

## Secure Tokens

**Store a secret and get a token reference:**
```bash
teamcity project token put <project-id> "my-secret-password"
```

**Store from stdin (for piping):**
```bash
echo -n "my-secret" | teamcity project token put <project-id> --stdin
```

**Retrieve a token value (requires System Admin):**
```bash
teamcity project token get <project-id> "credentialsJSON:abc123..."
```

## Managing Agents

**List all agents:**
```bash
teamcity agent list
```

**List connected agents only:**
```bash
teamcity agent list --connected
```

**Filter agents by pool:**
```bash
teamcity agent list --pool Default
```

**View agent details:**
```bash
teamcity agent view <agent-id>
```

**See what jobs an agent can run:**
```bash
teamcity agent jobs <agent-id>
```

**See why jobs are incompatible with an agent:**
```bash
teamcity agent jobs <agent-id> --incompatible
```

**Enable/disable an agent:**
```bash
teamcity agent enable <agent-id>
teamcity agent disable <agent-id>
```

**Authorize/deauthorize an agent:**
```bash
teamcity agent authorize <agent-id>
teamcity agent deauthorize <agent-id>
```

**Move agent to a different pool:**
```bash
teamcity agent move <agent-id> <pool-id>
```

**Reboot an agent:**
```bash
teamcity agent reboot <agent-id>
```

**Reboot after current build finishes:**
```bash
teamcity agent reboot <agent-id> --after-build
```

## Remote Agent Access

**Open interactive shell on an agent:**
```bash
teamcity agent term <agent-id>
```

**Execute a command on an agent:**
```bash
teamcity agent exec <agent-id> "ls -la"
```

**Execute with timeout:**
```bash
teamcity agent exec <agent-id> --timeout 10m -- long-running-script.sh
```

## Managing Agent Pools

**List all pools:**
```bash
teamcity pool list
```

**View pool details:**
```bash
teamcity pool view <pool-id>
```

**Link a project to a pool:**
```bash
teamcity pool link <pool-id> <project-id>
```

**Unlink a project from a pool:**
```bash
teamcity pool unlink <pool-id> <project-id>
```

## Tips

1. **Use `--json` for programmatic access** - Parse with `jq` for complex queries

1. **Use `teamcity api` as escape hatch** - When a specific command doesn't exist, use raw API access

1. **Environment variables** - Set `TEAMCITY_URL` and `TEAMCITY_TOKEN` for non-interactive use

1. **Open in browser** - Most view commands support `-w` to open in web browser

1. **Auto-detection from DSL** – When working in a project with Kotlin DSL config, the server URL is auto-detected from `.teamcity/pom.xml`

1. **Multiple servers** - Use `TEAMCITY_URL` env var to switch between servers, or `teamcity auth login --server <url>` to add servers

## Troubleshooting

| Symptom                      | Likely Cause              | Action                                                                                  |
|------------------------------|---------------------------|-----------------------------------------------------------------------------------------|
| `401 Unauthorized`           | Invalid or expired token  | Run `teamcity auth status` to check; re-login with `teamcity auth login`                |
| `403 Forbidden`              | Insufficient permissions  | Build config may require different access rights; check with TeamCity admin             |
| `404 Not Found`              | Build deleted or wrong ID | Verify the build ID/URL; the build may have been cleaned up                             |
| Connection refused / timeout | Server unreachable        | Check if TeamCity instance is accessible; verify server URL with `teamcity auth status` |
| `No server configured`       | Missing auth config       | Run `teamcity auth login -s <url>` or set `TEAMCITY_URL` and `TEAMCITY_TOKEN` env vars  |

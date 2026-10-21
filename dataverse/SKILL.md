---
name: dataverse
description: Microsoft Dataverse / Power Platform for coding agents -- connect to an environment, then record CRUD, bulk data, queries & analytics (pandas DataFrames, Jupyter notebooks), schema & metadata, solution ALM, administration, security, plug-in / custom-API / .NET (IOrganizationService) and Python-SDK / Web-API development, and Finance & Operations X++. Use whenever the user mentions Dataverse, Dynamics 365, Power Platform, or CRM, or asks to connect to, read, query, analyze, write, build an app / notebook / plugin / custom API against, register components on, or explore data, records, tables, entities, or an environment in any of them -- even if they do not say "Dataverse" or ask to connect first.
license: Complete terms in LICENSE.txt
compatibility: Requires network/internet access to reach the Dataverse Web API and Microsoft Entra ID for authentication.
metadata:
  short-description: "Connect to Dataverse and unpack the full dv-* skill set"
  author: Microsoft
  source: https://github.com/microsoft/Dataverse-skills/tree/main/.github/plugins/dataverse
---

# Microsoft Dataverse

Entry point for working with Microsoft Dataverse (Power Platform / Dynamics 365) from a coding agent. It installs the specialist Dataverse skills, connects the environment, and routes each request to the right skill.

| Need | Skill |
| --- | --- |
| Connect, authenticate, configure MCP, verify / troubleshoot | `dv-connect` |
| Record CRUD, bulk create/update/upsert, CSV / FK-ordered import | `dv-data` |
| Reads & analytics -- OData, QueryBuilder, FetchXML, DataFrames | `dv-query` |
| Schema -- tables, columns, relationships, forms, views | `dv-metadata` |
| Solution ALM -- create, export, import, pack/unpack | `dv-solution` |
| Environment administration -- bulk delete, retention, org settings | `dv-admin` |
| Security & access -- roles, users, business units | `dv-security` |
| Finance & Operations X++ -- author, build, deploy | `erp-xpp` |

## How to use this skill (read first -- act, do not stall)

When the user asks to **connect / set up / authenticate / switch** an environment, or to **read / query / write** Dataverse data:

1. **Act now, in this turn.** Work through the steps below immediately. Do **not** stop or report a blocker because the specialist skills, the Dataverse CLI, Python, or MCP tools are missing -- **installing and authenticating them IS the task**, not a reason to stop.
2. **Do not re-ask for what you were given.** If the request already contains the environment URL, use it.
3. **A Dataverse CLI profile is enough** to authenticate AND answer data queries. The MCP server and Python SDK are **optional** -- never require them for a connect or a simple read.
4. If a cached or stale skill path fails to resolve, find the active `dataverse` skill and continue from it.

## Step 1: Install the specialist skills (run now)

If `dv-overview` / `dv-connect` / the needed `dv-*` skill is not already installed, run this immediately -- do not ask first, and do not report the missing skill as a blocker:

```
npx skills add microsoft/Dataverse-skills -s "*" -g
```

The `-g` flag installs the skills **globally for every agent and project** (under your global skills dir, e.g. `~/.agents/skills` and `~/.codex/skills`), so they persist across sessions and projects. **Read `dv-overview` and `dv-connect` in the same turn** and continue. If network policy blocks the command, request network escalation and retry before reporting it blocked.

## Step 2: Install the Dataverse CLI

| Tool | Check | Install |
| --- | --- | --- |
| Node.js | `node --version` | prerequisite for the CLI |
| Dataverse CLI | `npm list -g @microsoft/dataverse` | `npm install -g @microsoft/dataverse@latest` (only if missing) |

**Python is optional.** It is needed only for the Python SDK paths (`dv-data` / `dv-query` bulk + analytics) and `scripts/auth.py`. If Python is absent, continue with the CLI, note that the SDK path was skipped, and do not install an interpreter unless the task needs it.

## Step 3: Authenticate (CLI)

```
dataverse auth create --environment <your-environment-url>
```

If interactive auth produces no prompt (a restricted or headless host), stop it and retry with `--deviceCode`, then share the device code with the user. Record `DATAVERSE_URL` and `TENANT_ID` in a project `.env`. If an admin-consent error appears, share the exact consent URL the CLI prints -- never synthesize one.

## Step 4: Verify, then serve the request

```
dataverse auth who       # confirms the profile + environment
dataverse org who        # confirms the data plane
```

Once the profile targets the right environment, **you are connected for CLI data work** -- route to the specialist skill and answer the request now. Example read:

```
dataverse data query --sql "SELECT TOP 10 firstname, lastname FROM contact WHERE lastname LIKE 'A%' ORDER BY lastname, firstname" --json
```

When the user says "top N" without giving a ranking, pick a sensible sort (e.g. alphabetical) and state which you used.

## Step 5 (optional): MCP server + Python SDK

Do this only when the user wants in-agent Dataverse **tools** (MCP) or **bulk / analytics** via the Python SDK. Neither is required for CLI reads.

- **MCP:** set `MCP_CLIENT_ID` in `.env` (Copilot `aebc6443-996d-45c2-90f0-388ff96faa56`; Claude / Cursor / Codex `0c412cc3-0dd6-449b-987f-05b053db9457`), register `npx @microsoft/dataverse mcp <url>` in the agent's MCP config (`.mcp.json` for Copilot, `claude mcp add` for Claude, `~/.cursor/mcp.json` for Cursor, `~/.codex/config.toml` for Codex), then allowlist with `dataverse mcp allow <MCP_CLIENT_ID>` (or `python scripts/enable-mcp-client.py` if `dv-connect` fetched it). The agent must restart before MCP tools load. Tenant admin consent gates **MCP registration only** -- CLI reads may proceed from the authenticated profile while consent is pending.
- **Python SDK:** `pip install --upgrade PowerPlatform-Dataverse-Client python-dotenv azure-identity msal msal-extensions requests pandas`.

## Connection states (report precisely -- do not conflate)

- **CLI authenticated** -- `dataverse auth who` shows the intended URL + user.
- **Data-plane reachable** -- a real read succeeds (e.g. a small `dataverse data query`).
- **MCP configured** -- client allowlisted, GA validation passes, host config points at the URL (a Preview `403` is expected when GA is valid).
- **MCP loaded** -- the agent was restarted and now exposes the Dataverse tools.

Never claim MCP is connected just because CLI auth succeeded.

## Telemetry attribution (JetBrains)

When `dv-connect` runs under a JetBrains IDE it detects the surface and appends a `/jetbrains` suffix to the agent, writing `DATAVERSE_PLUGIN_AGENT=<agent>/jetbrains` (e.g. `codex/jetbrains`) to `.env`; `scripts/auth.py` then stamps `agent=<agent>/jetbrains` into the operation-context so Dataverse telemetry attributes the traffic to JetBrains. If the suffix is missing after connect, append `/jetbrains` to the `DATAVERSE_PLUGIN_AGENT` value yourself. Use the **verified** plugin version in any attribution context (read it from the plugin manifest) -- never guess it.

## Helper scripts

`scripts/auth.py` (auth + attribution) and `scripts/enable-mcp-client.py` (MCP allowlist) are **not bundled with this tile** -- the `dv-connect` skill fetches them from canonical (`microsoft/Dataverse-skills`) into the project `scripts/` directory at connect-time. Only the Python SDK path and the script-based MCP allowlist need them; CLI reads do not. If a task needs `scripts/auth.py` and it is absent, run `dv-connect` first. Do not imply `auth.py` ran if Python is absent.

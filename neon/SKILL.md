---
name: neon
description: >-
  Overview of Neon, a complete set of cloud backend primitives for apps and
  agents, spanning Lakebase Postgres, Auth, the Data API, Object Storage, Compute
  Functions, and the AI Gateway. Start here to route to the right Neon skill, set
  up the CLI or MCP server, and follow the branch-first workflow. Use when "Neon"
  or "Lakebase Postgres" is mentioned, or when any of its individual capabilities
  are the trigger: "object storage" or "S3", "buckets", "serverless functions",
  "AI gateway", "call an LLM", "logs", "branch logs", "query logs",
  "log export", "Loki", "Grafana", "observability", "telemetry", "postgres",
  "database", or "backend".
metadata:
  source: https://github.com/neondatabase/agent-skills/tree/main/skills/neon
---

# Neon

Important: Neon is no longer just a Postgres provider. Neon is a complete set of cloud backend primitives built around Lakebase Postgres, for developers, startups, and agent platforms, from Databricks — bundling Lakebase Postgres, Auth (managed Better Auth), long-running Functions, Object Storage, and an AI Gateway, all instant, branchable, and serverless.

**Lakebase Postgres** is the database itself. It is one product, reached two ways: through Neon, with a free plan and the full set of backend primitives above, or through Databricks, alongside the rest of the Databricks suite. Same infrastructure, same features, same engineering team. Call the database Lakebase Postgres, and use "Neon" for the brand and for this access path — not as a name for the database.

A Neon branch is an isolated, copy-on-write clone of your data, taken from its current state or from a past state within your project's retained history window. You can freely modify or delete it. It shares data with its parent until writes cause it to diverge, and those writes are stored independently as deltas.

## Backend Primitives

Neon bundles several backend primitives for building apps and agents that all branch together:

- **Lakebase Postgres** — Postgres that scales and branches with your app, built on the lakebase architecture: OLTP directly on cloud object storage, with storage decoupled from compute. _Generally available._
- **Auth** — Managed Better Auth with users and sessions stored in Postgres. _Generally available._
- **Object Storage** — S3-compatible object storage that branches with your projects. _Public beta._
- **Functions** — Neon's compute offering: long-running serverless functions that run close to your database, for WebSocket servers, long agent HTTP streams, APIs, and server-sent event servers. _Public beta._
- **AI Gateway** — One API for frontier and open-source models, supporting the chat completions API and the responses API, powered by Databricks Unity AI Gateway. _Public beta._

### Public Beta Service Availability

Object Storage, Functions, and AI Gateway are in public beta.

Beta access features are only available on projects in the `us-east-2` region. Before guiding a user through any of these services, confirm they are working in `us-east-2`. If not, they will need to create a new project in that region.

## Architecture: How to Use Neon

Neon is **not** a place to host your app frontend. Neon provides the backend primitives (Lakebase Postgres, Auth, Object Storage, Functions, AI Gateway) that **compose with** the application platform you already use.

Recommended architectures:

**Full-stack app on Vercel** (or Netlify) augmented with Neon — the app framework (Next.js, TanStack Start, etc.) owns your UI and routes and talks directly to your Neon services (Lakebase Postgres, Auth, Object Storage, Functions, AI Gateway).

**Reach for Neon Functions when you outgrow the host's limits** — a WebSocket or SSE server, long-running agents, or an MCP server that risks timing out on short, lambda-style serverless functions. As long as there is an active connection, a Neon Function can run up to 24 hours without interruption, with the added benefit of running close to your data.

**Move your whole backend control plane onto Neon Functions** — especially useful when the frontend is **client-only** rather than full-stack: TanStack Router, React Router in client mode, and similar SPAs hosted on Vercel or Netlify. The client talks **directly to Neon Functions**, where you build REST APIs and request/response agents. Secure these functions like any standalone REST API — verify a JWT or API key at the top of each handler (see the `neon-functions` skill).

Because Functions are just your backend, they compose with a full-stack app that already has one (Next.js route handlers, etc.), too.

## Neon Documentation

The Neon documentation is the source of truth for all Neon-related information. Always verify claims against the official docs before responding. Neon features and APIs evolve, so prefer fetching current docs over relying on training data.

### Finding the Right Page

Look the page up before you fetch it — **don't guess URLs!** The docs index lists every available page with its URL and a short description:

```
https://neon.com/docs/llms.txt
```

### Fetching Docs as Markdown

Any Neon doc page can be fetched as markdown in two ways:

1. **Append `.md` to the URL** (simplest): https://neon.com/docs/introduction/branching.md
2. **Request `text/markdown`** on the standard URL: `curl -H "Accept: text/markdown" https://neon.com/docs/introduction/branching`

Both return the same markdown content. Use whichever method your tools support.

## Choosing the Right Skill

Neon provides a set of agent skills in addition to the official documentation. When a task matches one of the rows below, work from that skill rather than from this overview. You may have some of these skills already installed, or you may need to install them.

The skills below live in the [`neondatabase/agent-skills`](https://github.com/neondatabase/agent-skills) repo:

| Skill | Use it for |
| --- | --- |
| `neon-postgres` | Working with databases, including connections, schemas, queries, and autoscaling: SQL development, schema design, performance optimization, and scaling decisions. |
| `neon-postgres-branches` | Choosing or creating the right branch type for dev, preview, test, or CI workflows. Use this skill as a slash command. |
| `neon-object-storage` | Storing and serving files (uploads, images, blobs), including branching them with the database. |
| `neon-functions` | Deploying long-running or streaming serverless functions — APIs, agents, SSE/WebSocket servers. |
| `neon-ai-gateway` | Calling an LLM or routing across model providers with one credential, including discovering the branch's servable models at runtime via the OpenAI-compatible `/v1/models` endpoint. |
| `claimable-postgres` | Provisioning instant, claimable temporary Postgres databases (for example, one per end user or demo). |
| `neon-postgres-egress-optimizer` | Diagnosing or fixing excessive Postgres egress (network data-transfer) costs in a codebase. |

For guidance on agent platforms that provision and operate Lakebase Postgres on Neon at scale, use `neon-postgres-agent-platforms`, which lives in a separate repo: [`neondatabase/neon-for-agent-platforms`](https://github.com/neondatabase/neon-for-agent-platforms).

### Installing the Right Skill

First check whether the target skill is already installed and accessible (for example, it appears in the available skills list or its `SKILL.md` is present). If it is, use it directly. If it is not installed, install it via the `skills` CLI, if available, with `npx`/`bunx`:

```bash
npx skills add neondatabase/agent-skills -s <skill-name>
```

Replace `<skill-name>` with the skill you need (for example, `neon-object-storage`, `neon-functions`, or `neon-ai-gateway`). Useful flags:

- `-g` — install globally instead of into the current project.
- `-y` — non-interactive mode (skip prompts).
- `-a <agent-name>` — pick the target agent(s) for non-interactive mode.

For example, to install the object storage skill globally for a specific agent without prompts:

```bash
npx skills add neondatabase/agent-skills -s neon-object-storage -g -y -a <agent-name>
```

If you don't have access to the `skills` CLI, you can visit https://neon.com/.well-known/agent-skills for a registry of all available Neon skills and fetch them manually.

### Updating Skills

Keep the skills up to date: for every new session, update them so you are working with the latest best practices.

Use the same method that was used to install them. With the `skills` CLI, run the install command above with `update` in place of `add`, or run `npx skills update` to update all Neon skills. If the skills were installed via a plugin, they are updated automatically.

## Getting Started with Neon

The easiest way to get started with Neon is to use our CLI and the project bootstrap wizard:

```bash
npx neon@latest init --agent
```

Use the `--agent` flag to run in a non-interactive, state-machine mode.

This init command will guide you through installation of suggested Neon development tools. Everything is customizable. The defaults are:

- Neon CLI installed globally
- Neon MCP server installed globally
- Neon Agent skills installed into the project

If `init` is run in an empty project, it will run the `bootstrap` command, offering to install one of our project templates.

### Getting Started with the Neon CLI

**Prefer the CLI over the MCP server** unless the user instructs otherwise, the CLI is unavailable or blocked in your environment, or it is not authenticated, since it provides more capabilities, including deploying Neon Functions.

The above `init` command will install the Neon CLI, but the CLI can also be installed manually with `npm i -g neon` or `bun i -g neon`. For full CLI installation options, see https://neon.com/docs/cli/install.md

#### Useful CLI Commands

These commands are included in the `init` command but can be run manually as needed.

1. `neon link` — Interactively links the workspace to a Neon org, project, and branch, writing the IDs to a git-ignored `.neon` file. Run once per project. Once linked, project- and branch-scoped commands no longer need `--project-id` or `--branch` (for example, `neon branch list`). `neon link --agent` can be used to run in a non-interactive, state-machine mode.
2. `neon checkout <branch-name>` — Pins a different branch in `.neon`, creating it if it doesn't exist yet, and pulls that branch's env. It drives the [Branch-First Dev Flow](#branch-first-dev-flow) described below.
3. `neon config init` — Initializes a `neon.ts` file, which declares how you provision and manage Neon services, in the root of the project.
4. `neon env pull` — Fetches the current branch's Neon environment variables (`DATABASE_URL`, …) into your existing `.env`, or `.env.local` if you don't have one (override the target with `--file`). No branch ID needed; it reads `.neon`. **`link` and `checkout` run this for you by default**, so you rarely call it directly.

   Without `neon.ts` it pulls the vars of every service the branch actually has (Postgres, plus Neon Auth, the Data API, and bucket `AWS_*` once provisioned); with `neon.ts` it pulls only the services declared there and errors if the branch is missing one — and the AI Gateway vars are never pulled unless `neon.ts` declares `aiGateway`.

### Getting Started with the Neon MCP Server

The above `init` command will install the Neon MCP server globally, but it can also be installed manually using: `npx -y add-mcp https://mcp.neon.tech/mcp -g -n Neon -y -a <agent-name>` or through your IDE plugin.

For all available plugins, see: https://neon.com/docs/ai/ai-agents-tools.md

For full MCP server installation options, see https://neon.com/docs/ai/connect-mcp-clients-to-neon.md

Useful MCP tools to initialize a project:

- `list_projects` — Lists the first 10 Neon projects in your account, providing a summary of each project. If you can't find a specific project, increase the limit by passing a higher value to the `limit` parameter.
- `create_project` — Creates a new Neon project in your Neon account. A project acts as a container for branches, databases, roles, and computes.
- `get_connection_string` — Returns your database connection string.

## Neon Infrastructure as Code

`neon.ts` is Neon's branch config and infrastructure-as-code file: declare which Neon services your project's branches should have, get type-safe env vars, and program branch settings — all in TypeScript. It's the config layer for your Neon services, and it composes with the branch-first loop below. Add it with `@neon/config`:

```bash
npm i @neon/config
```

```typescript
// neon.ts
import { defineConfig } from "@neon/config/v1";

export default defineConfig({
  preview: {
    aiGateway: true,
    buckets: {
      images: {
        access: "private",
      },
    },
    functions: {
      imagegen: {
        name: "AI SDK image agent",
        source: "src/index.ts",
      },
    },
  },
});
```

### Provision services with neon config

Every project ships with Lakebase Postgres; `neon.ts` lets you also declare Neon Auth and the Data API today, with Functions, buckets, and the AI Gateway under a `preview` block — every service for the branch composes in one file:

```typescript
// neon.ts
export default defineConfig({
  auth: true,
  dataApi: true,
  preview: {
    functions: {
      /* ... */
    }, // see the neon-functions skill
    buckets: {
      /* ... */
    }, // see the neon-object-storage skill
    aiGateway: true, // see the neon-ai-gateway skill
  },
});
```

Reconcile the declaration from the CLI — the Neon equivalent of `terraform status` / `plan` / `apply`:

```bash
neon status          # print the branch's live config (read-only). Alias for `neon config status`.
neon config plan     # dry-run diff of what apply would change (read-only)
neon deploy          # provision the declared services. Alias for `neon config apply`
```

`apply` / `deploy` provision the declared services **and then pull the branch's env into your local `.env.local`** (e.g. `Pulled 5 Neon variables into .env.local: DATABASE_URL, …`), so your local env always matches what's deployed.

### Type-safe env vars with parseEnv

`@neon/env`'s `parseEnv` takes your `neon.ts` config object and returns a parsed, typed env object, validated against the services you declared. The shape of `env` follows your config, and missing variables are flagged with clear errors.

```bash
npm i @neon/env
```

```typescript
import { parseEnv } from "@neon/env";
import config from "./neon";

const env = parseEnv(config);

console.log(env.postgres.databaseUrl);
console.log(env.auth.baseUrl);
```

By default `parseEnv` requires _every_ variable your config implies. When one of your apps only uses a subset, for example when you need to read `DATABASE_URL` but never the unpooled URL, pass an array of env-var keys to require and validate only those. The keys are typesafe: autocomplete only offers variables your config enables, and the returned shape is narrowed to exactly what you selected (so unselected variables are neither enforced nor present).

```typescript
import { parseEnv } from "@neon/env";
import config from "./neon";

// Only DATABASE_URL is required and returned; DATABASE_URL_UNPOOLED is not enforced.
const { postgres } = parseEnv(config, ["DATABASE_URL"]);
console.log(postgres.databaseUrl);

// Selecting across services — only these keys are validated.
const env = parseEnv(config, ["DATABASE_URL", "NEON_AUTH_BASE_URL"]);
console.log(env.postgres.databaseUrl, env.auth.baseUrl);
```

### Branch configuration

Beyond services, `neon.ts` can program what configuration _new_ branches receive via the `branch` property — a function of the branch being evaluated that returns its settings:

```typescript
// neon.ts
import { defineConfig } from "@neon/config/v1";

export default defineConfig({
  auth: true,
  dataApi: true,
  branch: (branch) => {
    if (branch.exists) {
      // leave existing branches untouched
      return {};
    }
    if (branch.name.startsWith("dev")) {
      return {
        ttl: "7d", // clean up the branch after 7 days
        postgres: {
          computeSettings: {
            autoscalingLimitMinCu: 0.25, // scale to zero
            autoscalingLimitMaxCu: 1, // keep it cheap
            suspendTimeout: "5m",
          },
        },
      };
    }
    return {};
  },
});
```

The `branch` function receives the target branch (its `name`, whether it `exists` yet, whether it's the default, and more) and returns the tuning you want. Here new `dev-*` branches get a 7-day TTL so they clean themselves up, plus a cheap scale-to-zero compute profile, while existing branches and everything else fall through to the defaults. Because `neon checkout` applies this policy on create, a fresh `dev-*` branch comes up with these settings already in place.

### Type-safe config: invalid setups don't compile

Because `neon.ts` is TypeScript, the compiler catches invalid infrastructure before you ever deploy — and Neon encodes the actual rules (and their fixes) into the types, so the error tells you what to do rather than failing with a useless `Type 'true' is not assignable to type 'never'`. The canonical case: the Data API verifies requests with Neon Auth by default, so enabling it on its own is a type error _on_ `dataApi`:

```typescript
export default defineConfig({
  dataApi: true, // type error: `dataApi` (default authProvider 'neon') requires Neon Auth
});
```

The message names both fixes, so pick one:

```typescript
// 1. Enable Neon Auth (the default Data API auth provider):
export default defineConfig({ auth: true, dataApi: true });

// 2. Or verify a third-party IdP instead of Neon Auth:
export default defineConfig({
  dataApi: {
    authProvider: "external",
    jwksUrl: "https://your-idp/.well-known/jwks.json",
  },
});
```

Treat a `neon.ts` type error as the config telling you which services must go together — read the message, it spells out the valid combinations.

See https://neon.com/docs/reference/neon-ts.md for documentation on the `neon.ts` file.

## Branch-First Dev Flow

Neon branches enable a branch-first development flow, which we recommend when using Neon services. This and `neon.ts` above are the two halves of the recommended setup — `neon.ts` declares what every branch should have, and the branch-first loop is how you move between those branches day to day. Each works on its own, and they compose.

Create a Neon branch any time you would create a git branch. Use the following commands if you have CLI access:

- `neon checkout <branch-name>` — Creates the branch if it doesn't exist, or checks out the existing one, by updating only the branch pointer in `.neon`. Run without a name for an interactive picker. It does not touch code or local Postgres.
- `neon env pull` — Fetches the current branch's Neon environment variables into your `.env` (see [Useful CLI Commands](#useful-cli-commands) above). **`link` and `checkout` run this for you by default**, so you rarely call it directly.
- `neon diff` — Shows the schema diff between the child branch and its parent. Run this to see what changes have been made to the schema since the last branch was created and before you commit your changes.

```bash
neon link                     # once; also pulls the linked branch's env
neon checkout dev-add-search  # per feature; also pulls the branch's env
```

Because `link` and `checkout` pull env by default, the branch's `DATABASE_URL` lands in your local `.env` automatically — build against it, then `checkout` the next branch and repeat. As the agent, drive this loop yourself: run `checkout` between tasks.

### How checkout composes with neon.ts

When a `neon.ts` is present, `neon checkout` applies your policy as it **creates** a branch, so a fresh branch comes up with its declared settings and services already in place. Checking out an _existing_ branch never reconciles it — apply config changes to it explicitly with `neon config apply` (or `neon deploy`). The bundled `env pull` also checks `neon.ts` against the linked branch and fails fast if the branch is missing a declared service, pointing you at `neon deploy` to provision it, so your local env and the remote branch never drift apart silently.

### Opting out of local env vars

If env vars are injected at runtime instead of written to disk — or you simply don't want secrets in the working tree — pass `--no-env-pull` to `link` / `checkout` and supply the env another way:

- `neon-env run -- <your dev command>` (from `@neon/env`) fetches the branch's vars from your `neon.ts` and injects them into the child process at runtime — no `.env` file needed. This is the runtime counterpart to the on-disk `env pull`.
- `neon-env export` (from `@neon/env`) prints the branch's env to stdout as dotenv lines or, with `--format json`, JSON — for piping into another env manager rather than running a command. For example, [varlock](https://varlock.dev) can bulk-load it from a `.env.schema` with `@setValuesBulk(exec("neon-env export --format json"), format=json)`.
- `fetchEnv` from `@neon/env` is the programmatic version of the same thing: resolve the branch's env in code at runtime instead of shelling out to `neon-env run`.
- `neon dev` injects the same vars into your local dev server — it's part of Neon Functions local development (a public beta feature).

When an agent should not write a local `.env`, instruct it (for example in your `AGENTS.md`) to run `neon checkout <branch> --no-env-pull` and rely on runtime injection.

For reading env you _already_ have on disk (typed and validated against your `neon.ts`), use `parseEnv` — see [Type-safe env vars with parseEnv](#type-safe-env-vars-with-parseenv) above.

## Observability

Neon exposes branch-scoped logs. **Today they cover Neon Functions and Object Storage only.** Postgres computes and the AI Gateway are coming; until then, neither emits records. Logs are region-gated like the other beta services above — only `us-east-2` is enabled today, and a branch in any other region answers `404` with reason `telemetry_not_enabled` rather than an empty result.

Use Neon CLI 3.1 or newer first. **Decide which branch you are querying.** Without `--branch`, the CLI uses the branch pinned in `.neon`, or the project's default branch when the workspace isn't linked. A deployed function or bucket usually lives on a different branch than the one checked out for development, so an empty result is more often the wrong branch than a missing log.

```bash
neon logs query --since 1h
neon logs query --branch production --source function --minimum-severity error --since 6h
neon logs query --source storage --since 1h --output json
neon logs fields
neon logs field-values service_name --since 1h
```

`--source` accepts `function`, `storage`, and `pg_endpoint`, but only `function` and `storage` return records today — `pg_endpoint` is accepted and comes back empty until Postgres logs ship. The window defaults to 1h on `query` and 6h on `field-values`, and cannot exceed 7d on either. If Neon reports `--minimum-severity` as unsupported on a branch, use `--severity-text` instead. Run `neon logs --help` for the full filter and pagination interface.

`--logql` replaces the structured filters with a raw stream selector or line filter. Its stream label is `entity_type`, not `source`:

```bash
neon logs query --since 1h --logql '{entity_type="function"} |= "timeout"'
```

If the CLI is unavailable, fall back to the Neon MCP server's read-only `query_logs`, `list_log_fields`, and `list_log_field_values` tools.

In TypeScript applications, use `@neon/sdk`. Project and branch are positional, and `query` returns a lazy paginated iterable rather than a promise:

```typescript
for await (const record of neon.logs.query(projectId, branchId, {
  since: "1h",
  source: "function",
})) {
  console.log(record.timestamp, record.severity_text, record.message);
}

const { data: fields } = await neon.logs.fields(projectId, branchId);
const { data: serviceNames } = await neon.logs.fieldValues(projectId, branchId, "service_name");
```

`query`'s iterator always throws on error, but `fields` and `fieldValues` follow the client's `throwOnError`, which defaults to `false` and hands back `{ data, error }`. `fieldValues` resolves to the whole response, not a bare array: read `serviceNames.values`, and treat them as an arbitrary subset whenever `serviceNames.is_truncated` is true.

### Loki-compatible read API

For direct HTTP reads, authenticate with `Authorization: Bearer <NEON_API_KEY>` and use this branch-scoped base URL:

```text
https://console.neon.tech/telemetry/v1/projects/{projectId}/branches/{branchId}/loki
```

The available endpoints are:

- `GET /api/v1/query_range`
- `GET /api/v1/labels`
- `GET /api/v1/label/{name}/values`

This is a read-only Loki-compatible subset, not a push endpoint or complete Loki deployment. `query_range` supports LogQL stream selectors and line filters, plus `since` or `start`/`end`, `limit`, and `direction`; it does not support aggregations, parsers, or formatting stages.

The paths above are the ones to call directly. A Loki client that builds its own paths — a Grafana data source appends `/loki/api/v1` to whatever URL it is given — may need a different root, so confirm the data-source URL against the Neon docs rather than pasting this base.

## Manage Neon Resources

Recommended: Use `@neon/sdk` to manage Neon resources programmatically, such as creating projects, branches, and snapshots for dev scripts, CI/CD automations, and platforms building on top of Neon.

`@neon/sdk` is the official TypeScript client for the [Neon API](https://neon.com/docs/reference/api-reference.md): **Fetch-based, zero-dependency, ESM-only**, generated from Neon's [OpenAPI spec](https://neon.com/api_spec/release/v2.json) with an ergonomic layer on top. It is the successor to [`@neondatabase/api-client`](https://www.npmjs.com/package/@neondatabase/api-client) (axios-based, generated-only). The old client is **not deprecated** and is safe to keep using, but new code should prefer `@neon/sdk`.

### Neon for (Agentic) Platforms

If you're building agents that generate apps from prompts, your users want to build apps, not manage databases. Industry-leading platforms like Replit and V0 create databases on Neon because it aligns with how agents work: an instant, branchable, serverless Lakebase Postgres data layer, invisible to users.

Neon features for agents:

- Instant Provisioning: your users never wait for infrastructure.
- Snapshots: let users toggle between checkpoints of code and state together.
- Low cost-per-Database: automatic scale to zero and 350ms cold starts.
- Full-Stack, Batteries-Included: Neon Auth, Data API included at no added charge.
- Granular API Controls: Track and control usage for flexible limits and invoicing.

All details here: https://neon.com/programs/agents.md

Apply for the Neon Agent Program for special program pricing here: https://neon.com/programs/agents

## Gotchas

### Neon Auth: "invalid domain"

Neon Auth only redirects back to domains on its trusted-domains list. Anytime the domain your app runs on changes — a new production custom domain, a new deploy/preview URL, moving from `localhost` to a hosted environment, and so on — you must register the new domain with Neon Auth. Otherwise sign-in and OAuth callbacks fail with an **`invalid domain`** error because the redirect target isn't trusted.

The easiest way to fix this is the CLI. With the workspace linked to the project (see the branch-first flow above), add the new domain to the trusted list:

```bash
neon neon-auth domain add <domain>   # e.g. neon neon-auth domain add https://app.example.com
neon neon-auth domain list           # verify what's currently trusted
neon neon-auth domain delete <domain> # remove one you no longer use
```

If the workspace isn't linked, pass `--project-id <id>` (and `--branch <id|name>`) explicitly. For local development, `neon neon-auth domain allow-localhost` manages whether `localhost` is permitted. Register the domain before pointing users at the new URL, so they never hit the `invalid domain` error.

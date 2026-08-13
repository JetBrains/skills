# Sentry error monitoring on Neon Functions

A Neon Function is a **long-lived Node.js 24 process running a web-standard request/response handler** — not an edge worker or a short-lived lambda. That means any integration SDK that works in an ordinary Node process works here unchanged: you initialize it once at module load, before your handler starts serving requests, and it stays instrumented for the life of the isolate.

This reference walks through wiring up Sentry for errors, logs, and traces — including AI-agent tracing, since agents are the workload Functions are built for. The same shape (init module imported first, gated on an env var, secret passed at deploy time) applies to other Node SDKs — see [Other Node integrations](#other-node-integrations) at the end.

## Sentry (errors, logs, and traces)

Because the runtime is a normal Node process, use the Node SDK `@sentry/node` — not an edge/serverless wrapper. **Use `@sentry/node` ≥10.67.0**: older versions fail to register their tracer against the OpenTelemetry API global the runtime pre-creates, and every span is silently non-recording while errors and logs keep working. The SDK bundles cleanly through `neon deploy`'s esbuild with no extra build config.

Sentry has distinct signals, and picking the right one is the main instrumentation decision:

1. **Errors** — unhandled route errors and failures your code can't recover from. Each becomes a grouped, alertable *issue*.
2. **Logs** — recoverable failures and narrative events (a model attempt failed and the agent moved on, a retry, a fallback). Structured, searchable, linked to the trace — and they don't pollute the issue stream.
3. **Traces** — the request's span tree: the incoming request, outbound fetches, and (for agents) the full model/tool call hierarchy with token usage.

All three carry the same trace ID, so from an error you can pivot to the logs and spans of the same request.

### Environment variables

| Variable | Purpose |
| --- | --- |
| `SENTRY_DSN` | Project DSN; keep it configurable through the deployment environment. |
| `SENTRY_RELEASE` | Optional release identifier such as a commit SHA — unlocks regression detection. |
| `SENTRY_TRACES_SAMPLE_RATE` | Trace sample rate, default `1`. Agents are low-throughput and every trace is interesting; lower it for high-volume plain HTTP. |
| `PRODUCTION_BRANCH` | Your default branch's name, so it reports as environment `production` (see below). |

### 1. Initialize before anything else

Put `Sentry.init` in its own module and import it as the very first import of your entry file, so the process is instrumented before any other code (your handler, the DB pool, the agent) loads.

```typescript
// src/instrument.ts
import * as Sentry from "@sentry/node";

Sentry.init({
  dsn: process.env.SENTRY_DSN,
  enabled: Boolean(process.env.SENTRY_DSN),
  enableLogs: true,
  tracesSampleRate: Number(process.env.SENTRY_TRACES_SAMPLE_RATE ?? 1),
  traceLifecycle: "stream",
  streamGenAiSpans: true,
  integrations: [
    Sentry.vercelAIIntegration({ force: true }),
    Sentry.httpIntegration({ disableIncomingRequestSpans: true }),
  ],
  release: process.env.SENTRY_RELEASE,
  environment:
    process.env.NEON_BRANCH && process.env.NEON_BRANCH !== process.env.PRODUCTION_BRANCH
      ? process.env.NEON_BRANCH
      : "production",
});

process.on("SIGTERM", () => void Sentry.flush(2000));
process.on("SIGINT", () => void Sentry.flush(2000));

export { Sentry };
```

```typescript
// src/index.ts
import "./instrument"; // MUST be the first import, before the framework/agent
import { Sentry } from "./instrument";
import { Hono } from "hono";
// ... rest of the function
```

- **Gate `enabled` on the DSN.** Local dev (`neon dev`) and any branch where you haven't configured the secret then become a no-op — no init, no noise — without changing code.
- **`enableLogs: true`** — the `Sentry.logger.*` API is off by default.
- **`traceLifecycle: "stream"`** — sends each span as it finishes instead of holding the whole tree until the request ends, so spans that complete after the response (streaming agent calls) aren't lost. `streamGenAiSpans: true` is the default on current SDKs (sends gen_ai spans as standalone items so large prompts aren't truncated); set it to `false` on self-hosted Sentry.
- **The two integrations:** `vercelAIIntegration({ force: true })` because `neon deploy` bundles your code, which defeats the integration's module detection; `httpIntegration({ disableIncomingRequestSpans: true })` because the request root span comes from the middleware in step 3 (the runtime's internal server would otherwise add a duplicate with an unhelpful name).
- **Environment:** `NEON_BRANCH` is injected on every branch — including the default — and holds the branch **name** (e.g. `main`, `preview/add-auth`). Because it's always present, don't use it as a boolean flag; compare it against your default branch's name (passed in as `PRODUCTION_BRANCH`) so the default branch reads as `production` and other branches tag by name. Pass `SENTRY_ENVIRONMENT` explicitly per deploy to override.
- **Flush on shutdown:** the runtime sends `SIGTERM`/`SIGINT` before evicting an idle isolate; Sentry buffers logs and batches spans, so flush or the tail gets dropped.

### 2. Provide the DSN as a deploy-time secret

The DSN is your own secret, so set it per-deployment (see [Environment Variables](../SKILL.md#environment-variables)). Either pass it on deploy:

```bash
neon functions deploy <slug> --src src/index.ts \
  --env "SENTRY_DSN=https://…@…ingest.us.sentry.io/…" \
  --env "SENTRY_RELEASE=$(git rev-parse --short HEAD)" \
  --env "SENTRY_TRACES_SAMPLE_RATE=1"
```

or declare it under the function's `env` in `neon.ts` (read from `process.env` to avoid hardcoding). Deploy env vars **persist and accumulate across deployments** — omitting `--env` on a later deploy does not clear a variable set earlier.

### 3. Create the request span and catch route errors

The runtime invokes your handler through its own ingress rather than a plain `node:http` server, so give each request an isolation scope and a root span yourself — one Hono middleware covers it, and everything else (gen_ai spans, logs, outbound fetches) nests under it with clean route names. The `flush` at the end matters: an idle isolate can be suspended, so buffered telemetry has to ship while the request is alive.

```typescript
app.use("*", (c, next) =>
  Sentry.withIsolationScope(() =>
    Sentry.startSpan(
      {
        op: "http.server",
        name: `${c.req.method} ${c.req.path}`,
        forceTransaction: true,
        attributes: { "http.request.method": c.req.method, "url.path": c.req.path },
      },
      async (span) => {
        await next();
        span.setAttribute("http.response.status_code", c.res.status);
      },
    ).finally(() => Sentry.flush(2000)),
  ),
);
```

Then wire a top-level error handler so any error thrown in a route is reported. With Hono, `onError` covers this. Watch out for one gotcha: framework middleware such as `cors()` usually does **not** decorate error responses, so re-add any headers you need on the 500 yourself.

```typescript
app.onError((err, c) => {
  Sentry.captureException(err);
  c.header("access-control-allow-origin", "*"); // cors() doesn't run on error responses
  return c.json({ error: "internal_error" }, 500);
});
```

(There is a dedicated `@sentry/hono` package, but it is alpha and its Node entry point assumes the app is served by `@hono/node-server`, which is not how Functions run Hono — stick with `onError`.)

### 4. Errors are for failures; logs are for the story

Long-running agent workloads — the case Neon Functions are built for — typically **catch their own errors and fall back** (retry a different model, return a degraded result) rather than throwing. It's tempting to `captureException` those too, but every recovered retry then opens a warning-level issue: the issue stream fills with things nobody needs to act on, and the terminal failures drown in them.

Split by whether someone needs to act:

- **`Sentry.captureException` — terminal, needs attention.** The agent exhausted every fallback; an invariant broke. These become issues, group, and alert.
- **`Sentry.logger.*` — recoverable or narrative.** A model attempt failed and the agent moved on; an input couldn't be fetched; a milestone was reached. Structured log records, searchable by attribute and attached to the request's trace — the story you read *after* an issue fires.

A representative agent that tries several models in order:

```typescript
for (const model of models) {
  try {
    const { text: summary } = await generateText({
      model: neon(model),
      prompt,
      experimental_telemetry: { isEnabled: true },
    });
    Sentry.logger.info("summary produced", { component: "agent", model });
    return c.json({ summary, model });
  } catch (err) {
    lastError = err;
    Sentry.logger.warn("model attempt failed", {
      component: "agent",
      phase: "summarize-attempt",
      model,
      error: String(err),
    });
  }
}

Sentry.captureException(lastError, {
  tags: { component: "agent", phase: "summarize-all-failed" },
  contexts: { agent: { attempts: models.length } },
});
return c.json({ error: "all models failed" }, 502);
```

- Log **attributes** (the second argument — flat `string | number | boolean` values) are individually searchable and filterable in Sentry's Logs view.
- On the remaining `captureException` calls, use `tags` for the dimensions you'll filter and group by, and `contexts` for structured per-event detail (`contexts` replaces the legacy `extra`).
- Logs emitted during a request automatically link to its trace, so from the terminal error you can pull up every preceding attempt.

### 5. Trace the agent itself

If the function runs a [Vercel AI SDK](https://ai-sdk.dev) agent (see [references/ai-sdk.md](ai-sdk.md)), Sentry captures the full agent → model → tool span hierarchy with token usage per call — `gen_ai.invoke_agent`, `gen_ai.generate_content`, `gen_ai.execute_tool` spans in the request's trace, plus the **Insights → AI Agents** dashboard. On top of the init config from step 1:

**Opt each call in** — the AI SDK only emits spans when asked — and **report stream errors**, because `streamText` never throws: failures surface as error parts inside the stream and the HTTP response just ends, so without `onError` a dead agent looks like an empty reply.

```typescript
const result = streamText({
  model: neon(MODEL),
  messages,
  tools,
  experimental_telemetry: { isEnabled: true },
  onError: ({ error }) => {
    Sentry.captureException(error, { tags: { component: "agent", phase: "chat-stream" } });
  },
});
```

**Flush when the stream completes.** The middleware's flush runs when the `Response` object is created — before the model finishes — and the gen_ai spans only end with the stream. Ship them from the stream's own finalizer, while the request is still alive:

```typescript
const stream = result.textStream
  .pipeThrough(
    new TransformStream<string, string>({
      async flush() {
        await new Promise((r) => setTimeout(r, 0));
        await Sentry.flush(2000);
      },
    }),
  )
  .pipeThrough(new TextEncoderStream());
return new Response(stream, { headers: { "content-type": "text/plain; charset=utf-8" } });
```

(Once the runtime's `waitUntil` is no longer a preview stub, `waitUntil(Sentry.flush(2000))` is the cleaner way to express this.)

With telemetry enabled the AI SDK records prompts and outputs by default — set `recordInputs: false` / `recordOutputs: false` on the same `experimental_telemetry` object if conversation content must not leave the application. Optionally, group multi-turn chats into a timeline (**Explore → Conversations**) and attribute them to users — set both once per request before the model call:

```typescript
Sentry.setConversationId(chatId);
Sentry.setUser({ id: userId });
```

Direct provider SDKs (`openai`, `@anthropic-ai/sdk`, `@langchain/*`, `@google/genai`) have equivalent Sentry auto-instrumentation, but it patches those modules at import time — which bundling defeats. The Vercel AI SDK path is bundle-safe (the `ai` package emits its own OTel spans), which is why it's the recommended route here. The same caveat applies to other module-patching instrumentation (`pg` spans, for example): if a specific library's spans are missing from a deployed function, bundling is the first thing to check.

### Verifying the wiring

- **Errors:** temporarily add a route that throws (`app.get("/debug-sentry", () => { throw new Error("sentry test"); })`), hit it, confirm the 500 surfaces as an issue in Sentry, then remove the route.
- **Logs:** trigger a recoverable failure (e.g. pass a bogus model name to the fallback path) and confirm the `Sentry.logger` records show up in **Explore → Logs**, linked to the same trace.
- **Traces:** hit a real route and confirm a trace appears (**Explore → Traces**) with the spans you expect — the request root, outbound fetches, and (for agents) `gen_ai.*` spans with token counts. Two things to know before declaring it broken:
  - **Streamed span ingestion lags several minutes behind errors and logs.** An error visible in seconds does not mean its trace is lost — check again after a few minutes.
  - If spans **never** appear while errors and logs flow, check the SDK version — `@sentry/node` <10.67.0 cannot register its tracer against the runtime's pre-created OpenTelemetry API global. Upgrade, or on an older SDK run `delete globalThis[Symbol.for("opentelemetry.js.api.1")]` before `Sentry.init`.

## Other Node integrations

The same pattern generalizes to any Node integration (structured logging, analytics):

1. Initialize once at module scope in a dedicated init module, imported before your handler.
2. Gate it on an env var so local dev and unconfigured branches are a no-op.
3. Pass secrets via `--env KEY=VALUE` on deploy or the function's `env` in `neon.ts`.

Standard Node SDKs bundle through `neon deploy`'s esbuild without changes — but module-patching auto-instrumentation does not, and anything that registers OpenTelemetry globals contends with the runtime's own registration (see the version note at the top).

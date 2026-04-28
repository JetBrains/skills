---
name: observability-integrator
description: Design actionable observability for Kotlin plus Spring services across logs, metrics, tracing, and health endpoints. Use when instrumenting a service, improving incident diagnosis, defining SLO-driven metrics and alerts, adding trace propagation, controlling metric-cardinality cost, or ensuring async and coroutine flows remain observable in production.
metadata:
  short-description: "Design metrics, logs, and tracing well"
  author: Kotlin
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/observability-integrator
---

# Observability Integrator

Source mapping: Tier 2 high-value skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-17`).

## Mission

Make the service explain its own behavior under normal load and under failure.
Instrument for operational questions and decisions, not for vanity dashboards.

## Read First

- Business-critical user journeys and SLO or SLA targets.
- Existing metrics, logs, traces, and actuator exposure.
- Platform stack: Prometheus, Grafana, OpenTelemetry, ELK, Loki, vendor APM, or mixed.
- Service topology, downstream dependencies, async boundaries, and coroutine usage.
- Existing runbooks or alerting gaps.

## Design Sequence

1. Define the most important operational questions:
   - is the service healthy
   - which journey is slow
   - which dependency is failing
   - where saturation is growing
2. Instrument the critical path before the nice-to-have path.
3. Add metrics, traces, and logs that answer those questions together.
4. Add health indicators and alerts with clear actionability.
5. Re-check cardinality, PII, and exposure risk.

## Metrics Rules

- Prefer metrics tied to user journeys, dependencies, pool saturation, queue depth, and retry behavior.
- Choose labels with a cardinality budget in mind.
- Favor histogram or timer metrics where latency distributions matter.
- Separate success, client error, server error, and dependency failure semantics clearly.
- Include outcome metrics for retries, circuit breakers, cache behavior, and scheduler work when they affect operations.

## Logging Rules

- Use structured logs with stable field names.
- Include correlation identifiers such as trace id or request id when the platform supports them consistently.
- Log business-relevant events at service boundaries and failure points, not every line of code.
- Redact or avoid PII, secrets, tokens, and credentials by policy, not by luck.
- Prefer stable event names or codes over prose-only log statements when operations depend on searchability.

## Tracing And Health Rules

- Propagate trace context across HTTP, messaging, async execution, and coroutine boundaries.
- Sample traces deliberately. Full sampling is not always affordable or necessary.
- Distinguish liveness, readiness, and startup health semantics.
- Keep actuator exposure minimal and authenticated where needed.
- Include dependency health only when the signal is actionable and does not create cascading false alarms.

## Advanced Observability Traps

- High-cardinality labels can make a metric unusable and expensive at the same time.
- Trace propagation may silently fail across executors, coroutines, or listeners even when HTTP tracing looks fine.
- Logs without stable correlation are often worse than fewer logs with consistent context.
- A metric that never drives an alert, dashboard, or investigation path is probably noise.
- Readiness that depends on every optional downstream can create self-inflicted outages.
- Health endpoints that expose secrets or internal topology are security risks, not observability wins.
- Poor sampling decisions can hide the exact slow or failing traces operators care about.

## SLO And Cost Nuances

- RED and USE perspectives complement each other. User-facing latency and error metrics do not replace resource saturation visibility, and vice versa.
- Burn-rate alerting is often more actionable than static threshold alerting for SLO-backed services.
- Histogram bucket choice affects both storage cost and usefulness. Buckets should reflect user-facing latency objectives, not library defaults.
- Exemplars or trace links can shorten incident diagnosis dramatically when supported by the platform.
- Metric names and labels become quasi-APIs for operators. Renaming them casually creates observability drift across dashboards and alerts.
- Observability cost is part of the design. Sampling, retention, and cardinality are architectural choices, not cleanup work for later.

## Expert Heuristics

- Instrument the path that paged someone last time before instrumenting the path that is merely interesting.
- Prefer a smaller set of trusted dashboards and alerts over a broad telemetry surface nobody uses.
- If correlation breaks across async boundaries, fix that before adding more log lines.
- Good observability makes rollback, mitigation, and capacity decisions faster. Favor signals that support those decisions directly.

## Output Contract

Return these sections:

- `Operational questions`: what the instrumentation must answer.
- `Metrics plan`: the key metrics and label strategy.
- `Logging plan`: structure, correlation, and redaction rules.
- `Tracing plan`: propagation points and sampling guidance.
- `Health and alerting`: readiness, liveness, startup, and actionable alerts.
- `Minimal implementation plan`: the smallest set of instrumentation changes that materially improves operability.

## Guardrails

- Do not instrument everything.
- Do not expose actuator or debug endpoints casually.
- Do not emit sensitive data in logs or traces.
- Do not add cardinality-heavy labels such as raw user ids, full URLs, or free-form exception messages.
- Do not create alerts with no obvious operator action.

## Quality Bar

A good run of this skill gives operators clear signals, low-noise alerts, and fast incident localization.
A bad run produces a large telemetry bill, noisy dashboards, and no practical improvement in diagnosis.

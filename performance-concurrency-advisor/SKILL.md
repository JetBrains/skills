---
name: performance-concurrency-advisor
description: Analyze and improve performance, throughput, latency, and concurrency behavior in Kotlin plus Spring services using real evidence from metrics, traces, SQL, thread or heap signals, and code paths. Use when endpoints are slow, pools saturate, coroutines or reactive flows block unexpectedly, N+1 or contention appears, or caching and parallelism decisions need precise, non-generic guidance.
metadata:
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/performance-concurrency-advisor
---

# Performance Concurrency Advisor

Source mapping: Tier 2 high-value skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-18`).

## Mission

Find the bottleneck that actually limits the system, then recommend the smallest high-leverage change.
Treat performance work as evidence-driven systems analysis, not as a bag of folklore.

## Read Evidence First

- Latency metrics, percentile breakdowns, throughput, and error rate.
- Traces for slow flows.
- SQL count or explain plans when persistence is involved.
- Hikari, thread pool, event loop, or queue metrics when available.
- Thread dumps, heap or GC signals, and recent code changes when the issue is severe.

## Diagnose By Layer

Check these layers explicitly:

- database query count and query latency
- connection pool contention
- downstream HTTP or messaging latency
- thread pool starvation or scheduler misuse
- event-loop blocking in reactive flows
- serialization or large payload overhead
- lock contention or transactional scope
- cache misses or invalidation churn
- CPU-bound work and allocation pressure

## Advanced Performance Heuristics

- N+1 is both a latency bug and a load amplifier. Fixing it often helps pool pressure and downstream CPU together.
- Bigger pools are not a universal fix. If the database is slow, increasing concurrency often worsens tail latency.
- Hikari tuning should follow database capacity and workload pattern, not only CPU count.
- Blocking calls inside WebFlux or coroutine event loops are catastrophic even when average latency looks acceptable.
- `Dispatchers.IO` is not a free performance button. It moves blocking but can also hide architectural mismatch and increase context switching.
- Parallelizing downstream calls can reduce mean latency while worsening saturation and timeouts under load.
- Caches need invalidation, warm-up, and cardinality discipline. A bad cache can improve benchmarks and hurt production correctness.
- Large object graphs, verbose JSON, and repeated mapping can dominate latency once the database is already optimized.
- Lock contention and long transactions often look like random latency spikes until you correlate them with pool saturation and retries.
- GC tuning is downstream of allocation patterns. Do not jump to JVM flags before understanding object churn.

## Measurement Nuances

- Average latency hides tail pain. Prefer percentiles and saturation correlation when user pain is bursty.
- Coordinated omission can make a load test look healthier than production. Treat benchmark tooling assumptions as part of the evidence.
- Container CPU limits, memory limits, and JDK container-awareness can dominate runtime behavior even when local profiling looks clean.
- Warm-up state, JIT compilation, cache fill, and connection pool priming matter. Separate cold-start behavior from steady-state behavior.
- If queueing exists anywhere in the path, small utilization increases can create nonlinear latency growth. Think in queueing terms, not only in raw CPU percentages.

## Expert Heuristics

- Prefer eliminating unnecessary work before parallelizing existing work.
- If one optimization helps throughput but worsens tail latency or operator clarity, call that tradeoff out explicitly.
- When reactive or coroutine code is slower than blocking code, first verify hidden blocking boundaries and context switches before blaming the paradigm.
- If the service is already SLO-bound by a dependency, optimize isolation and graceful degradation before micro-optimizing local code.

## Concurrency Rules

- Distinguish I/O-bound work from CPU-bound work before recommending async or parallel execution.
- Keep transaction scopes short when locks or connection usage are involved.
- Treat shared mutable in-memory state as a design smell in horizontally scaled services.
- For coroutine or reactive flows, verify context propagation for transactions, security, and MDC if debugging or tracing is part of the issue.
- Use backpressure, bulkheads, or bounded queues before unbounded parallelism.

## Output Contract

Return these sections:

- `Observed bottleneck`: the most likely limiting resource or contention point.
- `Evidence`: metrics, traces, code path, or SQL evidence supporting that conclusion.
- `Recommended change`: the smallest high-impact optimization.
- `Tradeoffs`: what gets better and what new risk appears.
- `Verification`: the benchmark, metric, or load-test signal that should improve.

## Guardrails

- Do not optimize without measurement.
- Do not recommend caching, async, or bigger pools as reflexes.
- Do not confuse throughput optimization with latency optimization; sometimes they move in opposite directions.
- Do not trust local benchmarks alone for highly concurrent or I/O-heavy paths.

## Quality Bar

A good run of this skill identifies the true constraint and gives a measurable improvement plan.
A bad run suggests generic tuning knobs with no evidence, no tradeoffs, and no way to confirm success.

---
name: stacktrace-log-triage
description: Diagnose Kotlin + Spring failures from stack traces, startup logs, runtime logs, and related metric anomalies, then separate root cause from wrapper exceptions and propose safe next steps. Use when the application fails to start, an endpoint crashes, logs are noisy or deeply nested, or the agent must produce both a quick mitigation and a proper long-term fix without guessing.
metadata:
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/stacktrace-log-triage
---

# Stacktrace Log Triage

Source mapping: Tier 1 critical skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-15`).

## Mission

Turn noisy failure evidence into a ranked diagnosis with explicit confidence.
Always distinguish symptom, proximate cause, and true root cause.

## Gather Evidence First

- Read the full stack trace, not only the top frame.
- Read the surrounding log lines before and after the first failure.
- Capture timestamps, correlation ids, request ids, thread names, and active profiles when available.
- Read recent relevant code or config changes if they are available.
- Reuse repository constraints from `project-context-ingestion` when possible.

## Triage Workflow

1. Find the earliest meaningful failure signal.
2. Walk the `Caused by:` chain until it stops getting more specific.
3. Classify the incident:
   - DI and context startup
   - configuration binding
   - serialization or validation
   - SQL or migration
   - HTTP or timeout
   - security
   - concurrency or locking
   - classpath or version mismatch
4. Separate what failed from why it failed.
5. Rank hypotheses when evidence is incomplete.
6. Propose a quick mitigation and a proper fix separately.

## Diagnostic Rules

- Treat `BeanCreationException`, `InvocationTargetException`, and similar wrappers as transport, not diagnosis.
- Treat the first user-code frame after framework wrappers as high-signal evidence.
- Use log chronology, not stack depth alone.
- If the logs are truncated, say so and lower confidence.
- Prefer an explanation grounded in the observed code path or configuration over pattern-matching from memory.

## Advanced Signal Patterns

- Check suppressed exceptions, root-cause logging one or two lines above the stack trace, and companion failures on adjacent threads. The most useful clue is often not in the main stack at all.
- Distinguish hard failures from saturation patterns: connection pool exhaustion, thread pool starvation, event-loop blocking, retry storms, and deadlock retries often show up as secondary symptoms first.
- For SQL failures, inspect vendor codes and SQL state when available. They are often more actionable than the wrapper exception type.
- For startup failures after version or deploy changes, compare classpath and configuration drift before assuming a code bug.
- For intermittent failures, weigh time correlation and concurrency context heavily. A race rarely leaves a clean single-thread narrative.
- For OOM or GC-related incidents, plain stack traces are weak evidence. Thread dumps, heap symptoms, and allocation context matter more.
- If metrics exist, correlate the first error spike with latency, pool saturation, or downstream dependency degradation rather than treating logs in isolation.
- If the symptom appears in reactive or coroutine code, verify whether context propagation broke logging correlation before dismissing missing IDs or misleading thread names.

## Expert Heuristics

- The first fix after triage should reduce uncertainty as well as reduce pain. Prefer mitigations that also sharpen diagnosis.
- If several candidate causes fit, rank them by evidence, blast radius, and reversibility of the proposed mitigation.
- If the trace points to a framework wrapper, move outward to code and config that changed recently, then inward again through the causal chain.
- Always state what evidence would falsify the leading hypothesis.

## Output Contract

Return these sections:

- `Root cause`: confirmed or most likely cause in plain language.
- `Confidence`: confirmed, high, medium, or low.
- `Evidence`: the log line, exception, config fact, or code path supporting the diagnosis.
- `Hotfix`: the safest immediate containment or unblocking step.
- `Long-term fix`: the proper code or configuration correction.
- `Reproduction and verification`: how to reproduce, test, and monitor the fix.

## Safety Rules

- Label hypotheses as hypotheses.
- Do not recommend risky schema changes, version upgrades, or broad refactors as a hotfix unless the incident truly leaves no safer option.
- Do not claim certainty when the logs are incomplete.
- Do not ignore alerting or monitoring implications after the fix.

## Kotlin-Specific Checks

- Watch for platform-type `NullPointerException`.
- Watch for coroutine stack traces that obscure the original call path.
- Watch for missing Kotlin compiler plugins causing runtime behavior gaps.
- Watch for Jackson plus Kotlin constructor issues that surface as generic deserialization errors.

## Quality Bar

A good run of this skill gives the user a path from failure evidence to safe remediation.
A bad run repeats the top exception, ignores the `Caused by:` chain, or offers a dangerous hotfix without explaining the risk.

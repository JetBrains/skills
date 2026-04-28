---
name: spring-kotlin-code-review
description: Review Kotlin + Spring changes for behavioral regressions, transaction and proxy bugs, API and serialization mistakes, persistence risks, security issues, configuration drift, and missing tests. Use when reviewing a PR, diff, patch, or design change where generic style-focused review would miss Spring-specific correctness and operational risks.
metadata:
  short-description: "Review PRs for Spring-specific risks"
  author: Kotlin
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/spring-kotlin-code-review
---

# Spring Kotlin Code Review

Source mapping: Tier 1 critical skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-21`).

## Mission

Review changes the way a strong Kotlin plus Spring teammate would review them: behavior first, risk first, evidence first.
Optimize for catching bugs, regressions, and missing tests, not for polishing style.

## Read In This Order

- Diff or changed files.
- Related tests.
- Configuration or build file changes.
- Impacted controllers, services, repositories, security config, and migrations.
- Project conventions from `project-context-ingestion` if available.

## Review Dimensions

Check every relevant change for:

- transaction boundaries and rollback behavior
- proxy compatibility and self-invocation traps
- bean wiring and configuration safety
- API contract, validation, and serialization correctness
- JPA or repository correctness and performance
- security exposure and authorization drift
- concurrency, retries, and idempotency risks
- observability regressions
- test adequacy and missing failure-path coverage
- Kotlin-specific problems such as `!!`, unsafe platform types, and misuse of `lateinit`

## Output Contract

Return findings first and order them by severity.
Use this structure:

- `Findings`: each finding should name the risk, explain the consequence, and point to the relevant file and line when available.
- `Open questions or assumptions`: only where uncertainty changes the review outcome.
- `Summary`: only after findings, and only briefly.

If no material findings exist, say so explicitly and still note residual risk or testing gaps.

## What Counts As A Real Finding

- A correctness bug.
- A production-risking design choice.
- A likely regression.
- A security or data-consistency hole.
- Missing coverage for a meaningful failure path.

Minor style suggestions are secondary and should never drown out real risk.

## Review Heuristics

- Prefer a smaller number of well-supported findings over a long list of weak suspicions.
- Tie every finding to behavior, not only to taste.
- Verify whether the repository's existing conventions intentionally justify an unusual pattern before flagging it.
- Distinguish `must fix` concerns from `consider improving` concerns.

## Advanced Review Checklist

- Check deploy-order safety. A code change, config change, and migration may each be correct alone but unsafe in rolling deployment order.
- Check backward compatibility of JSON contracts, event schemas, database writes, and feature flags. Additive changes are safer than semantic changes hidden behind the same shape.
- Check cache invalidation, deduplication, retry semantics, and idempotency whenever writes or integrations change.
- Check whether observability changed with the behavior. A new critical path without metrics, logs, or trace propagation is a real operational regression.
- Check whether new repository queries need supporting indexes or whether an innocuous loop creates N+1 behavior.
- Check whether any new async, scheduled, or concurrent path changes transaction scope, MDC propagation, or security context.
- Check build and dependency changes for BOM drift, plugin mismatches, or silent classpath changes.
- Check what was removed, not only what was added. Missing validation, logging, or authorization is often the real regression.

## Expert Heuristics

- Read the change as a workflow, not as isolated files. Many Spring bugs live in the seam between controller, service, repository, and config.
- If a finding depends on an assumption, state the assumption and the fastest way to confirm it.
- Prefer findings that are expensive for the team to rediscover in production.
- Use style comments only when they prevent future correctness bugs or materially improve maintainability.

## Guardrails

- Do not nitpick naming or formatting when the change contains higher-severity risk.
- Do not invent risks without code evidence.
- Do not praise or summarize before surfacing findings.
- Do not ignore missing tests just because the code "looks straightforward."
- Do not apply generic Java advice without checking Kotlin and Spring specifics.

## Quality Bar

A good run of this skill gives the author a short list of concrete, high-signal risks to address.
A bad run reads like a generic lint pass and misses the transactional, proxy, security, or persistence behavior that actually matters.

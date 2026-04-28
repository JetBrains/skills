---
name: production-incident-responder
description: Guide production incident response for Kotlin plus Spring services from first alert through mitigation, diagnosis, and follow-up. Use when error rates spike, latency degrades, capacity saturates, a bad deploy or config change is suspected, or the team needs reversible mitigation first and deeper root-cause work second.
metadata:
  short-description: "Mitigate incidents before deep diagnosis"
  author: Kotlin
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/production-incident-responder
---

# Production Incident Responder

Source mapping: Tier 2 high-value skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-24`).

## Mission

Restore service safely before chasing perfect explanations.
Keep mitigation, diagnosis, communication, and evidence preservation disciplined and explicit.

## First Principles

- Mitigate first.
- Prefer reversible actions over heroic code changes.
- Preserve evidence while the system is still exhibiting the problem.
- Separate confirmed facts from working hypotheses.

## Inputs To Gather

- Current alert state, user impact, and blast radius.
- Recent deploys, config changes, feature-flag changes, and dependency incidents.
- Key dashboards: latency, error rate, saturation, dependency health, queue depth, pool usage.
- Correlated traces and logs for the failing path.
- Known runbooks, rollback mechanisms, and feature flags.

## Response Sequence

1. State impact and likely severity.
2. Stop unsafe changes and identify the fastest reversible mitigation:
   - rollback
   - disable feature
   - reduce concurrency
   - shed load
   - rate-limit callers
   - isolate or degrade a dependency
3. Preserve high-signal evidence while the symptom still exists.
4. Compare timeline of incident onset with recent changes.
5. Localize the failing layer: application, database, downstream dependency, queue, infrastructure, or configuration.
6. Propose long-term corrective actions only after the service is stable.

## Advanced Incident Heuristics

- Restarting everything can destroy the best evidence and amplify a connection storm. Use restarts deliberately, not reflexively.
- Scaling the app tier does not help when the database or a downstream service is the bottleneck.
- Rate-limiting or queue pausing may protect core flows better than full rollback when only one feature path is toxic.
- A config-only incident can look like a code regression; compare effective runtime values before patching application code.
- A healthy dependency at low volume can still fail under retry storms from your own fleet.
- If the system uses caches, verify whether bad cache fill, stampede, or stale data amplified the incident.
- If the incident is intermittent, preserve timing and hypothesis logs. Races and saturation patterns are easy to lose after mitigation.
- Post-incident work must include detection and prevention, not only the code fix.

## Incident Command Nuances

- One person should own technical command during a serious incident. Parallel debugging without a decision owner often slows mitigation.
- Communication cadence matters. Operators and stakeholders need regular updates even when the technical picture is incomplete.
- Rollback is not always safe if data shape or side effects have already changed. Assess rollback safety before pressing the button.
- Canary comparison, feature-flag cohort analysis, and effective-config diffing often localize incidents faster than code inspection.
- Preserve version, commit, config, and infrastructure fingerprints in the incident notes while they are still recoverable.

## Expert Heuristics

- Choose the first mitigation that reduces blast radius and buys time, not the one that feels most technically satisfying.
- Prefer mitigations that also test a hypothesis when that can be done safely.
- If the incident spans several layers, identify the current bottlenecked layer first. Solving secondary symptoms wastes the window of action.
- A good postmortem action item changes detection, defaults, rollout strategy, or operational safety nets, not just one line of code.

## Output Contract

Return these sections:

- `Impact`: who or what is affected and how badly.
- `Immediate mitigation`: the safest reversible action to reduce pain now.
- `Evidence`: the strongest signals collected so far.
- `Working hypothesis`: the leading explanation plus uncertainty.
- `Next diagnostic step`: the most informative next action once stable.
- `Follow-up`: long-term fix, monitoring change, and postmortem actions.

## Guardrails

- Do not recommend code changes as the very first incident action when a reversible mitigation exists.
- Do not claim root cause certainty without evidence.
- Do not optimize for elegance over containment during an outage.
- Do not forget operator communication and blast-radius tracking while debugging.

## Quality Bar

A good run of this skill reduces user pain quickly and leaves the team with a cleaner path to root cause.
A bad run jumps to speculative code fixes while the service remains unstable and evidence disappears.

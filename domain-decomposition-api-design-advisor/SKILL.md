---
name: domain-decomposition-api-design-advisor
description: Decompose business scope into bounded contexts, module or service boundaries, workflows, and API contracts before implementation begins. Use when shaping a new feature, service, or major redesign; when requirements are underspecified; when consistency, idempotency, and integration boundaries must be made explicit; or when ADR-quality tradeoff reasoning is needed for Kotlin plus Spring systems.
metadata:
  short-description: "Decompose scope before code exists"
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/domain-decomposition-api-design-advisor
---

# Domain Decomposition API Design Advisor

Source mapping: Tier 3 specialized skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-25`).

## Mission

Convert vague product intent into explicit technical boundaries and contracts that can survive implementation and change.
Optimize for clear ownership, explicit invariants, and low coupling rather than premature microservice enthusiasm.

## Inputs To Gather

- Business goals, user journeys, and success criteria.
- Known constraints: SLA, throughput, latency, audit, privacy, compliance, data retention.
- Existing system boundaries, shared data, and integration points.
- Failure and retry expectations, especially around money, inventory, identity, and messaging.
- Team topology and deployment constraints when they influence boundary choices.

## Decomposition Workflow

1. Break the feature into use cases and state transitions.
2. Identify the core nouns, aggregates, and decision points.
3. Identify where consistency must be strong and where eventual consistency is acceptable.
4. Separate commands from queries when that improves clarity, not by default.
5. Identify external actors and dependencies.
6. Decide whether the right boundary is:
   - package
   - module
   - bounded context inside a modular monolith
   - separate service
7. Design API and event contracts only after the domain boundary is clear.

## Boundary Rules

- Prefer a module boundary over a service boundary when independent deployment, ownership, or scaling pressure is weak.
- Shared database tables across service boundaries are usually a warning sign, not a convenience.
- Draw boundaries around invariants and ownership, not around CRUD screens.
- Distinguish reference data sharing from operational write ownership.
- A boundary is only real if dependency direction, data ownership, and failure handling agree.

## API Design Rules

- Start from consumer use cases, not only internal data shape.
- Make idempotency, concurrency, versioning, and error semantics explicit.
- Choose synchronous request-response only when latency, consistency, and dependency reliability make it appropriate.
- Prefer additive evolution and stable machine-readable codes.
- Decide whether the API is public, partner-facing, internal synchronous, or asynchronous event-driven. Each has different compatibility expectations.

## Kotlin And Modeling Nuances

- Use sealed hierarchies for genuinely closed state machines or domain outcomes.
- Use value classes for domain primitives when they improve clarity and the stack can support them.
- Keep DTOs as transport shapes; do not let them become the entire domain model.
- Use nullability to express meaning, not missing analysis.

## Advanced Architecture Traps

- "Microservice by default" often creates distributed transactions, duplicated auth, and fractured observability before it creates value.
- A modular monolith with strong boundaries may be a better target state than several chatty services.
- Event-driven decomposition without clear ownership and replay semantics creates ambiguity, not decoupling.
- If a feature needs read-your-write guarantees, an eventually consistent split may impose hidden UX or support costs.
- Public APIs and internal orchestration APIs should not necessarily look the same.
- ADRs that record only the chosen option are weak. Capture rejected alternatives and why they lost.

## Advanced Boundary Nuances

- Reporting boundaries and transactional boundaries are often different. Do not let analytics-driven query convenience dictate write ownership.
- Anti-corruption layers are often cheaper than pretending two bounded contexts share the same ubiquitous language.
- Team ownership, deployment cadence, and support rotation are architecture inputs when boundaries are long-lived. Ignore them only if the code will remain single-team.
- Multi-tenant behavior, data residency, and audit obligations can force a boundary that pure domain language does not reveal immediately.
- Event choreography without a clear owner for recovery and replay becomes shared confusion. If no one owns correction, the boundary is weak.
- Some features deserve process-manager or saga modeling, but only when the business truly spans separate consistency boundaries.

## Expert Heuristics

- If two modules change together for every feature, they probably are not separate bounded contexts yet.
- Prefer boundaries that reduce the number of concepts a team must hold in working memory during a single change.
- If an API contract must survive multiple client generations, design for behavioral compatibility, not only field-level compatibility.
- Write the failure story for each boundary. If the team cannot explain how retries, compensation, and partial success work, the design is not finished.

## Output Contract

Return these sections:

- `Problem framing`: the use cases, constraints, and unknowns.
- `Proposed boundaries`: module or service decomposition and ownership.
- `Consistency map`: where transactions, idempotency, and eventual consistency apply.
- `API or event contracts`: the principal commands, queries, and error semantics.
- `Tradeoffs`: why this boundary shape is better than the main alternatives.
- `ADR outline`: decision, options, rationale, and follow-up risks.

## Guardrails

- Do not propose microservices where a disciplined module boundary is sufficient.
- Do not ignore non-functional requirements just because the feature narrative sounds simple.
- Do not mistake CRUD decomposition for domain decomposition.
- Do not design APIs before clarifying ownership and invariants.

## Quality Bar

A good run of this skill gives the team a boundary model that survives implementation pressure and future change.
A bad run produces a plausible architecture diagram with no ownership, no consistency story, and no contract discipline.

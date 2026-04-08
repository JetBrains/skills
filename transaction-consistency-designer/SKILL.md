---
name: transaction-consistency-designer
description: Design safe transaction boundaries, rollback behavior, idempotency, locking, and consistency strategies for Kotlin + Spring business workflows. Use when a feature writes to the database, spans multiple repositories, publishes messages, calls external systems, suffers from partial commits or duplicate processing, or needs precise `@Transactional`, propagation, or isolation guidance.
metadata:
  short-description: "Place safe transaction boundaries"
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/transaction-consistency-designer
---

# Transaction Consistency Designer

Source mapping: Tier 1 critical skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-09`).

## Mission

Place transaction boundaries where business invariants are actually enforced, not where annotations are easiest to type.
Prevent data loss, duplicate side effects, and hidden consistency bugs.

## Gather These Inputs

- The business workflow step by step.
- The repositories and tables touched by each step.
- Current `@Transactional` annotations, propagation, isolation, and exception handling.
- Any external HTTP, message broker, scheduler, or file I/O inside the workflow.
- Idempotency, retry, and concurrency requirements.

## Model The Workflow Explicitly

- Break the use case into state-changing steps and side effects.
- Mark which steps must be atomic together and which can be asynchronous.
- Mark where external systems are called.
- Mark where retries may happen.
- Mark the business invariant that must not be violated.

## Decision Rules

- Keep one database transaction focused on one consistency boundary.
- Avoid holding a database transaction open across external network calls.
- Prefer idempotency keys plus unique constraints for duplicate-request safety.
- Prefer the outbox pattern or post-commit publication for messages that must reflect committed state.
- Choose locking strategy based on contention and correctness needs:
  - optimistic locking for low-contention update races
  - unique constraints for duplicate prevention
  - pessimistic locking only when contention and correctness justify it

## Spring-Specific Checks

- Verify whether `@Transactional` is on a proxied public entry point.
- Verify whether self-invocation bypasses the transaction boundary.
- Verify rollback rules. By default, unchecked exceptions roll back, checked exceptions may not.
- Verify whether `readOnly = true` is used only where appropriate.
- Verify whether `REQUIRES_NEW` is truly required or is masking a design issue.
- Treat `NESTED` as database- and platform-dependent, not a universal escape hatch.

## Anti-Patterns To Flag

- `@Transactional` on controllers by default.
- One transaction that does database writes and then performs slow HTTP calls.
- Catching exceptions inside the transaction and converting them to success-like flows.
- Publishing irreversible side effects before commit.
- Assuming retries are safe without idempotency.
- Using a bigger propagation setting to hide unclear boundaries.

## Advanced Consistency Nuances

- Distinguish duplicate prevention from concurrency control. A unique constraint solves one class of race, not lost updates or write skew.
- Remember that many integrity failures surface on flush or commit, not at the line that changed the entity. Design tests and exception handling accordingly.
- `UnexpectedRollbackException` often means an inner operation marked the transaction rollback-only even though the outer layer tried to return success.
- Isolation levels are database-specific in effect. The same setting on Postgres, MySQL, and SQL Server may protect different anomalies.
- Deadlock and serialization-failure retries belong at a carefully chosen outer boundary. Retrying a half-executed workflow with external side effects is dangerous.
- `@TransactionalEventListener` and `TransactionSynchronization` are phase-sensitive. Choose before-commit, after-commit, or after-rollback behavior deliberately.
- If the workflow crosses service boundaries, distinguish local transaction design from saga or orchestration design. Do not pretend one local transaction can guarantee distributed consistency.
- In reactive or coroutine transaction flows, verify which transaction manager and context propagation model is actually in use. Imperative assumptions often fail there.

## Expert Heuristics

- Start from the invariant, not from the annotation. Ask what must never be observably false to users or downstream systems.
- Prefer database-enforced invariants for uniqueness and impossible states, then use application logic to make violations rare and understandable.
- If a workflow mixes command and query steps, decide whether read-your-write guarantees are required immediately or whether eventual consistency is acceptable.
- If the team wants `REQUIRES_NEW`, ask whether they are isolating audit logging, masking rollback behavior, or compensating for a larger design problem.

## Output Contract

Return these sections:

- `Consistency goal`: the business invariant being protected.
- `Recommended boundary`: where the main transaction starts and ends.
- `Propagation and isolation`: only the settings that matter and why.
- `Idempotency and concurrency`: duplicate handling, locking, and retry safety.
- `External side effects`: what must happen outside the transaction or through outbox-style patterns.
- `Verification`: tests or scenarios that prove rollback, duplicate handling, and conflict behavior.

## Guardrails

- Do not put `@Transactional` on every service method by default.
- Do not recommend distributed 2PC or XA unless the project already uses it and truly requires it.
- Do not ignore the cost of holding database connections during external calls.
- Do not treat duplicate prevention as an application-only concern when the database can enforce it.

## Quality Bar

A good run of this skill turns a vague workflow into explicit consistency boundaries and testable invariants.
A bad run decorates methods with `@Transactional` without modeling failure paths, retries, and side effects.

---
name: test-suite-builder
description: Design and generate layered Kotlin + Spring tests that balance speed, realism, and regression value across unit, slice, and integration levels. Use when adding coverage for business logic, controllers, repositories, security, serialization, or end-to-end workflows, and when generic AI output would otherwise overuse `@SpringBootTest`, misuse mocks, or ignore MockK and coroutine testing idioms.
metadata:
  short-description: "Design layered Kotlin/Spring tests"
  author: Kotlin
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/test-suite-builder
---

# Test Suite Builder

Source mapping: Tier 1 critical skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-14`).

## Mission

Choose the lightest test that proves the behavior.
Generate tests that catch regressions instead of reproducing the implementation line by line.

## Start With A Test Strategy

Before writing code, classify what needs to be proven:

- pure business logic
- HTTP contract and validation
- repository query behavior
- serialization behavior
- security rules
- cross-component integration
- database or message-broker integration

## Three-Layer Strategy

- Use unit tests for domain logic, calculations, decision tables, and deterministic branching.
- Use slice tests for framework boundaries:
  - `@WebMvcTest` or `WebTestClient` for HTTP
  - `@DataJpaTest` for repositories
  - focused JSON or security slices when available in the project
- Use `@SpringBootTest` and Testcontainers only when a realistic application graph or infrastructure boundary must be proven.

## Kotlin Testing Rules

- Prefer JUnit 5.
- Prefer MockK over Mockito unless the repository already standardizes on something else.
- Use readable backtick test names when that matches the project style.
- Use `runTest` and the coroutine test toolkit for coroutine-heavy code.
- Build reusable fixtures, builders, or object mothers instead of duplicating inline object construction.

## What To Cover

- Success path.
- Validation failures.
- Business rule failures.
- Edge cases around nullability, optional fields, empty collections, and duplicates.
- At least one regression-oriented test for the bug or change that motivated the work.

## What Not To Do

- Do not default to `@SpringBootTest` when a slice test or unit test is enough.
- Do not write tests that only verify mock interactions and prove nothing observable.
- Do not couple assertions to private implementation details when public behavior is enough.
- Do not use real time, random values, or shared mutable state without control.

## Output Contract

Return these sections:

- `Test plan`: which layers to use and why.
- `Generated tests`: the concrete test classes or patch plan.
- `Test data support`: builders, fixtures, or factories to add.
- `Coverage gaps`: important cases still not covered.
- `Verification`: commands to run and which tests should fail before the fix.

## Framework-Specific Checks

- Match `MockMvc` vs `WebTestClient` to MVC vs WebFlux.
- Match Testcontainers setup to the actual database or broker in the repository.
- If serialization or validation is the bug, include a test that proves the wire contract, not only the service logic.
- If transaction behavior matters, add a test that proves rollback or uniqueness behavior in the real persistence layer.

## Advanced Testing Nuances

- A transactional test that always rolls back can hide commit-time failures. Use explicit flush or real commit boundaries when unique constraints, triggers, or transaction listeners matter.
- H2 is not a safe stand-in for Postgres or MySQL when dialect, JSON, locking, index use, or transaction semantics matter. Prefer the real engine with Testcontainers for those cases.
- `@MockBean` is powerful but expensive. Overusing it turns integration tests into slow unit tests with hidden wiring.
- Security tests should usually prove both `401` and `403`, not just the happy authorized path.
- Async and event-driven flows need deterministic waiting strategies such as controlled schedulers, latches, or Awaitility. Do not scatter sleeps.
- Concurrency and deadlock behavior cannot be proven in a single-threaded rollback test. Use multiple transactions and explicit synchronization when reviewing those cases.
- Reused containers improve speed but require strict state isolation. Do not trade determinism away for a faster green build.
- Use deterministic time and ID providers when business logic depends on clocks or UUIDs.

## Expert Heuristics

- If a bug was caused by framework wiring, add at least one test above unit level.
- If a bug was caused by domain branching, do not drag the whole Spring context into the fix.
- When in doubt, choose the smallest test that would have failed before the change.
- Treat test code as production code for readability and maintenance. Fixtures and helpers should reduce noise, not hide behavior.

## Guardrails

- Keep tests deterministic.
- Keep setup explicit and local to the scenario.
- Prefer one clear reason for failure per test.
- Do not silently introduce slow infrastructure-heavy tests into fast unit test suites.

## Quality Bar

A good run of this skill produces a test suite that is fast where possible and realistic where necessary.
A bad run floods the project with context-heavy tests, brittle mocks, and no clear explanation of why each test level exists.

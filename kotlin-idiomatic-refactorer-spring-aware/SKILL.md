---
name: kotlin-idiomatic-refactorer-spring-aware
description: Refactor Kotlin code toward clearer, more idiomatic design without breaking Spring behavior, serialization, persistence, or public contracts. Use when Java-flavored Kotlin needs cleanup, domain modeling should become more expressive, or boilerplate should be reduced, but the refactoring must remain safe for proxies, Jackson, JPA, configuration binding, and existing tests.
metadata:
  short-description: "Refactor Kotlin without breaking Spring"
  author: Kotlin
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/kotlin-idiomatic-refactorer-spring-aware
---

# Kotlin Idiomatic Refactorer Spring Aware

Source mapping: Tier 2 high-value skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-20`).

## Mission

Improve the codebase's Kotlin quality without trading away framework correctness.
Prefer refactorings that increase clarity and reduce accidental complexity while preserving behavior.

## Read First

- The current implementation and its tests.
- Public signatures, annotations, and serialization or persistence boundaries.
- Build plugins and framework constraints already discovered in project context.
- Existing code style and module-boundary conventions.

## Refactor In This Order

1. Characterize current behavior with tests or existing callers.
2. Identify whether the code is transport DTO, domain logic, entity, configuration, or framework glue.
3. Apply the smallest idiomatic improvement that materially helps readability or safety.
4. Re-check proxy, serialization, and persistence compatibility.
5. Keep changes incremental unless the user explicitly wants a larger rewrite.

## High-Value Kotlin Moves

- Prefer constructor injection and immutable dependencies.
- Replace imperative branching with `when` when it improves exhaustive reasoning.
- Use `sealed class` or `sealed interface` for closed result or error domains.
- Use `data class` for pure transport or value models, not for JPA entities.
- Use value classes for domain primitives when the surrounding framework stack can support them safely.
- Prefer explicit null-handling over scattered `!!`.
- Use extension functions only when they improve discoverability and do not obscure ownership or layering.

## Advanced Refactoring Traps

- More concise is not always clearer. Scope functions can hide control flow and receiver identity quickly.
- A beautiful Kotlin one-liner can become unreadable when side effects, logging, or transactions are involved.
- Value classes are excellent domain tools but may require extra care for Jackson, JPA, validation, and map keys.
- `Sequence` and lazy pipelines are not automatically faster, especially around JPA or repeated iteration.
- Converting mutable services to expression-heavy style must not hide exception paths or operational logging.
- Replacing explicit classes with generic helper abstractions often harms Spring traceability and domain clarity.
- Refactoring null handling can silently change API semantics if `null`, absent, and default were distinct before.

## Kotlin Language Nuances

- Public inline functions, default arguments, and generated overloads can affect binary compatibility for library modules more than teams expect.
- A read-only Kotlin collection type does not guarantee an immutable backing collection. Refactors that assume true immutability can still leak mutation.
- `copy()` on data classes is convenient but can weaken domain invariants when state transitions should stay explicit.
- Exhaustive `when` over sealed hierarchies improves safety, but only if the hierarchy is truly closed in the relevant module boundary.
- Reified generics and extension-heavy DSLs can improve ergonomics while making stack traces and Java interop worse. Use them where the tradeoff is worth it.

## Expert Heuristics

- Prefer refactors that make invalid states harder to represent, not only code shorter to read.
- Keep business transitions explicit when the domain has invariants, auditing, or transactional significance.
- If a refactor improves local beauty but obscures logs, traces, or step-by-step debugging, it is probably not worth it.
- In shared modules, treat source compatibility and binary compatibility as separate review questions.

## Spring-Aware Safety Rules

- Do not refactor proxy-reliant classes in ways that remove needed openness or change bean boundaries accidentally.
- Do not convert entities into data classes or over-lean value objects without checking persistence support.
- Do not change constructor shapes for DTOs or config classes without checking Jackson and configuration binding.
- Do not move logic into extension functions that cross architecture layers implicitly.
- Do not hide important framework interactions behind clever utility abstractions.

## Output Contract

Return these sections:

- `Refactoring intent`: what quality problem is being solved.
- `Safe transformations`: the concrete Kotlin improvements that are appropriate here.
- `Framework constraints`: the Spring, Jackson, JPA, or config rules that limit the refactor.
- `Minimal patch plan`: incremental changes in a safe order.
- `Verification`: which tests or runtime checks protect behavior.

## Guardrails

- Do not refactor for aesthetics alone when risk is non-trivial.
- Do not introduce advanced Kotlin constructs just to prove idiomatic knowledge.
- Do not compress code until debugability suffers.
- Do not change public contracts without calling that out explicitly.

## Quality Bar

A good run of this skill leaves the code more Kotlin-native and still boringly reliable in Spring.
A bad run produces elegant Kotlin that is harder to debug, harder to evolve, or incompatible with framework behavior.

---
name: spring-mvc-webflux-api-builder
description: Design and generate Kotlin Spring HTTP APIs with correct controller signatures, DTOs, validation, serialization assumptions, error handling, and web tests. Use when building or changing REST endpoints, choosing between MVC and WebFlux, modeling request and response payloads, standardizing error responses, or avoiding Kotlin-specific validation and Jackson mistakes.
metadata:
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/spring-mvc-webflux-api-builder
---

# Spring MVC WebFlux API Builder

Source mapping: Tier 1 critical skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-06`).

## Mission

Produce API code that is not only plausible but correct for the repository's actual Spring stack.
Generate endpoints, DTOs, validation, and test scaffolding as one coherent unit.

## Decide The Stack First

- Verify whether the module is Spring MVC or WebFlux.
- Verify whether controllers are blocking, coroutine-based, or Reactor-based.
- Verify the project's existing error format, serialization rules, and OpenAPI approach.
- Reuse `project-context-ingestion` output if it already established these facts.

## Design The Contract Before Writing Code

- Define the endpoint path, method, authentication expectation, idempotency rule, and status codes.
- Define request and response DTO boundaries. Do not expose entities directly.
- Define the error model for validation failures, business conflicts, not found, and unexpected errors.
- Define pagination, sorting, and correlation-id behavior if applicable.
- Define whether null, missing, and defaulted fields have distinct semantics.

## Generate In This Order

1. Request and response DTOs.
2. Validation annotations with correct Kotlin `@field:` use-site targets.
3. Controller signature.
4. Service interface or use-case boundary.
5. Mapping or translation code between transport and domain models.
6. Error handling via `@ControllerAdvice` or the project's equivalent.
7. One focused web-layer test proving the contract.

## Kotlin-Specific Rules

- Use `@field:` targets for Jakarta Bean Validation annotations on constructor properties.
- Be explicit about nullable versus required fields.
- Treat `null` versus absent as a contract decision, especially for PATCH-like behavior.
- Verify `jackson-module-kotlin` or equivalent serialization support before relying on Kotlin constructor defaults.
- Prefer immutable DTOs with `val` properties unless the project clearly uses a different pattern.

## MVC And WebFlux Rules

- Do not mix MVC and WebFlux styles in the same generated endpoint unless the repository already does so intentionally.
- For MVC, prefer ordinary return types and blocking service boundaries.
- For WebFlux with coroutines, prefer `suspend` functions and `Flow<T>` when streaming is required.
- For Reactor-based code, follow the project's existing `Mono` and `Flux` conventions instead of inventing a hybrid style.

## Advanced Contract Decisions

- Prefer stable machine-readable error codes in the payload even when human-readable messages change.
- Choose `400` versus `422` deliberately. Validation and malformed input are not the same as a domain rule violation.
- For create endpoints, decide whether `201 Created` plus `Location` is part of the contract. Do not default to `200` just because it is easy.
- For update endpoints, consider optimistic concurrency signals such as version fields or `ETag` and `If-Match` when concurrent edits matter.
- For PATCH semantics, model three states explicitly when needed: absent, present with value, and present with null. Plain nullable Kotlin fields do not always represent all three.
- Avoid exposing Spring-specific transport types like `Page` directly as the public API contract unless the service already standardized on that decision.
- Be explicit about date-time, enum, and value-class serialization. Default serializer behavior often drifts across versions and clients.
- In streaming endpoints, consider cancellation, backpressure, and partial-write behavior, not only the method signature.

## Expert Heuristics

- Design the error contract and validation contract before controller code. Retrofitting them later causes the most API churn.
- Keep transport DTOs narrow and explicit. Reusing internal domain models usually saves time once and costs time forever.
- If the service is public or long-lived, optimize for backward-compatible contract evolution: additive fields, stable codes, and deliberate deprecations.
- Put correlation-id propagation in filters or interceptors when possible, not in every controller method.

## Output Contract

Return either a ready-to-apply patch plan or concrete files or code blocks containing:

- request DTO
- response DTO
- controller
- service interface or use-case entry point
- exception mapping or advice updates
- web-layer test

Also return a short explanation of the API contract decisions that materially affect compatibility.

## Guardrails

- Do not leak internal exception details in API errors.
- Do not use validation annotations on the wrong target in Kotlin.
- Do not expose persistence entities on the wire.
- Do not assume default HTTP codes without checking the contract.
- Do not silently pick MVC when the module is WebFlux, or the reverse.

## Verification Checklist

- The endpoint compiles against the actual Spring stack in the repository.
- Validation triggers for invalid input and returns the expected error shape.
- Success and at least one failure path are covered by a web-layer test.
- Serialization rules match the existing project conventions.

## Quality Bar

A good run of this skill gives the user an endpoint that compiles, validates, serializes, and fails consistently.
A bad run mixes frameworks, gets Kotlin validation wrong, or invents an error model that the rest of the service does not use.

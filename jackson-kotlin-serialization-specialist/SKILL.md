---
name: jackson-kotlin-serialization-specialist
description: Diagnose and design JSON serialization and deserialization behavior for Kotlin plus Jackson in Spring applications. Use when DTOs fail to deserialize, default parameters or nullability behave unexpectedly, date-time or enum formats drift, polymorphic payloads are involved, PATCH semantics must distinguish null from absent, or ObjectMapper changes risk breaking existing API or message contracts.
metadata:
  short-description: "Fix Kotlin JSON edge cases safely"
  author: Kotlin
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/jackson-kotlin-serialization-specialist
---

# Jackson Kotlin Serialization Specialist

Source mapping: Tier 2 high-value skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-08`).

## Mission

Make Kotlin plus Jackson behavior explicit, compatible, and testable.
Treat wire-format correctness as a contract problem, not only a mapper-configuration problem.

## Read First

- The actual DTO or event model classes.
- The exact failing JSON payload or expected payload examples.
- `ObjectMapper` customizers, Spring Boot Jackson properties, and any per-client mapper overrides.
- Build files to verify Jackson module alignment with Spring Boot and Kotlin versions.
- The boundary where serialization matters: MVC, WebFlux, Kafka, Redis, persistence JSON column, or external HTTP client.

## Diagnose In This Order

1. Verify module presence and alignment:
   - `jackson-module-kotlin`
   - `JavaTimeModule`
   - other custom modules or serializers
2. Verify constructor semantics:
   - default parameters
   - required parameters
   - nullable versus non-null
3. Verify field presence semantics:
   - absent
   - present with `null`
   - present with value
4. Verify naming, inclusion, and date-time strategy.
5. Verify polymorphism or custom serializer behavior.
6. Verify whether the real bug comes from a local mapper override rather than the global mapper.

## Core Kotlin Rules

- Keep DTOs immutable unless the project already has a strong alternative convention.
- Do not switch `val` to `var` just to appease Jackson.
- Do not add empty constructors to Kotlin DTOs as a workaround if the Kotlin module can model the contract correctly.
- Treat nullable as a wire-contract decision, not a convenient escape hatch.
- Be explicit about value classes, sealed hierarchies, and default parameter behavior.

## Advanced Serialization Traps

- Missing field and explicit `null` are not the same. For PATCH-like contracts, model tri-state semantics deliberately.
- Default constructor values can silently hide client mistakes if the field should have been required.
- `@JsonInclude` may improve payload size but can also erase signal that clients rely on.
- Non-null primitives, `FAIL_ON_NULL_FOR_PRIMITIVES`, and Kotlin non-null types interact differently across payload shapes.
- Sealed classes need stable, versionable type discriminators. Do not treat polymorphic type ids as an internal detail once they are on the wire.
- Enum serialization by name, code, or custom object form is a public compatibility choice.
- Date-time serialization must make timezone assumptions explicit. `Instant`, `OffsetDateTime`, and `LocalDateTime` are not interchangeable.
- `@JvmInline value class` support may differ by Jackson version and serializer context. Verify scalar form and map-key behavior explicitly.
- Global `ObjectMapper` changes can break unrelated endpoints or message consumers. Prefer narrow fixes when the issue is boundary-specific.

## Boundary-Specific Nuances

- MVC request and response mapping, Kafka message mapping, Redis payload mapping, and JSON-column mapping often use different mapper lifecycles even inside one codebase.
- `ObjectMapper.copy()` can preserve most configuration while still drifting from future global changes. If a subsystem owns a private mapper, document that divergence.
- Kotlin default parameters interact differently with creator annotations, mix-ins, and custom deserializers. If custom deserialization exists, verify constructor invocation explicitly.
- Unknown enum handling, unknown-property handling, and coercion rules are compatibility decisions. A permissive setting may preserve old clients or may quietly accept garbage.
- If the API is documented through OpenAPI or consumer contracts, make sure the documented nullability and actual wire behavior match. Kotlin type hints alone are not enough.

## Expert Heuristics

- If the same model is used on both inbound and outbound boundaries, check whether the optimal serializer settings are actually symmetrical. Often they are not.
- If clients rely on partial update semantics, prefer an explicit patch model rather than trying to infer intent from ordinary DTO nullability.
- If a serializer bug appears after a dependency upgrade, inspect feature defaults and module registration order before rewriting DTOs.
- If compatibility matters, prove the fix with golden JSON examples or snapshot-style serialization tests, not only with one happy-path request.

## Design Rules

- Choose one naming strategy and document it.
- Keep transport DTOs separate from persistence and domain objects when contract stability matters.
- If payload evolution matters, favor additive fields and backward-compatible defaults over silent semantic changes.
- If multiple serialization contexts exist, decide which behavior is global and which is boundary-specific.

## Output Contract

Return these sections:

- `Observed behavior`: what the current mapper does.
- `Contract expectation`: what the wire format should mean.
- `Root cause`: module, DTO, annotation, or mapper configuration issue.
- `Minimal fix`: the smallest safe code or config change.
- `Compatibility risk`: what existing clients or consumers might notice.
- `Verification`: tests or sample payloads that prove the behavior.

## Guardrails

- Do not recommend random Jackson versions outside the repository's version authority.
- Do not add global mapper behavior for a local one-off issue without explaining blast radius.
- Do not hide a contract problem behind broad `JsonNode` or `Map<String, Any>` usage unless the boundary is intentionally untyped.
- Do not rely on Jackson defaults when a stable external contract matters.

## Quality Bar

A good run of this skill explains the wire contract, the mapper mechanics, and the compatibility impact in one coherent answer.
A bad run sprinkles annotations until the example payload passes while leaving the contract ambiguous or unstable.

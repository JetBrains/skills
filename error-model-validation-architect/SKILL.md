---
name: error-model-validation-architect
description: Design and implement consistent API validation and error-handling behavior for Kotlin plus Spring services. Use when defining error payloads, mapping framework and domain exceptions, standardizing HTTP status codes, adding `@ControllerAdvice`, preventing internal-detail leakage, or ensuring clients can rely on stable machine-readable error semantics across endpoints.
metadata:
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/error-model-validation-architect
---

# Error Model Validation Architect

Source mapping: Tier 2 high-value skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-07`).

## Mission

Turn ad-hoc exception handling into a deliberate public contract.
Treat validation and error mapping as part of API design, not a post-processing afterthought.

## Inputs To Read

- Endpoint contracts and sample payloads.
- Existing exception classes and error payloads.
- Validation annotations, custom validators, and business rule failures.
- Current `@ControllerAdvice`, security exception handling, and framework defaults.
- Logging and observability conventions, especially correlation ids and PII policy.

## Design The Error Taxonomy

Separate at least these categories:

- malformed request or unreadable JSON
- transport-level validation failure
- not found
- conflict or concurrency failure
- business rule rejection
- authentication and authorization failure
- downstream dependency failure
- unexpected internal failure

Do not collapse all non-success outcomes into one generic error envelope.

## Status Code Rules

- Use `400` for malformed input, type mismatch, or invalid transport shape.
- Use `401` for authentication failure and `403` for authorization failure.
- Use `404` when the addressed resource is absent.
- Use `409` for state conflicts, optimistic locking failures, duplicate idempotency keys, and uniqueness races when conflict semantics matter.
- Use `422` when the payload is structurally valid but violates business rules.
- Use `429`, `502`, `503`, or `504` deliberately when gateway or dependency behavior is part of the contract.
- Reserve `500` for genuinely unexpected internal failures.

## Advanced Contract Decisions

- Separate machine-readable error code from human-readable message. Clients should not parse prose.
- Decide whether field-level validation errors should be aggregated or first-failure only. Make the rule consistent.
- Preserve nested field paths for complex payloads and collections.
- Decide whether localization is a server concern, client concern, or documentation-only concern.
- Standardize correlation or trace identifiers in the error payload only if the platform can produce them consistently.
- Decide how unknown fields, enum mismatches, and unreadable date formats surface. These are common client-integration pain points.
- Map downstream failures carefully. Not every remote `500` should become your own `500`.
- If using RFC 7807 Problem Details, decide which extensions are stable parts of the contract and which are internal.

## Validation Rules

- Keep transport validation separate from domain validation even if both ultimately reject the request.
- Use Kotlin `@field:` targets for Bean Validation annotations on DTO constructor properties.
- Prefer dedicated validators or domain rules over abusing regex annotations for business semantics.
- Validate required configuration or invariants at startup when they are not truly request-scoped.

## Framework Exception Coverage

- Cover `MethodArgumentNotValidException`, `ConstraintViolationException`, `HttpMessageNotReadableException`, `MethodArgumentTypeMismatchException`, missing-parameter exceptions, and unsupported media-type cases explicitly if the API claims a stable error contract.
- Decide how security exceptions participate in the shared error model. `AuthenticationEntryPoint` and `AccessDeniedHandler` often need explicit alignment with controller advice.
- Decide how async, scheduled, and messaging failures are reported differently from HTTP failures. One global envelope does not fit every boundary.
- If the platform uses Problem Details, verify framework-generated problem payloads do not diverge from custom ones during upgrades.

## Expert Heuristics

- Let validation errors help the client recover, but let internal logs help operators diagnose. These are different audiences and should not share the same detail level.
- If the service is public, keep error-code taxonomy versionable and stable even when internal exception types change.
- When multiple validation layers reject the same request, choose the most user-actionable signal instead of stacking redundant errors.
- If a downstream dependency failure is part of the business path, decide whether the contract should expose dependency semantics or normalize them to your own domain.

## Output Contract

Return these sections:

- `Error taxonomy`: the categories and stable codes.
- `Status mapping`: which exception or failure type maps to which status and why.
- `Payload shape`: the fields that belong in every error.
- `Framework coverage`: which framework exceptions must be handled explicitly.
- `Minimal implementation plan`: advice class, exception types, and tests to add.
- `Logging rule`: what to log server-side versus what to expose to clients.

## Guardrails

- Do not leak stack traces, SQL fragments, class names, tokens, or secrets to clients.
- Do not use one generic message for all failures if clients need actionable categories.
- Do not map every domain failure to `400`.
- Do not let security exceptions bypass the common contract unintentionally.
- Do not rely on the framework default error shape if the service claims to have a stable API contract.

## Quality Bar

A good run of this skill gives clients a predictable error language and gives operators enough server-side detail to debug safely.
A bad run produces a pretty error JSON that still conflates malformed input, business rejection, and internal failure.

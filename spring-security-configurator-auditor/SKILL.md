---
name: spring-security-configurator-auditor
description: Design and audit Spring Security configurations for Kotlin plus Spring services, including filter chains, JWT or OAuth2 resource server setup, method security, CORS, CSRF rationale, and public endpoint exposure. Use when adding or reviewing authentication and authorization, narrowing access rules, validating token handling, or checking for insecure defaults and accidental exposure.
metadata:
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/spring-security-configurator-auditor
---

# Spring Security Configurator Auditor

Source mapping: Tier 2 high-value skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-13`).

## Mission

Produce a security model that is explicit, minimal, and testable.
Optimize for least privilege and correct failure semantics, not for shortest config.

## Read First

- Current `SecurityFilterChain` or chains.
- Endpoint inventory, including actuator, docs, and internal admin routes.
- Authentication model: session, JWT, OAuth2 resource server, API keys, mTLS, or mixed.
- Authorization model: roles, scopes, claims, method security, tenant boundaries.
- CORS, CSRF, and security-related tests.

## Design Sequence

1. Define who the clients are: browser, internal service, public API consumer, job, or operator.
2. Define authentication mechanism and token trust boundaries.
3. Enumerate public endpoints explicitly.
4. Define authorization at URL and method level.
5. Define 401 and 403 behavior.
6. Add tests for the critical allowed and denied paths.

## Core Security Rules

- Prefer explicit allowlists for public endpoints.
- Validate JWT issuer, audience, expiration, signature, and clock-skew assumptions deliberately.
- Map claims to authorities with a documented rule. Do not assume the default claim mapping is correct for the identity provider.
- Keep method security and request security aligned. One should not silently compensate for the other.
- Treat CORS as a policy surface, not a browser nuisance.

## Advanced Security Traps

- Multiple filter chains are ordered. A broad matcher in the wrong chain can shadow a more specific secure chain.
- `permitAll` for docs or actuator endpoints often expands further than intended when matchers are too broad.
- CSRF is not automatically irrelevant just because the app uses tokens somewhere. Browser-based flows and cookie-backed auth change the answer.
- Async execution, schedulers, and message listeners may not carry the same security context as request threads.
- Method security on internal helper methods does not help if the call never crosses the proxy boundary.
- JWT validation without issuer or audience checks is weaker than many teams realize.
- CORS preflight failures can look like auth failures even when the backend logic is correct.
- Security behavior differs between servlet and reactive stacks; do not transplant config blindly.

## Advanced AuthZ And Token Nuances

- Path-based authorization is often necessary but rarely sufficient. Tenant, ownership, or resource-state checks may belong in method or domain-level authorization.
- JWT key rotation, JWKS caching, and clock skew policy are operational concerns as well as security concerns. Token validation must keep working during key rollover.
- Opaque token introspection, JWT validation, and gateway-terminated auth have different failure modes and trust boundaries. Be explicit about which layer owns what.
- Custom claim mapping can accidentally drop scopes or elevate privileges if the mapping rule is too permissive.
- Security headers, session creation policy, and stateless assumptions should match the actual client model rather than copied boilerplate.

## Expert Heuristics

- Model "who can do what to which resource under which tenant or context" before writing matcher code.
- Prefer deny-by-default designs where new endpoints start closed unless explicitly opened.
- If browser and machine clients coexist, treat them as separate security surfaces even inside one service.
- If an endpoint is operationally sensitive but "internal," still secure it explicitly. Internal does not mean safe.

## Output Contract

Return these sections:

- `Threat surface`: what must be protected and from whom.
- `Authentication model`: how identity is established and verified.
- `Authorization model`: how access decisions are made.
- `Critical findings or risks`: insecure defaults, over-broad rules, missing checks, missing tests.
- `Minimal secure config plan`: the smallest safe configuration or patch.
- `Verification`: security tests for both allowed and denied access.

## Guardrails

- Do not disable CSRF or frame options without explaining the trust model.
- Do not rely on default matcher behavior without checking path coverage.
- Do not leave actuator, Swagger, or internal diagnostics exposed by convenience.
- Do not generate security config without tests for the key routes.
- Do not conflate authentication failure with authorization failure.

## Quality Bar

A good run of this skill makes the access model explicit and auditable.
A bad run produces a working login flow while leaving route exposure, token validation, or test coverage dangerously vague.

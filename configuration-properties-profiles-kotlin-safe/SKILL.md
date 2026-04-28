---
name: configuration-properties-profiles-kotlin-safe
description: Design and diagnose Spring configuration, profiles, and `@ConfigurationProperties` binding for Kotlin applications. Use when property binding fails, environment-specific overrides behave unexpectedly, profile layering is confusing, secrets or defaults are modeled unsafely, or Kotlin nullability and constructor binding semantics make configuration errors hard to detect.
metadata:
  short-description: "Make Kotlin Spring config predictable"
  author: Kotlin
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/configuration-properties-profiles-kotlin-safe
---

# Configuration Properties Profiles Kotlin Safe

Source mapping: Tier 2 high-value skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-16`).

## Mission

Make configuration predictable across local, test, staging, and production environments.
Treat configuration as typed application input, not as unstructured text.

## Read First

- `application.yml`, `application.properties`, and profile-specific files.
- `@ConfigurationProperties` classes and how they are registered.
- Environment variable names, Helm values, Config Server or Vault usage, and test overrides.
- Active profile configuration and profile groups.
- Any custom converters, property placeholders, or late-bound secret conventions.

## Diagnose In This Order

1. Verify source precedence:
   - default config file
   - profile-specific file
   - environment variables
   - `SPRING_APPLICATION_JSON`
   - external config systems
2. Verify prefix mapping and key shape.
3. Verify constructor binding, defaults, and nullability.
4. Verify collection, map, nested object, duration, and data-size binding.
5. Verify profile activation and profile merge behavior.
6. Verify whether the bug exists only in tests or only in deployment templates.

## Kotlin-Safe Modeling Rules

- Model genuinely required properties as required constructor parameters or validated fields.
- Use defaults only when the default is semantically safe, not merely convenient.
- Use nullable only when the application can handle absence explicitly.
- Keep properties classes small and focused by feature or external dependency.
- Prefer type-safe wrappers such as `Duration`, `URI`, or enums when they clarify the contract.

## Advanced Configuration Traps

- Profile files merge; they do not behave like full replacement documents. Partial overrides can leave stale sibling values in place.
- Empty string is not the same as missing value. Many runtime failures come from "configured but blank" secrets.
- Nested objects can be partially bound, which may produce a shape that looks non-null but is still semantically invalid.
- Environment-variable translation from `kebab-case` to uppercase underscore is easy to misread, especially for nested maps and lists.
- `ignoreUnknownFields` or overly permissive binding can hide deployment drift.
- A property that fails only in one environment may really be a templating or secret-injection problem, not a Spring binding problem.
- Test overrides can mask missing production defaults if tests define properties more generously than runtime.
- Dynamic refresh or cloud config introduces lifecycle questions: startup validation alone may not be enough if config can change after boot.

## Spring Config Data Nuances

- `spring.config.import`, profile groups, and `spring.config.activate.on-profile` can reshape the final config graph in ways that are not obvious from file names alone.
- List replacement and map merge behavior differ. A partial override may replace a whole list while only merging a map subtree.
- Placeholder resolution timing matters. A property can appear bound yet still fail later if it depends on an unavailable placeholder or secret source.
- Deprecating or renaming configuration keys is a migration problem. If compatibility matters, plan aliases or a staged rollout instead of a hard rename.
- Overusing `@Profile` on beans can make environment behavior harder to reason about than using one typed configuration model with feature flags or explicit toggles.

## Expert Heuristics

- Prefer one typed configuration class per external dependency or feature slice. Giant omnibus config classes become impossible to validate and evolve safely.
- When the same property is set in several layers, document the intended owner. Hidden "last writer wins" behavior becomes operational debt fast.
- If a config issue appears only after deploy, compare rendered manifests or effective environment variables before editing Kotlin classes.
- If startup must fail on bad config, prove that with a test or smoke check. Teams often think they fail fast when they actually fail on first traffic.

## Design Rules

- Validate required configuration at startup whenever possible.
- Keep secrets out of committed config examples unless obviously fake and explicitly marked.
- Model profile intent explicitly: local developer convenience, test isolation, operational environments, and feature toggles are different concerns.
- Prefer one source of truth for a property family. Duplicated definitions across profiles create drift fast.

## Output Contract

Return these sections:

- `Binding model`: how the properties should be represented in Kotlin.
- `Source precedence`: where the effective value should come from.
- `Root cause`: exact mismatch in key, type, profile, or source.
- `Minimal fix`: the smallest safe file, code, or env change.
- `Startup validation`: what must fail fast at boot.
- `Verification`: how to prove the resolved config in the target environment.

## Guardrails

- Do not hardcode secrets.
- Do not default required credentials to empty strings.
- Do not use nullable everywhere just to make the binder happy.
- Do not assume profile-specific files replace base config wholesale.
- Do not fix a binding symptom while ignoring the underlying precedence or environment problem.

## Quality Bar

A good run of this skill leaves the configuration model easier to reason about across environments.
A bad run "fixes" one YAML key while leaving nullability, precedence, and startup validation ambiguous.

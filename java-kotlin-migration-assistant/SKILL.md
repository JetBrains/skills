---
name: java-kotlin-migration-assistant
description: Migrate Java code to Kotlin in Spring-based codebases without changing behavior, public contracts, framework compatibility, or binary assumptions unless explicitly intended. Use when converting classes incrementally, replacing Lombok patterns, managing platform types and nullability, preserving JPA and proxy behavior, or planning the safe migration order of a mixed Java and Kotlin codebase.
metadata:
  short-description: "Migrate safely without semantic drift"
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/java-kotlin-migration-assistant
---

# Java Kotlin Migration Assistant

Source mapping: Tier 3 specialized skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-19`).

## Mission

Translate Java into Kotlin in a way that improves long-term maintainability without introducing semantic drift.
Prioritize behavioral equivalence first, idiomatic cleanup second, and broad redesign only when explicitly requested.

## Read First

- The Java class and its callers.
- Existing tests and any contract documentation.
- Spring annotations, JPA annotations, Jackson annotations, validation annotations, and logging patterns.
- Build plugins and Kotlin compiler configuration already present in the repository.
- Whether the module is internal-only or has external consumers that care about binary compatibility.

## Migration Order

Use this default order unless the repository strongly suggests otherwise:

1. Tests and support utilities.
2. Stateless helpers and internal-only classes.
3. Services and controllers.
4. Domain models and DTOs.
5. Entities and framework-sensitive classes last.

The most fragile classes are usually entities, heavily proxied services, reflection-heavy config classes, and public library APIs.

## Translation Rules

- Preserve public behavior before pursuing idiomatic compression.
- Convert fields plus getters plus setters into properties only when that does not change framework expectations.
- Replace Lombok consciously:
  - `@Data` is not a universal reason to create a Kotlin `data class`
  - `@Builder` may map to named/default parameters or may need an explicit builder for Java callers
  - `@Slf4j` usually becomes an explicit logger field or companion object
- Replace checked-exception-heavy flows carefully. Kotlin does not enforce checked exceptions, but Java callers may still depend on them semantically.
- Keep annotations on the correct target after translation. Kotlin use-site targets matter.

## Nullability And Platform-Type Rules

- Treat every Java type crossing into Kotlin as suspect until nullability is proven.
- Add explicit Kotlin nullability based on contracts, not on optimistic guesses.
- If upstream Java code lacks annotations, prefer defensive boundary checks over sprinkling `!!`.
- Distinguish internal invariants from external compatibility. Sometimes a Kotlin non-null model is correct internally even when the Java boundary remains nullable.
- Re-check collections, optionals, and maps separately. Java usage patterns often hide null values inside otherwise non-null containers.

## Spring And JPA Nuances

- Verify proxy-sensitive classes still work after translation:
  - `@Transactional`
  - `@Cacheable`
  - `@Async`
  - method security
- Do not convert JPA entities into `data class`.
- Preserve JPA constructor, field access, and equality semantics deliberately.
- Re-check `@ConfigurationProperties`, Jackson creator behavior, and validation annotations after constructor translation.
- If the project relies on interface-based proxies, do not accidentally move critical behavior into non-interface methods that callers bypass.

## Advanced Migration Traps

- Default parameters can change Java call sites, overload generation, and reflective constructor expectations.
- Kotlin properties may alter method signatures, bean-introspection shape, and serialization behavior compared with explicit Java accessors.
- Inline classes, sealed hierarchies, and scope-function-heavy refactors are not first-pass migration tools. Use them after compatibility is proven.
- Replacing mutable Java collections with read-only Kotlin interfaces does not magically make the backing data immutable.
- `equals`, `hashCode`, and `toString` generation can silently change semantics for entities, cache keys, and logging.
- Library modules may need `@JvmStatic`, `@JvmOverloads`, `@JvmField`, or explicit overload preservation for Java consumers.
- Mixed Java/Kotlin modules can expose incremental compilation and annotation-processing surprises. Keep build verification tight during migration.

## Bytecode And Interop Nuances

- `internal` visibility is a module-level bytecode concept, not a source-level privacy guarantee. Do not use it casually in previously public or Java-consumed APIs.
- Interface default methods, companion objects, and top-level functions alter Java call ergonomics and sometimes reflective lookup paths.
- SAM conversion, raw types, and wildcard variance can change overload resolution at Java call sites even when Kotlin looks cleaner.
- `Optional<T>`, primitive wrappers, arrays, and checked exceptions each deserve explicit migration treatment; do not lump them under generic nullability cleanup.
- Parameter-name retention, generated constructors, and method overloading affect frameworks that use reflection or bytecode-generated proxies.
- Annotation processors, generated metamodels, and kapt or KSP transitions can change what "successful migration" means for the build beyond source compilation.

## Expert Heuristics

- If a class is consumed by Java, optimize for interop predictability before Kotlin elegance.
- Migrate behaviorally boring classes first to establish project conventions for logging, nullability, builders, and testing.
- Delay introduction of coroutines, sealed redesigns, and stronger domain modeling until the mixed Java/Kotlin seam is stable.
- When uncertain, preserve explicit methods over collapsing everything into properties or generated helpers. Debuggability and binary stability matter during migration.

## Output Contract

Return these sections:

- `Migration scope`: what is being converted now and what stays in Java for the moment.
- `Behavioral invariants`: what must remain unchanged.
- `Kotlin translation plan`: the exact language-level changes to make.
- `Framework constraints`: Spring, Jackson, JPA, validation, and binary-compatibility concerns.
- `Verification`: tests and runtime checks that prove no semantic drift.

## Guardrails

- Do not change public API shape casually.
- Do not hide uncertain nullability behind `!!`.
- Do not mix migration with broad architectural redesign unless explicitly asked.
- Do not make entity, proxy, or serialization classes "more idiomatic" at the cost of framework correctness.

## Quality Bar

A good run of this skill yields Kotlin that is safer and cleaner while still behaving like the original Java where it matters.
A bad run produces "Java with Kotlin syntax" or, worse, a pretty rewrite that silently breaks callers, proxies, or persistence.

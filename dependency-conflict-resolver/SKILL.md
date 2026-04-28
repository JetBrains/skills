---
name: dependency-conflict-resolver
description: Diagnose and resolve Gradle and Spring classpath conflicts, version drift, and binary incompatibilities in Kotlin applications. Use when `NoSuchMethodError`, `ClassNotFoundException`, linkage errors, duplicate logging bindings, Jackson or Hibernate mismatches, or BOM-versus-explicit-version conflicts appear, and the fix must respect the repository's real version authorities.
metadata:
  short-description: "Untangle Gradle classpath conflicts"
  author: Kotlin
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/dependency-conflict-resolver
---

# Dependency Conflict Resolver

Source mapping: Tier 2 high-value skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-05`).

## Mission

Find which artifact version actually wins, why it wins, and what the narrowest safe correction is.
Treat dependency conflict resolution as a version-authority problem, not a guessing contest.

## Read First

- `./gradlew dependencies` or module-scoped dependencies output.
- `./gradlew dependencyInsight --dependency <artifact>` for the failing library family.
- Root and module build files, version catalogs, imported BOMs, and plugin versions.
- The exact runtime or compile-time error:
  - `NoSuchMethodError`
  - `ClassNotFoundException`
  - `NoClassDefFoundError`
  - `AbstractMethodError`
  - duplicate binding warnings

## Diagnose In This Order

1. Identify the failing class or method owner.
2. Identify which artifact provides that class.
3. Identify all candidate versions on the graph.
4. Identify the winning version and why it won:
   - direct dependency
   - BOM or platform
   - version catalog alias
   - transitive dependency
   - conflict resolution rule
5. Identify the true version authority that should own the family.

## Advanced Classpath Traps

- A compile-time green build and a runtime linkage error usually means binary incompatibility between resolved artifacts, not a missing import.
- Jackson, Netty, SLF4J, Logback, Kotlin stdlib, and Hibernate families should usually be version-aligned as families, not patched one artifact at a time.
- Plugin classpath and application classpath are different worlds. A plugin upgrade may not fix a runtime library conflict and vice versa.
- Test runtime can differ from main runtime. A conflict that appears only in tests may come from test fixtures, mock libraries, or test containers support.
- `enforcedPlatform`, `strictly`, and exclusions can fix a conflict or silently lock the graph into brittle behavior. Use them deliberately.
- Multiple logging bindings often create confusing startup behavior long before they fail hard.
- Shading or relocation is rarely the first answer in an ordinary Spring Boot app. Reach for it only when classpath isolation is truly required.

## Gradle Resolution Nuances

- Variant-aware resolution can choose artifacts by attributes such as usage, JVM version, and capabilities. A "present" dependency may still resolve to the wrong variant.
- BOM import order and multiple platforms can subtly change which family wins even when the declared versions look reasonable.
- `api` versus `implementation` leakage in shared modules can make conflicts appear only in downstream consumers.
- Duplicate classes from repackaged or relocated jars may not surface until runtime classloading or auto-configuration scans.
- Annotation processors, KSP, and plugin classpaths may need separate alignment from the main application runtime.

## Expert Heuristics

- If the symptom is runtime-only, inspect the packaged artifact or runtime classpath, not only IDE dependency trees.
- If a library family is managed by Spring Boot, prefer returning ownership to the BOM over adding more local pins.
- If the same conflict recurs across modules, solve it at the platform or convention level instead of patching one module at a time.
- If a dependency looks unused, verify reflective, auto-configured, or service-loader usage before removing it.

## Preferred Resolution Strategy

- Prefer removing an unnecessary explicit version when the BOM already manages a compatible one.
- Prefer aligning the entire affected dependency family.
- Prefer a direct, documented version override over hidden exclusions if an override is truly needed.
- Use exclusions only when a dependency is genuinely wrong for the module, not merely inconvenient.
- Explain why the chosen layer should own the version so the conflict does not return on the next upgrade.

## Output Contract

Return these sections:

- `Conflict summary`: what is colliding and where the symptom appears.
- `Winning path`: how the resolved artifact version is selected.
- `Version authority`: which file or platform should own the fix.
- `Minimal fix`: the smallest safe dependency or platform change.
- `Verification`: compile-time and runtime checks that confirm the graph is healthy.

## Guardrails

- Do not invent version numbers without tracing the existing compatibility model.
- Do not spray exclusions until the graph goes green.
- Do not use `force` as the default answer.
- Do not fix only the visibly failing artifact when the whole library family is misaligned.

## Quality Bar

A good run of this skill explains the conflict path and resolves it at the right authority layer.
A bad run produces a one-off version pin that compiles today and recreates the same conflict in the next branch.

---
name: gradle-kotlin-dsl-doctor
description: Generate, debug, and repair Kotlin + Spring Gradle builds with minimal, compatible changes. Use when `build.gradle.kts` or `settings.gradle.kts` is failing, plugins or toolchains are incompatible, dependency management is drifting from the Spring Boot BOM, test or runtime classpaths are broken, or a Kotlin DSL patch must be safe and incremental.
metadata:
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/gradle-kotlin-dsl-doctor
---

# Gradle Kotlin DSL Doctor

Source mapping: Tier 1 critical skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-04`).

## Mission

Stabilize the build with the smallest defensible change.
Treat Gradle problems as compatibility and model problems, not only syntax problems.

## Read First

- `settings.gradle.kts`
- root and module `build.gradle.kts`
- `gradle.properties`
- `gradle/libs.versions.toml` or other version catalogs
- `gradle-wrapper.properties`
- the exact Gradle output from the failing task

## Classify The Failure

Classify the problem before editing anything:

- plugin resolution or plugin version mismatch
- Kotlin DSL syntax or type-safe accessor issue
- dependency resolution or BOM conflict
- JDK toolchain or target mismatch
- source set or test runtime misconfiguration
- compiler plugin problem such as `plugin.spring`, `plugin.jpa`, KAPT, or KSP
- task wiring or multi-module convention issue

## Work Sequence

1. Identify the failing task and the first meaningful error line.
2. Determine whether the breakage is in plugin resolution, dependency resolution, compilation, test execution, or packaging.
3. Extract the current version authorities:
   - Spring Boot plugin
   - Kotlin plugin
   - Gradle wrapper
   - JDK toolchain
   - version catalog or convention plugin
4. Verify whether the project should inherit versions from the Spring Boot BOM instead of declaring them manually.
5. Patch the narrowest file possible. Prefer module-local fixes over global rewrites.
6. Recommend the smallest verification command that proves the fix before running the full build.

## Kotlin And Spring Checks

- Verify `kotlin("plugin.spring")` when Spring-managed classes rely on proxies.
- Verify `kotlin("plugin.jpa")` when JPA entities are present.
- Verify JVM target and Java toolchain alignment.
- Verify `kotlin-reflect`, serialization, and coroutine libraries align with the Kotlin version in use.
- Verify whether annotation processing should use KAPT or KSP in this project.

## Preferred Fix Style

- Remove unnecessary explicit versions before adding new ones.
- Let the Spring Boot BOM manage compatible dependency versions whenever possible.
- Prefer small diffs over full-file replacement.
- Preserve existing conventions such as version catalogs, convention plugins, or build logic modules.
- If a fix spans several modules, explain the dependency graph that forces it.

## Advanced Build Diagnostics

- Distinguish dependency declaration problems from variant-selection problems. A dependency can exist and still resolve the wrong JVM target, classifier, or capability.
- Check whether the repo uses `platform(...)` or `enforcedPlatform(...)` in addition to the Spring Boot BOM. That changes how conflict resolution behaves.
- Check whether the real plugin source of truth is `pluginManagement`, a version catalog, or a convention plugin rather than the module build file.
- Check whether the Boot plugin is applied to library modules that should publish plain jars instead of executable jars.
- Check configuration-cache compatibility if the project is trying to use it. Eager task access and mutable global state in custom build logic often explain strange Gradle behavior.
- Check whether KAPT, KSP, code generation, or source-set wiring requires generated-source directories or task ordering that is currently missing.
- Check dependency locking, verification metadata, or repository policy files before suggesting new repositories or version changes.
- Check test fixtures, included builds, and composite builds when inter-module dependencies behave differently in IDE and CI.

## Expert Heuristics

- Prefer explaining which layer owns a version: wrapper, plugin, platform, version catalog, or module override. This avoids repeated drift after the immediate fix.
- If the build fails only in CI, compare wrapper version, JDK vendor, `org.gradle.jvmargs`, configuration cache flags, and repository credentials before touching dependency declarations.
- If a custom task or plugin breaks under a new Gradle version, isolate that change from ordinary dependency or Kotlin compiler fixes.
- If the build uses dependency substitution or included builds, verify whether the resolved artifact is local source or published binary before diagnosing API mismatches.

## Output Contract

Return these sections:

- `Root cause`: what is broken and at which stage of the build.
- `Minimal patch`: the exact Gradle change to make.
- `Why this works`: the compatibility rule or Gradle model fact behind the patch.
- `Verification`: one or more commands in increasing confidence order.
- `Follow-up risk`: only if the fix unblocks the build but leaves technical debt behind.

## Guardrails

- Do not invent random dependency or plugin versions.
- Do not rewrite Kotlin DSL into Groovy or Maven syntax.
- Do not change unrelated modules just because they look similar.
- Do not hide version conflicts with `force` or aggressive exclusions unless there is a strong, explicit reason.
- Do not recommend clearing caches as the primary fix when the build model itself is wrong.

## High-Signal Commands

Use these when the repository permits command execution:

- `./gradlew help`
- `./gradlew build`
- `./gradlew :module:compileKotlin`
- `./gradlew dependencies`
- `./gradlew dependencyInsight --dependency <artifact>`

## Quality Bar

A good run of this skill leaves the build model clearer than before and produces a fix that survives CI.
A bad run throws version guesses at the problem, rewrites too much, or ignores the Spring Boot compatibility model.

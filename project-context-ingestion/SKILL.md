---
name: project-context-ingestion
description: Inspect a Kotlin + Spring repository before making changes or answering implementation, debugging, build, architecture, or review questions. Use when the agent must map modules, Spring Boot/Kotlin/Gradle/JDK versions, compiler plugins, profiles, dependencies, runtime stack, and architectural boundaries so later advice stays compatible with the real project.
metadata:
  short-description: "Map Kotlin/Spring repo before acting"
  author: Kotlin
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/project-context-ingestion
---

# Project Context Ingestion

Source mapping: Tier 1 critical skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-01`).

## Mission

Build an accurate mental model of the repository before proposing code, fixes, tests, or architecture advice.
Treat this skill as the default first step for all other Kotlin + Spring skills.

## Inspect First

- Read `settings.gradle.kts`, root `build.gradle.kts`, module build files, `gradle.properties`, `gradle/libs.versions.toml`, and Gradle wrapper properties if present.
- Read `application.yml`, `application.yaml`, `application.properties`, and profile-specific variants.
- Read the top-level package or module layout, plus representative controller, service, repository, configuration, and test classes.
- Read runtime and delivery files when present: `Dockerfile`, `docker-compose*.yml`, Kubernetes or Helm manifests, CI workflows, `.env.example`, and deployment scripts.
- Inspect dependency and plugin declarations before recommending any new annotation, library, or Spring API.

## Extract The Project Map

- Record the Kotlin version, Spring Boot version, Spring Framework generation, Gradle version, JDK or toolchain version, and major libraries that constrain advice.
- Record whether the project uses MVC or WebFlux, JPA or JDBC or jOOQ or MyBatis or R2DBC, synchronous or coroutine-based services, and which test stack is present.
- Record whether compiler plugins such as `kotlin("plugin.spring")`, `kotlin("plugin.jpa")`, `kotlin("plugin.serialization")`, KAPT, or KSP are enabled.
- Record whether the repository is single-module, multi-module, modular monolith, or service-per-repo.
- Record package boundaries and obvious architecture rules such as `web -> service -> persistence` or hexagonal adapters.
- Record active configuration prefixes, profiles, secret-loading patterns, and conditional bean usage.

## Work Sequence

1. Start from build and config files. Do not begin with business code.
2. Infer compatibility constraints. Treat these as hard boundaries for every later suggestion.
3. Map modules and their responsibilities. Note where HTTP, persistence, messaging, security, and tests live.
4. Identify framework mode: MVC vs WebFlux, blocking vs reactive persistence, servlet vs netty, synchronous vs coroutine style.
5. Identify Kotlin and Spring trap markers:
   - missing `plugin.spring` while AOP annotations exist
   - missing `plugin.jpa` while JPA entities exist
   - manual dependency versions that may fight the Spring Boot BOM
   - mixed blocking and reactive stacks in the same request flow
   - duplicate or profile-fragmented configuration
6. Summarize only what matters for the task at hand. Do not dump a full file inventory.
7. If the repository is incomplete, state the missing evidence and continue with bounded assumptions.

## What To Produce

Return a compact project brief with these sections:

- `Stack`: framework mode, persistence mode, test stack, build stack.
- `Compatibility constraints`: exact versions or explicit unknowns.
- `Architecture map`: modules or packages and responsibility boundaries.
- `Operational config`: profiles, property binding style, runtime and secret conventions.
- `Risk markers`: project-specific traps that may invalidate generic advice.
- `Next commands or files`: the highest-value checks to run next.

## Decision Rules

- Prefer facts from the repository over conventions from memory.
- If multiple modules disagree on versions or stack style, call that out explicitly instead of normalizing it away.
- If build files and runtime code imply different approaches, treat that as a risk, not a detail.
- If the task is narrow, still inspect the minimum build and config context before editing code.
- If another skill is invoked later, feed it the extracted constraints instead of rereading everything from scratch.

## Advanced Context Signals

- Distinguish application modules from library modules. A shared library inside the repo may intentionally avoid the Spring Boot plugin, `bootJar`, or runtime-only dependencies.
- Check whether build logic lives in convention plugins, `buildSrc`, or an included `build-logic` build. Those places often override what module build files appear to say.
- Detect `javax.*` versus `jakarta.*` imports to place the codebase on the correct migration timeline. This changes which Spring, Hibernate, and validation advice is valid.
- Detect whether the project uses generated sources such as OpenAPI codegen, QueryDSL, jOOQ, protobuf, or MapStruct. Generated code changes where safe edits belong.
- Detect whether `spring.jpa.open-in-view`, `spring.main.lazy-initialization`, `spring.aot.enabled`, or native-image related settings are active. These settings change what is safe to suggest.
- Detect whether the system relies on platform features such as Config Server, Vault, Helm-templated config, or environment-variable substitution. Local files may not show the full runtime truth.
- Detect whether observability, tracing, and correlation are first-class conventions. This affects controller, client, and incident advice.
- Detect whether the service owns its schema, shares a database, or integrates through outbox or CDC. That changes migration and transaction guidance.

## Expert Heuristics

- If the repo mixes JPA and R2DBC or MVC and WebFlux, determine whether that is an intentional boundary by module or an accidental hybrid. Advice differs sharply between the two cases.
- If the repo uses Kotlin coroutines on top of blocking persistence, call out that this is still a blocking architecture unless the persistence layer is reactive.
- If multiple dependency authorities exist, rank them. The real source of truth may be the Spring Boot plugin, a version catalog, a company convention plugin, or a platform BOM.
- If tests use different stacks than production, note that explicitly. For example, H2 in tests plus Postgres in production invalidates many persistence assumptions.
- If the repo has no obvious architecture rules, report that as a constraint instead of inventing a clean architecture that the codebase does not follow.

## Guardrails

- Do not recommend APIs or annotations from a different Spring Boot major version than the project actually uses.
- Do not add dependencies or plugin blocks without checking whether the project already gets them from a BOM, version catalog, or convention plugin.
- Do not assume MVC when `spring-boot-starter-webflux` or coroutine-web stack is present.
- Do not assume JPA just because a database exists. Verify the actual persistence technology.
- Do not propose architecture refactors before identifying the current structure and team conventions.

## Escalate Suspicion When You See

- Both servlet and reactive starters without a clear reason.
- `data class` entities together with JPA or Hibernate.
- `@Transactional`, `@Cacheable`, or `@Async` with no Kotlin Spring plugin.
- Explicit versions for Jackson, Hibernate, Netty, Logback, or SLF4J in a Boot-managed build.
- Profile-specific config files that redefine the same properties with inconsistent shapes.

## Quality Bar

A good run of this skill makes downstream advice more precise, narrower, and easier to verify.
A bad run starts suggesting code before proving which stack, versions, and conventions the repository actually uses.

---
name: spring-context-di-reasoning
description: Diagnose Spring application context startup failures, bean graph problems, missing or duplicate beans, circular dependencies, conditional auto-configuration mismatches, and profile-related wiring issues in Kotlin + Spring projects. Use when the app does not start, a bean is not created or is created unexpectedly, auto-configuration behaves strangely, or a minimal DI fix is needed instead of a broad rewrite.
metadata:
  short-description: "Resolve bean graph and context failures"
  author: Kotlin
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/spring-context-di-reasoning
---

# Spring Context DI Reasoning

Source mapping: Tier 1 critical skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-02`).

## Mission

Reconstruct why the Spring container made a specific decision and propose the narrowest fix that restores correct wiring.
Assume the visible exception is often a wrapper, not the real cause.

## Collect Evidence First

- Read the full exception chain, not only the top exception.
- Read the startup log lines around the first failure and around any `Caused by:` entries.
- Read the relevant `@Configuration`, component classes, `@Bean` methods, `@ConfigurationProperties`, and active profiles.
- Read the build context if it is not already known. Reuse output from `project-context-ingestion` when available.
- If a condition evaluation report or auto-configuration report exists, use it.

## Reconstruct The Bean Path

- Identify the bean that failed first.
- Walk constructor arguments, method parameters, and factory methods to build the dependency chain.
- Separate symptom from cause:
  - `BeanCreationException` is usually a wrapper.
  - `UnsatisfiedDependencyException` usually points to a missing or ambiguous dependency one hop deeper.
  - property binding failures often surface as bean creation failures.
- Record whether the failing bean is user-defined, auto-configured, conditional, profile-specific, or imported from another module.

## Diagnose By Category

Check these categories in order:

1. Missing bean:
   - package not scanned
   - bean class not annotated
   - `@Bean` method not imported
   - wrong module dependency
2. Ambiguous bean:
   - multiple beans of same type
   - missing `@Qualifier`
   - missing `@Primary`
3. Conditional mismatch:
   - `@Profile` not active
   - `@ConditionalOnProperty` false or missing
   - classpath condition not satisfied
4. Configuration binding failure:
   - wrong prefix
   - missing required property
   - invalid type conversion
5. Circular dependency:
   - constructor cycle
   - configuration class cycle
   - bean factory method recursion
6. Auto-configuration collision:
   - user bean unintentionally overrides or blocks auto-config
   - excluded auto-config removes a dependency chain
7. Lifecycle or initialization side effects:
   - bean does work too early in `@PostConstruct`
   - external system call during initialization

## Fix Hierarchy

Prefer fixes in this order:

1. Correct the missing import, annotation, qualifier, or profile.
2. Correct the configuration property source or binding model.
3. Narrow the scan or import boundary to the intended package or module.
4. Resolve ambiguity with `@Qualifier` or `@Primary`.
5. Refactor an actual cycle into a cleaner dependency direction.
6. Use `@Lazy` only as a deliberate escape hatch, and explain the tradeoff.

## Advanced Container Traps

- Check whether a `@Configuration` class uses `proxyBeanMethods = false`. In that mode, direct calls between `@Bean` methods do not return the managed singleton unless dependencies are expressed through method parameters.
- Check whether the missing bean is actually a `FactoryBean` product versus the factory itself.
- Check generic type narrowing. `Foo<Bar>` and `Foo<Baz>` may both exist while raw-type reasoning makes the graph look ambiguous or missing.
- Check collection and map injection semantics. `List<BeanType>` ordering, bean names, and conditional registration can hide the real cause.
- Check whether `ObjectProvider<T>`, `Optional<T>`, or lazy lookup is masking a missing dependency until first use.
- Check whether `@ConditionalOnMissingBean`, `@ConditionalOnSingleCandidate`, or ordering annotations caused an auto-configuration branch to back off unexpectedly.
- Check for environment post-processors, imported configs, or test slices that change the bean graph compared with main runtime.
- If the project uses AOT or native image workflows, check whether reflection or generated context metadata, not the source annotations alone, is the true source of failure.

## Expert Heuristics

- Prefer following the bean creation path from the first failing constructor argument over scanning all annotations in the module.
- If one fix would "make startup pass" but leaves the graph semantically wrong, reject it and explain the deeper issue.
- When the problem surfaces only in tests, compare the test slice graph with the production graph before changing production code.
- If a bean exists but the wrong implementation is selected, explain the selection mechanism, not only the missing qualifier.
- If property binding and bean creation both fail, fix property binding first. Bean graph symptoms often disappear afterward.

## Output Contract

Return these sections:

- `Diagnosis`: the most probable root cause in one or two sentences.
- `Evidence`: the exact lines, bean path, condition, or configuration fact that supports the diagnosis.
- `Minimal fix`: the smallest code or config change that addresses the root cause.
- `Alternatives`: only if there is more than one legitimate fix.
- `Verification`: how to confirm the fix, such as `./gradlew test`, `bootRun`, or a specific startup assertion.

## Guardrails

- Do not suggest `@ComponentScan("*")`, giant `scanBasePackages`, or other "scan the world" fixes.
- Do not guess bean names or qualifiers that are not present in the code.
- Do not recommend disabling auto-configuration broadly to silence a symptom.
- Do not use `@Lazy` as the default answer to cycles.
- Do not ignore profiles, conditions, or property sources when explaining bean behavior.

## Common Spring-Specific Checks

- Verify `@EnableConfigurationProperties` or equivalent registration if properties beans are missing.
- Verify whether the failing type is an interface with multiple implementations.
- Verify whether a bean lives in another Gradle module that is not on the runtime classpath.
- Verify whether a `@TestConfiguration` or test slice changes the graph only in tests.
- Verify whether the issue is really a classpath problem disguised as a bean problem.

## Quality Bar

A good run of this skill explains why Spring made the wrong decision and gives a minimal, testable fix.
A bad run waves at annotations, suggests broad scans, or ignores the dependency chain that actually failed.

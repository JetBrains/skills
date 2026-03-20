---
name: ci-cd-containerization-advisor
description: Design reproducible build, image, and deployment pipelines for Kotlin plus Spring applications, including CI verification, layered containers, rollout safety, and deployment-time migration coordination. Use when creating or improving Dockerfiles, CI workflows, image hardening, Kubernetes manifests, release gates, or deployment strategies for Spring Boot services, especially where build reproducibility and operational safety matter.
metadata:
  source: https://github.com/Kotlin/kotlin-backend-agent-skills/tree/main/.agents/skills/ci-cd-containerization-advisor
---

# CI CD Containerization Advisor

Source mapping: Tier 3 specialized skill derived from `Kotlin_Spring_Developer_Pipeline.md` (`SK-23`).

## Mission

Turn a codebase into a repeatable artifact and a safe deployment process.
Treat CI, container images, and rollout strategy as one delivery system, not disconnected YAML and Dockerfile fragments.

## Read First

- Build files, wrapper config, and artifact packaging approach.
- Current CI workflow definitions and caching strategy.
- Dockerfiles or buildpack usage.
- Deployment manifests, Helm charts, or platform-specific deployment descriptors.
- Runtime assumptions: JDK version, JVM options, health probes, config injection, migration process.

## Build Pipeline Rules

- Make the wrapper and toolchain the source of truth for build reproducibility.
- Keep CI stages explicit:
  - dependency resolution
  - compile
  - tests
  - packaging
  - security or policy checks
  - publish
- Cache deliberately, but never in a way that hides reproducibility issues.
- Publish immutable versioned artifacts; avoid "mystery latest" promotion paths.

## Container Rules

- Prefer multi-stage builds or buildpacks that produce minimal, reproducible runtime images.
- Run as non-root.
- Pin base image families deliberately and know their tradeoffs:
  - glibc vs musl
  - distroless vs debug-friendly
  - JRE vs full JDK
- Keep image layers stable so dependency and application changes cache efficiently.
- Separate build-time secrets from runtime secrets.
- Align container memory and CPU assumptions with JVM container-awareness and runtime limits.

## Deployment Rules

- Define readiness, liveness, and startup probes that reflect real application behavior.
- Coordinate schema migrations with rollout order deliberately.
- Use rolling, canary, or blue-green strategies based on blast radius and compatibility constraints.
- Prefer configuration injection that is explicit and auditable.
- Record image version, git revision, and effective configuration linkage in the deployed artifact or metadata.

## Advanced Delivery Traps

- Alpine or musl-based images can break native dependencies, DNS behavior, or performance assumptions. Smaller is not always safer.
- A fast CI cache can hide missing lockfiles, flaky dependency resolution, or undeclared build inputs.
- Running migrations inside app startup may work locally and deadlock rollout safety in production.
- Layered jars help build speed, but only if the Dockerfile or buildpack order preserves dependency-cache reuse.
- Distroless images improve hardening but reduce debugging options. Know the operator tradeoff.
- Health probes that are too eager can create crash loops during cold startup or migration windows.
- Resource requests and limits interact with JVM heap sizing, GC, and startup time. Container configuration is part of application performance.
- Supply-chain controls such as SBOM, vulnerability scanning, signature, and provenance are part of delivery quality for serious systems.

## Runtime Delivery Nuances

- PID 1 signal handling, graceful shutdown, and preStop hooks determine whether rolling deploys drain traffic cleanly or drop requests.
- Read-only root filesystems, writable temp directories, and filesystem permissions are runtime design choices, not just hardening checkboxes.
- Digest-pinned base images improve reproducibility but require deliberate patching strategy to avoid silent drift or stale images.
- Hermetic or near-hermetic builds reduce "works only in CI" surprises by making network, timestamp, and undeclared tool dependencies visible.
- Remote build cache can be a speed win or a correctness trap depending on how well task inputs are modeled.
- Preview or ephemeral environments can catch config and migration issues early, but only if they use realistic secrets, networking, and backing services.

## Expert Heuristics

- Make local, CI, and production use the same major JDK and Gradle assumptions whenever possible.
- Prefer one clean deployment path over several partially maintained ones.
- Design deployment gates around rollback confidence and blast radius, not only around green unit tests.
- Prefer one debug-friendly escape hatch in operations even if production images are hardened and minimal.
- Treat supply-chain metadata as something operators may depend on later for incident response, not only for compliance.
- If the service needs graceful drain or long request handling, prove shutdown behavior under rollout in tests or staging, not only by configuration reading.
- If zero-downtime matters, force deployment and migration strategy to prove backward compatibility, not just assume it.
- Design the pipeline so that a failed deploy is cheap to stop and cheap to roll back.

## Output Contract

Return these sections:

- `Artifact strategy`: how the service is built and packaged.
- `CI plan`: stages, caches, gates, and artifact publication.
- `Container plan`: image build strategy, hardening, and runtime assumptions.
- `Deployment plan`: probe design, rollout strategy, config injection, and migration coordination.
- `Operational risks`: what could go wrong during build or deploy.
- `Verification`: the checks that prove the pipeline is reproducible and the rollout is safe.

## Guardrails

- Do not use floating production image tags.
- Do not run as root without a strong reason.
- Do not assume the same Docker or CPU environment locally and in CI unless proven.
- Do not couple schema migration execution to app boot casually.
- Do not optimize image size at the expense of runtime correctness or operability.

## Quality Bar

A good run of this skill gives the team a build and deployment path that is repeatable, observable, and rollback-aware.
A bad run outputs a fashionable Dockerfile and CI YAML that still leave runtime drift and deployment risk unresolved.

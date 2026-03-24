# Skills Catalog

This repository contains a JetBrains-filtered and JetBrains-verified snapshot of upstream GitHub agent skills collected into a flat local directory layout for easier discovery and reuse.

Each skill lives in its own top-level directory. Every `SKILL.md` keeps an exact `metadata.source` link to the original upstream skill path.

## Disclaimer
These skills are provided for demonstration and educational purposes only. While some of these capabilities may be available in Claude, the implementations and behaviors you receive from Claude may differ from what is shown in these skills. These skills are meant to illustrate patterns and possibilities. Always test skills thoroughly in your own environment before relying on them for critical tasks.

## Summary

- Total skills: 129
- Upstream repositories: 13
- Attribution rule: the `Author` column uses the explicit skill front matter when present; otherwise it falls back to the upstream maintainer or organization.

## Security Scanning

Manual run:

```bash
./.scripts/run-skill-scanner.sh
```

Default output:

- `.reports/skill-scanner/skill-scan-report.md`

CI:

- `.github/workflows/skill-scanner.yml` runs Cisco's [`skill-scanner`](https://github.com/cisco-ai-defense/skill-scanner) on push, pull request, and manual dispatch
- push and pull request runs scan only the changed skill directories and fail only on changed-skill error-level findings
- `workflow_dispatch` runs a full-repo audit and uploads the report artifact without blocking on inherited upstream findings
- the workflow stores the full scan output as an artifact
- SARIF upload to GitHub Code Scanning is best-effort and only succeeds when GitHub Code Security is enabled for the repository
- the workflow fails when the processed report still contains changed-skill error-level findings

## Skills

| Author | Skill | Repository |
| --- | --- | --- |
| Anthropic | [`algorithmic-art`](./algorithmic-art) | [anthropics/skills](https://github.com/anthropics/skills) |
| Anthony Fu | [`antfu`](./antfu) | [antfu/skills](https://github.com/antfu/skills) |
| Vercel | [`antfu-web-design-guidelines`](./antfu-web-design-guidelines) | [antfu/skills](https://github.com/antfu/skills) |
| Anthropic | [`anthropics-pdf`](./anthropics-pdf) | [anthropics/skills](https://github.com/anthropics/skills) |
| Anthropic | [`brand-guidelines`](./brand-guidelines) | [anthropics/skills](https://github.com/anthropics/skills) |
| Anthropic | [`canvas-design`](./canvas-design) | [anthropics/skills](https://github.com/anthropics/skills) |
| runkids | [`changelog`](./changelog) | [runkids/skillshare](https://github.com/runkids/skillshare) |
| Kotlin | [`ci-cd-containerization-advisor`](./ci-cd-containerization-advisor) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| runkids | [`cli-e2e-test`](./cli-e2e-test) | [runkids/skillshare](https://github.com/runkids/skillshare) |
| OpenAI | [`cloudflare-deploy`](./cloudflare-deploy) | [openai/skills](https://github.com/openai/skills) |
| runkids | [`codebase-audit`](./codebase-audit) | [runkids/skillshare](https://github.com/runkids/skillshare) |
| forketyfork | [`compose-ui-test-server`](./compose-ui-test-server) | [forketyfork/compose-ui-test-server](https://github.com/forketyfork/compose-ui-test-server) |
| Vercel | [`composition-patterns`](./composition-patterns) | [vercel-labs/agent-skills](https://github.com/vercel-labs/agent-skills) |
| Kotlin | [`configuration-properties-profiles-kotlin-safe`](./configuration-properties-profiles-kotlin-safe) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Kotlin | [`dependency-conflict-resolver`](./dependency-conflict-resolver) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| runkids | [`devcontainer`](./devcontainer) | [runkids/skillshare](https://github.com/runkids/skillshare) |
| OpenAI | [`doc`](./doc) | [openai/skills](https://github.com/openai/skills) |
| Anthropic | [`doc-coauthoring`](./doc-coauthoring) | [anthropics/skills](https://github.com/anthropics/skills) |
| Anthropic | [`docx`](./docx) | [anthropics/skills](https://github.com/anthropics/skills) |
| Kotlin | [`domain-decomposition-api-design-advisor`](./domain-decomposition-api-design-advisor) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Kotlin | [`error-model-validation-architect`](./error-model-validation-architect) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| OpenAI | [`figma`](./figma) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`figma-implement-design`](./figma-implement-design) | [openai/skills](https://github.com/openai/skills) |
| Anthropic | [`frontend-design`](./frontend-design) | [anthropics/skills](https://github.com/anthropics/skills) |
| Google Gemini | [`gemini-api-dev`](./gemini-api-dev) | [google-gemini/gemini-skills](https://github.com/google-gemini/gemini-skills) |
| Google Gemini | [`gemini-interactions-api`](./gemini-interactions-api) | [google-gemini/gemini-skills](https://github.com/google-gemini/gemini-skills) |
| Google Gemini | [`gemini-live-api-dev`](./gemini-live-api-dev) | [google-gemini/gemini-skills](https://github.com/google-gemini/gemini-skills) |
| OpenAI | [`gh-address-comments`](./gh-address-comments) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`gh-fix-ci`](./gh-fix-ci) | [openai/skills](https://github.com/openai/skills) |
| Kotlin | [`gradle-kotlin-dsl-doctor`](./gradle-kotlin-dsl-doctor) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Google Workspace | [`gws-admin-reports`](./gws-admin-reports) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-calendar`](./gws-calendar) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-chat`](./gws-chat) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-classroom`](./gws-classroom) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-docs`](./gws-docs) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-drive`](./gws-drive) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-events`](./gws-events) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-forms`](./gws-forms) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-gmail`](./gws-gmail) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-keep`](./gws-keep) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-meet`](./gws-meet) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-modelarmor`](./gws-modelarmor) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-people`](./gws-people) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-shared`](./gws-shared) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-sheets`](./gws-sheets) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-slides`](./gws-slides) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-tasks`](./gws-tasks) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| Google Workspace | [`gws-workflow`](./gws-workflow) | [googleworkspace/cli](https://github.com/googleworkspace/cli) |
| OpenAI | [`imagegen`](./imagegen) | [openai/skills](https://github.com/openai/skills) |
| runkids | [`implement-feature`](./implement-feature) | [runkids/skillshare](https://github.com/runkids/skillshare) |
| Kotlin | [`integration-resilience-engineer`](./integration-resilience-engineer) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Anthropic | [`internal-comms`](./internal-comms) | [anthropics/skills](https://github.com/anthropics/skills) |
| Kotlin | [`jackson-kotlin-serialization-specialist`](./jackson-kotlin-serialization-specialist) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Kotlin | [`java-kotlin-migration-assistant`](./java-kotlin-migration-assistant) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Kotlin | [`jpa-spring-data-kotlin-mapper`](./jpa-spring-data-kotlin-mapper) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| OpenAI | [`jupyter-notebook`](./jupyter-notebook) | [openai/skills](https://github.com/openai/skills) |
| JetBrains | [`kotlin-backend-jpa-entity-mapping`](./kotlin-backend-jpa-entity-mapping) | [Kotlin/kotlin-agent-skills](https://github.com/Kotlin/kotlin-agent-skills) |
| Kotlin | [`kotlin-idiomatic-refactorer-spring-aware`](./kotlin-idiomatic-refactorer-spring-aware) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Kotlin | [`kotlin-spring-proxy-compatibility`](./kotlin-spring-proxy-compatibility) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| JetBrains | [`kotlin-tooling-agp9-migration`](./kotlin-tooling-agp9-migration) | [Kotlin/kotlin-agent-skills](https://github.com/Kotlin/kotlin-agent-skills) |
| JetBrains | [`kotlin-tooling-cocoapods-spm-migration`](./kotlin-tooling-cocoapods-spm-migration) | [Kotlin/kotlin-agent-skills](https://github.com/Kotlin/kotlin-agent-skills) |
| JetBrains | [`kotlin-tooling-java-to-kotlin`](./kotlin-tooling-java-to-kotlin) | [Kotlin/kotlin-agent-skills](https://github.com/Kotlin/kotlin-agent-skills) |
| OpenAI | [`linear`](./linear) | [openai/skills](https://github.com/openai/skills) |
| forketyfork | [`managing-youtrack`](./managing-youtrack) | [forketyfork/agentic-skills](https://github.com/forketyfork/agentic-skills) |
| Anthropic | [`mcp-builder`](./mcp-builder) | [anthropics/skills](https://github.com/anthropics/skills) |
| OpenAI | [`netlify-deploy`](./netlify-deploy) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`notion-knowledge-capture`](./notion-knowledge-capture) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`notion-meeting-intelligence`](./notion-meeting-intelligence) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`notion-research-documentation`](./notion-research-documentation) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`notion-spec-to-implementation`](./notion-spec-to-implementation) | [openai/skills](https://github.com/openai/skills) |
| Anthony Fu | [`nuxt`](./nuxt) | [antfu/skills](https://github.com/antfu/skills) |
| Kotlin | [`observability-integrator`](./observability-integrator) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| OpenAI | [`openai-docs`](./openai-docs) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`openai-pdf`](./openai-pdf) | [openai/skills](https://github.com/openai/skills) |
| Kotlin | [`performance-concurrency-advisor`](./performance-concurrency-advisor) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Anthony Fu | [`pinia`](./pinia) | [antfu/skills](https://github.com/antfu/skills) |
| OpenAI | [`playwright`](./playwright) | [openai/skills](https://github.com/openai/skills) |
| Anthony Fu | [`pnpm`](./pnpm) | [antfu/skills](https://github.com/antfu/skills) |
| Supabase | [`postgres-best-practices`](./postgres-best-practices) | [supabase/agent-skills](https://github.com/supabase/agent-skills) |
| Anthropic | [`pptx`](./pptx) | [anthropics/skills](https://github.com/anthropics/skills) |
| Kotlin | [`production-incident-responder`](./production-incident-responder) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Kotlin | [`project-context-ingestion`](./project-context-ingestion) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Vercel | [`react-best-practices`](./react-best-practices) | [vercel-labs/agent-skills](https://github.com/vercel-labs/agent-skills) |
| Vercel | [`react-native-skills`](./react-native-skills) | [vercel-labs/agent-skills](https://github.com/vercel-labs/agent-skills) |
| OpenAI | [`render-deploy`](./render-deploy) | [openai/skills](https://github.com/openai/skills) |
| Kotlin | [`schema-migration-planner`](./schema-migration-planner) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| OpenAI | [`screenshot`](./screenshot) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`security-best-practices`](./security-best-practices) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`security-threat-model`](./security-threat-model) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`sentry`](./sentry) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`skill-creator`](./skill-creator) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`skill-installer`](./skill-installer) | [openai/skills](https://github.com/openai/skills) |
| runkids | [`skillshare`](./skillshare) | [runkids/skillshare](https://github.com/runkids/skillshare) |
| Anthropic | [`slack-gif-creator`](./slack-gif-creator) | [anthropics/skills](https://github.com/anthropics/skills) |
| Anthony Fu | [`slidev`](./slidev) | [antfu/skills](https://github.com/antfu/skills) |
| OpenAI | [`sora`](./sora) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`speech`](./speech) | [openai/skills](https://github.com/openai/skills) |
| OpenAI | [`spreadsheet`](./spreadsheet) | [openai/skills](https://github.com/openai/skills) |
| Kotlin | [`spring-context-di-reasoning`](./spring-context-di-reasoning) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Kotlin | [`spring-kotlin-code-review`](./spring-kotlin-code-review) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Kotlin | [`spring-mvc-webflux-api-builder`](./spring-mvc-webflux-api-builder) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Kotlin | [`spring-security-configurator-auditor`](./spring-security-configurator-auditor) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Kotlin | [`stacktrace-log-triage`](./stacktrace-log-triage) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| JetBrains | [`teamcity-cli`](./teamcity-cli) | [JetBrains/teamcity-cli](https://github.com/JetBrains/teamcity-cli) |
| Kotlin | [`test-suite-builder`](./test-suite-builder) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| Anthropic | [`theme-factory`](./theme-factory) | [anthropics/skills](https://github.com/anthropics/skills) |
| Kotlin | [`transaction-consistency-designer`](./transaction-consistency-designer) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| OpenAI | [`transcribe`](./transcribe) | [openai/skills](https://github.com/openai/skills) |
| Anthony Fu | [`tsdown`](./tsdown) | [antfu/skills](https://github.com/antfu/skills) |
| Anthony Fu | [`turborepo`](./turborepo) | [antfu/skills](https://github.com/antfu/skills) |
| runkids | [`ui-website-style`](./ui-website-style) | [runkids/skillshare](https://github.com/runkids/skillshare) |
| Anthony Fu | [`unocss`](./unocss) | [antfu/skills](https://github.com/antfu/skills) |
| runkids | [`update-docs`](./update-docs) | [runkids/skillshare](https://github.com/runkids/skillshare) |
| Kotlin | [`upgrade-breaking-change-navigator`](./upgrade-breaking-change-navigator) | [Kotlin/kotlin-backend-agent-skills](https://github.com/Kotlin/kotlin-backend-agent-skills) |
| OpenAI | [`vercel-deploy`](./vercel-deploy) | [openai/skills](https://github.com/openai/skills) |
| Vercel | [`vercel-deploy-claimable`](./vercel-deploy-claimable) | [vercel-labs/agent-skills](https://github.com/vercel-labs/agent-skills) |
| Vercel | [`vercel-labs-web-design-guidelines`](./vercel-labs-web-design-guidelines) | [vercel-labs/agent-skills](https://github.com/vercel-labs/agent-skills) |
| Google Gemini | [`vertex-ai-api-dev`](./vertex-ai-api-dev) | [google-gemini/gemini-skills](https://github.com/google-gemini/gemini-skills) |
| Anthony Fu | [`vite`](./vite) | [antfu/skills](https://github.com/antfu/skills) |
| Anthony Fu | [`vitepress`](./vitepress) | [antfu/skills](https://github.com/antfu/skills) |
| Anthony Fu | [`vitest`](./vitest) | [antfu/skills](https://github.com/antfu/skills) |
| Anthony Fu | [`vue`](./vue) | [antfu/skills](https://github.com/antfu/skills) |
| vuejs-ai | [`vue-best-practices`](./vue-best-practices) | [antfu/skills](https://github.com/antfu/skills) |
| vuejs-ai | [`vue-router-best-practices`](./vue-router-best-practices) | [antfu/skills](https://github.com/antfu/skills) |
| vuejs-ai | [`vue-testing-best-practices`](./vue-testing-best-practices) | [antfu/skills](https://github.com/antfu/skills) |
| Anthony Fu | [`vueuse-functions`](./vueuse-functions) | [antfu/skills](https://github.com/antfu/skills) |
| Anthropic | [`webapp-testing`](./webapp-testing) | [anthropics/skills](https://github.com/anthropics/skills) |
| Anthropic | [`xlsx`](./xlsx) | [anthropics/skills](https://github.com/anthropics/skills) |
| OpenAI | [`yeet`](./yeet) | [openai/skills](https://github.com/openai/skills) |

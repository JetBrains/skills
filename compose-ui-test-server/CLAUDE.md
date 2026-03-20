# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

compose-ui-test-server is a Kotlin library that enables AI coding agents and automation tools to control Compose Desktop applications at runtime via HTTP. When enabled, an embedded Ktor server exposes REST endpoints for clicking elements, entering text, waiting for UI elements, and capturing screenshots.

## Build Commands

```bash
# Build and run tests
./gradlew build --warning-mode fail

# Run tests only
./gradlew test

# Run the application in normal mode
./gradlew run

# Run with test server enabled (for agent control)
COMPOSE_UI_TEST_SERVER_ENABLED=true ./gradlew run

# Run with custom port
COMPOSE_UI_TEST_SERVER_ENABLED=true COMPOSE_UI_TEST_SERVER_PORT=8080 ./gradlew run

# Publish to Maven Central (release only)
./gradlew publishAndReleaseToMavenCentral --no-configuration-cache
```

## Architecture

### Core Components

- **`ComposeUiTestServer`** - Main HTTP server using Ktor/Netty. Takes a `ComposeUiTest` instance and exposes UI operations as HTTP endpoints. Supports registering custom endpoints via `registerEndpoint()`.

- **`runApplication()`** - Zero-configuration launcher that automatically switches between normal desktop mode and agent-controlled mode based on the `COMPOSE_UI_TEST_SERVER_ENABLED` environment variable.

- **`TestEndpoint`** - Interface for custom endpoints. Implement `Route.configure(composeTest: ComposeUiTest)` to add app-specific shortcuts.

### Endpoint Pattern

Built-in endpoints are in `endpoint/` package. Each endpoint class implements `TestEndpoint` and can be individually enabled/disabled via `ComposeUiTestServerConfig`. Endpoints receive a `ComposeUiTest` instance to perform UI operations.

### Configuration

- `WindowConfig` - Window settings for normal mode (title, dimensions)
- `ComposeUiTestServerConfig` - Server settings (port, host, enabled endpoints, timeouts)
- Environment variables: `COMPOSE_UI_TEST_SERVER_ENABLED`, `COMPOSE_UI_TEST_SERVER_PORT`

### HTTP Endpoints (default port 54345)

| Endpoint | Purpose |
|----------|---------|
| `GET /health` | Health check |
| `GET /onNodeWithTag/{tag}/performClick` | Click by test tag |
| `GET /onNodeWithText/{text}/performClick` | Click by text |
| `GET /onNodeWithTag/{tag}/performTextInput?text=...` | Text input |
| `GET /waitUntilExactlyOneExists/tag/{tag}?timeout=5000` | Wait for element |
| `GET /waitForIdle` | Wait for UI idle |
| `GET /captureScreenshot?path=/tmp/screenshot.png` | Screenshot |

## Technology Stack

- Kotlin 2.3.0 with Kotlin Multiplatform (JVM only)
- Compose Multiplatform 1.10.0
- Ktor 3.3.3 (embedded Netty server)
- Kotlinx Serialization for JSON
- JDK 17

## CI/CD

- GitHub Actions runs `./gradlew build --warning-mode fail` on push/PR to main
- Releases triggered by `v*` tags, publish to Maven Central via Sonatype Central Portal

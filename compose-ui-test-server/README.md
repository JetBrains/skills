# compose-ui-test-server

[![Build status](https://github.com/forketyfork/compose-ui-test-server/actions/workflows/build.yml/badge.svg)](https://github.com/forketyfork/compose-ui-test-server/actions/workflows/build.yml)
[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![Release](https://img.shields.io/github/v/release/forketyfork/compose-ui-test-server)](https://github.com/forketyfork/compose-ui-test-server/releases)

A library that enables coding agents and automation tools to control Compose Desktop applications at runtime via HTTP. Agents can click buttons, enter text, wait for UI elements, and capture screenshots through simple REST API calls.

## Use Case

This library is designed for **AI coding agents** (like Claude Code) to interact with running Compose Desktop applications. When enabled, the app exposes an HTTP server that agents can use to:

- Navigate through the UI by clicking buttons and entering text
- Wait for specific UI elements to appear
- Capture screenshots to verify the current state
- Perform automated workflows and testing scenarios

## Features

- HTTP-based control of running Compose Desktop apps
- Zero-configuration launcher with automatic mode switching
- Conditional startup via environment variable (disabled by default)
- Built-in endpoints for common UI operations
- Extensible with custom endpoints for app-specific shortcuts
- Screenshot capture for visual verification

## Installation

Add the dependency to your desktop source set in `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
        val desktopMain by getting {
            dependencies {
                implementation("io.github.forketyfork:compose-ui-test-server:0.2.0")

                // Required: Compose UI Test framework
                implementation(compose.uiTest)
            }
        }
    }
}
```

## Quick Start

### 1. Use the Application Launcher

Replace your `main()` function with `runApplication`:

```kotlin
import io.github.forketyfork.composeuittest.WindowConfig
import io.github.forketyfork.composeuittest.runApplication

fun main() =
    runApplication(
        windowConfig = WindowConfig(
            title = "My App",
            minimumWidth = 1024,
            minimumHeight = 768,
        ),
    ) {
        App()
    }
```

That's it! Your app now supports agent control with zero additional configuration.

### 2. Add Test Tags to UI Elements

For agents to interact with UI elements, add test tags:

```kotlin
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

Button(
    onClick = { /* ... */ },
    modifier = Modifier.testTag("login_button")
) {
    Text("Login")
}

TextField(
    value = username,
    onValueChange = { username = it },
    modifier = Modifier.testTag("username_field")
)
```

### 3. Run Your App

**Normal mode** (default):
```bash
./gradlew run
```

**Agent-controlled mode**:
```bash
COMPOSE_UI_TEST_SERVER_ENABLED=true ./gradlew run
```

When the environment variable is set, your app automatically starts with an HTTP server that agents can use to control the UI.

### 4. Control via HTTP

Once running in agent-controlled mode:

```bash
# Health check
curl http://localhost:54345/health

# Click a button by test tag
curl http://localhost:54345/onNodeWithTag/submit_button/performClick

# Enter text into a field
curl "http://localhost:54345/onNodeWithTag/username_field/performTextInput?text=myuser"

# Wait for an element to appear
curl "http://localhost:54345/waitUntilExactlyOneExists/tag/welcome_screen?timeout=5000"

# Capture a screenshot
curl "http://localhost:54345/captureScreenshot?path=/tmp/current_state.png"
```

## Available Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /health` | Health check - returns "OK" |
| `GET /onNodeWithTag/{tag}/performClick` | Click element by test tag |
| `GET /onNodeWithTag/{tag}/performTextInput?text=...` | Enter text into element |
| `GET /onNodeWithText/{text}/performClick` | Click element by display text |
| `GET /waitUntilExactlyOneExists/tag/{tag}?timeout=5000` | Wait for element by tag |
| `GET /waitUntilExactlyOneExists/text/{text}?exact=true&timeout=5000` | Wait for element by text |
| `GET /waitForIdle` | Wait for UI to become idle |
| `GET /captureScreenshot?path=/tmp/screenshot.png` | Capture screenshot to file |

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `COMPOSE_UI_TEST_SERVER_ENABLED` | Enable agent-controlled mode | `false` |
| `COMPOSE_UI_TEST_SERVER_PORT` | Server port | `54345` |

## Advanced Configuration

### Server Configuration

For more control over the test server, pass a server configuration:

```kotlin
import io.github.forketyfork.composeuittest.ComposeUiTestServerConfig
import io.github.forketyfork.composeuittest.WindowConfig
import io.github.forketyfork.composeuittest.runApplication

fun main() =
    runApplication(
        windowConfig = WindowConfig(title = "My App"),
        serverConfig = ComposeUiTestServerConfig(
            port = 8080,
            host = "0.0.0.0",
            defaultScreenshotPath = "/tmp/app_screenshot.png",
            defaultTimeout = 10_000L,
        ),
    ) {
        App()
    }
```

### Custom Endpoints

Add app-specific shortcuts for common agent workflows:

```kotlin
import io.github.forketyfork.composeuittest.TestEndpoint
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performClick
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.response.respondText

class LoginEndpoint(
    private val username: String,
    private val password: String
) : TestEndpoint {
    override fun Route.configure(composeTest: ComposeUiTest) {
        get("/shortcuts/login") {
            composeTest.onNodeWithTag("username").performTextInput(username)
            composeTest.onNodeWithTag("password").performTextInput(password)
            composeTest.onNodeWithTag("login_button").performClick()
            call.respondText("Login completed")
        }
    }
}
```

To use custom endpoints, use the lower-level API:

```kotlin
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.forketyfork.composeuittest.ComposeUiTestServer
import io.github.forketyfork.composeuittest.isTestServerEnabled

fun main() {
    if (isTestServerEnabled()) {
        runComposeUiTest {
            setContent { App() }
            ComposeUiTestServer(this)
                .registerEndpoint(LoginEndpoint("test@example.com", "password"))
                .start()
                .awaitTermination()
        }
    } else {
        application {
            Window(onCloseRequest = ::exitApplication, title = "My App") {
                App()
            }
        }
    }
}
```

## Platform Support

Currently supports **Desktop (JVM) only** due to Skia dependencies for screenshot capture.

## Claude Code Skill Installation

This library includes a skill that teaches Claude Code how to control Compose Desktop apps and set up new projects. Install it to enable automatic UI control capabilities.

### Personal Installation (all your projects)

```bash
mkdir -p ~/.claude/skills/compose-ui-control
curl -o ~/.claude/skills/compose-ui-control/SKILL.md \
  https://raw.githubusercontent.com/forketyfork/compose-ui-test-server/main/SKILL.md
```

### Project Installation (this project only)

```bash
mkdir -p .claude/skills/compose-ui-control
curl -o .claude/skills/compose-ui-control/SKILL.md \
  https://raw.githubusercontent.com/forketyfork/compose-ui-test-server/main/SKILL.md
```

### Using the Skill

Once installed, Claude Code can:

- **Automatically** use the skill when you ask it to interact with UI, click buttons, or test the app
- **Manually** invoke it with `/compose-ui-control`
- **Set up new projects** with agent control capabilities

Example prompts that trigger the skill:
- "Click the login button in the app"
- "Enter my username and password"
- "Take a screenshot of the current state"
- "Wait for the dashboard to appear"
- "Add agent control to this Compose Desktop project"

### Skill Reference

See [SKILL.md](SKILL.md) for the full skill documentation that Claude Code uses.

## License

MIT License - see [LICENSE](LICENSE) for details.

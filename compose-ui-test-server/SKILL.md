---
name: compose-ui-control
description: Control a running Compose Desktop application via HTTP. Use when you need to interact with UI elements, click buttons, enter text, wait for elements to appear, or capture screenshots in a Compose Desktop app that has compose-ui-test-server enabled.
argument-hint: "[action] [target]"
metadata:
  source: https://github.com/forketyfork/compose-ui-test-server/blob/main/SKILL.md
---

# compose-ui-test-server

A library that enables AI coding agents to control Compose Desktop applications at runtime via HTTP.

- **Repository**: https://github.com/forketyfork/compose-ui-test-server
- **Maven Central**: `io.github.forketyfork:compose-ui-test-server`
- **Current version**: 0.2.0

## Checking If Already Installed

Before setting up, check if the project already has the library:

```bash
grep -r "compose-ui-test-server\|composeuittest" --include="*.gradle*" --include="*.kt" .
```

Look for:
- Dependency on `io.github.forketyfork:compose-ui-test-server`
- Imports from `io.github.forketyfork.composeuittest`

If found, skip to [Starting the Application](#starting-the-application).

## Installing in a Compose Desktop Project

### Step 1: Add the dependency

Find the app's `build.gradle.kts` and locate the desktop source set dependencies. Add `compose-ui-test-server` and `compose.uiTest`:

```kotlin
kotlin {
    sourceSets {
        val desktopMain by getting {
            dependencies {
                // Existing dependencies...

                // Add these two:
                implementation("io.github.forketyfork:compose-ui-test-server:0.2.0")
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
            }
        }
    }
}
```

**For projects using version catalogs**, add to `gradle/libs.versions.toml`:

```toml
[libraries]
compose-ui-test-server = { module = "io.github.forketyfork:compose-ui-test-server", version = "0.2.0" }
```

Then reference in `build.gradle.kts`:

```kotlin
implementation(libs.compose.ui.test.server)
```

### Step 2: Update the main function

Find the application's `main()` function (usually in `Main.kt` or similar). Replace the standard Compose Desktop launcher with `runApplication`:

**Before** (typical Compose Desktop main):
```kotlin
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "My App") {
        App()
    }
}
```

**After** (with agent control support):
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

The app now runs normally by default, but supports agent control when launched with `COMPOSE_UI_TEST_SERVER_ENABLED=true`.

### Step 3: Add test tags to UI elements

For agents to interact with specific UI elements, add test tags:

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

## Starting the Application

```bash
# Normal mode (no server)
./gradlew run

# Agent-controlled mode (server enabled)
COMPOSE_UI_TEST_SERVER_ENABLED=true ./gradlew run

# With custom port (default is 54345)
COMPOSE_UI_TEST_SERVER_ENABLED=true COMPOSE_UI_TEST_SERVER_PORT=8080 ./gradlew run
```

## Verifying the Server

Always check health first:

```bash
curl http://localhost:54345/health
# Expected: "OK"
```

## Available Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /health` | Health check |
| `GET /onNodeWithTag/{tag}/performClick` | Click element by test tag |
| `GET /onNodeWithTag/{tag}/performTextInput?text=...` | Enter text (URL-encode the text!) |
| `GET /onNodeWithText/{text}/performClick` | Click element by display text |
| `GET /waitUntilExactlyOneExists/tag/{tag}?timeout=5000` | Wait for element by tag |
| `GET /waitUntilExactlyOneExists/text/{text}?exact=true&timeout=5000` | Wait for element by text |
| `GET /waitForIdle` | Wait for UI to stabilize |
| `GET /captureScreenshot?path=/tmp/screenshot.png` | Capture screenshot |

## Workflow Pattern

Follow this sequence for reliable interactions:

```bash
# 1. Verify server is running
curl http://localhost:54345/health

# 2. Wait for UI to be ready
curl http://localhost:54345/waitForIdle

# 3. Perform action
curl "http://localhost:54345/onNodeWithTag/username/performTextInput?text=myuser"

# 4. Wait for UI to settle
curl http://localhost:54345/waitForIdle

# 5. Perform next action
curl http://localhost:54345/onNodeWithTag/login_button/performClick

# 6. Wait for result
curl "http://localhost:54345/waitUntilExactlyOneExists/tag/dashboard?timeout=10000"

# 7. Capture screenshot to verify
curl "http://localhost:54345/captureScreenshot?path=/tmp/result.png"
```

## Finding Test Tags

Search the codebase for existing test tags:

```bash
grep -r "testTag\|Modifier.testTag" --include="*.kt" .
```

Also check:
- `CLAUDE.md` for documented test tags
- Test files in `src/*Test/` directories

## Important Notes

- **Always URL-encode** special characters in text: space→`%20`, `@`→`%40`, `&`→`%26`
- **Use `waitForIdle`** between operations for stability
- **Check HTTP status codes**: 200=success, 400=bad request, 500=error
- **Use appropriate timeouts** for waits (default 5000ms may be too short)
- Screenshots require absolute paths

## Error Handling

If an endpoint returns an error:
1. Check the element exists (search for its test tag in code)
2. Ensure UI has finished loading (`waitForIdle`)
3. Verify the server is still running (`/health`)
4. Try with a longer timeout for wait operations

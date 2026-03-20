package io.github.forketyfork.composeuittest

import androidx.compose.ui.test.ComposeUiTest

/**
 * Environment variable to enable the UI test server.
 * Set to "true" to enable: `COMPOSE_UI_TEST_SERVER_ENABLED=true`
 */
const val ENV_SERVER_ENABLED = "COMPOSE_UI_TEST_SERVER_ENABLED"

/**
 * Environment variable to configure the server port.
 * Example: `COMPOSE_UI_TEST_SERVER_PORT=8080`
 */
const val ENV_SERVER_PORT = "COMPOSE_UI_TEST_SERVER_PORT"

/**
 * Check if the UI test server is enabled via environment variable.
 *
 * @return true if [ENV_SERVER_ENABLED] is set to "true"
 */
fun isTestServerEnabled(): Boolean = System.getenv(ENV_SERVER_ENABLED)?.toBoolean() ?: false

/**
 * Get the configured server port from environment variable.
 *
 * @param default The default port if not configured
 * @return The port from [ENV_SERVER_PORT] or the default
 */
fun getTestServerPort(default: Int = 54345): Int = System.getenv(ENV_SERVER_PORT)?.toIntOrNull() ?: default

/**
 * Start a UI test server for this Compose UI test.
 *
 * This creates and starts a [ComposeUiTestServer] that enables coding agents
 * and automation tools to control the running application via HTTP.
 *
 * Example usage:
 * ```kotlin
 * runComposeUiTest {
 *     setContent { MyApp() }
 *
 *     val server = startTestServer(
 *         config = ComposeUiTestServerConfig(port = 8080)
 *     )
 *
 *     server.awaitTermination()
 * }
 * ```
 *
 * @param config Server configuration (uses defaults if not specified)
 * @return The started [ComposeUiTestServer] instance
 */
fun ComposeUiTest.startTestServer(
    config: ComposeUiTestServerConfig = ComposeUiTestServerConfig(),
): ComposeUiTestServer = ComposeUiTestServer(this, config).start()

/**
 * Conditionally start a UI test server based on environment variable.
 *
 * The server is only started if the environment variable [ENV_SERVER_ENABLED]
 * is set to "true". This allows the same application binary to run normally
 * or in agent-controlled mode based on environment.
 *
 * The port can be configured via [ENV_SERVER_PORT] environment variable,
 * or falls back to the config value.
 *
 * Example usage in your app:
 * ```kotlin
 * runComposeUiTest {
 *     setContent { MyApp() }
 *
 *     // Server only starts if COMPOSE_UI_TEST_SERVER_ENABLED=true
 *     val server = startTestServerIfEnabled()
 *
 *     // Continue with normal app flow...
 *     // If server was started, agents can control the app via HTTP
 * }
 * ```
 *
 * Run with agent control enabled:
 * ```bash
 * COMPOSE_UI_TEST_SERVER_ENABLED=true COMPOSE_UI_TEST_SERVER_PORT=8080 java -jar myapp.jar
 * ```
 *
 * @param config Base server configuration (port may be overridden by environment variable)
 * @return The started [ComposeUiTestServer] if enabled, null otherwise
 */
fun ComposeUiTest.startTestServerIfEnabled(
    config: ComposeUiTestServerConfig = ComposeUiTestServerConfig(),
): ComposeUiTestServer? {
    if (!isTestServerEnabled()) {
        return null
    }
    val port = getTestServerPort(config.port)
    val effectiveConfig = if (port != config.port) config.copy(port = port) else config
    return ComposeUiTestServer(this, effectiveConfig).start()
}

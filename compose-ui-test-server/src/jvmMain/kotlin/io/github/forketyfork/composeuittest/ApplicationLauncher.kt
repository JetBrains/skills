@file:Suppress("MatchingDeclarationName")

package io.github.forketyfork.composeuittest

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.Dimension

/**
 * Configuration for the application window in normal (non-test) mode.
 *
 * @property title Window title
 * @property minimumWidth Minimum window width in pixels (optional)
 * @property minimumHeight Minimum window height in pixels (optional)
 */
data class WindowConfig(
    val title: String = "Application",
    val minimumWidth: Int? = null,
    val minimumHeight: Int? = null,
)

/**
 * Runs a Compose Desktop application with automatic test server support.
 *
 * When `COMPOSE_UI_TEST_SERVER_ENABLED=true` environment variable is set,
 * the application runs in test mode with an HTTP server that allows
 * coding agents to control the UI.
 *
 * When disabled (default), the application runs normally in a desktop window.
 *
 * Example usage:
 * ```kotlin
 * fun main() = runApplication(
 *     windowConfig = WindowConfig(
 *         title = "My App",
 *         minimumWidth = 1024,
 *         minimumHeight = 768
 *     )
 * ) {
 *     App()
 * }
 * ```
 *
 * Run normally:
 * ```bash
 * ./gradlew run
 * ```
 *
 * Run with agent control enabled:
 * ```bash
 * COMPOSE_UI_TEST_SERVER_ENABLED=true ./gradlew run
 * ```
 *
 * @param windowConfig Configuration for the window in normal mode
 * @param serverConfig Configuration for the test server in agent-controlled mode
 * @param content The composable content to display
 */
@OptIn(ExperimentalTestApi::class)
fun runApplication(
    windowConfig: WindowConfig = WindowConfig(),
    serverConfig: ComposeUiTestServerConfig = ComposeUiTestServerConfig(),
    content: @Composable () -> Unit,
) {
    if (isTestServerEnabled()) {
        runComposeUiTest {
            setContent { content() }
            val port = getTestServerPort(serverConfig.port)
            val effectiveConfig = if (port != serverConfig.port) serverConfig.copy(port = port) else serverConfig
            ComposeUiTestServer(this, effectiveConfig).start().awaitTermination()
        }
    } else {
        application {
            Window(
                onCloseRequest = ::exitApplication,
                title = windowConfig.title,
            ) {
                if (windowConfig.minimumWidth != null && windowConfig.minimumHeight != null) {
                    window.minimumSize = Dimension(windowConfig.minimumWidth, windowConfig.minimumHeight)
                }
                content()
            }
        }
    }
}

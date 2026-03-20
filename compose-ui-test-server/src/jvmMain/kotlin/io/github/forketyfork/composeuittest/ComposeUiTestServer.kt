package io.github.forketyfork.composeuittest

import androidx.compose.ui.test.ComposeUiTest
import io.github.forketyfork.composeuittest.endpoint.ClickEndpoints
import io.github.forketyfork.composeuittest.endpoint.HealthEndpoint
import io.github.forketyfork.composeuittest.endpoint.ScreenshotEndpoint
import io.github.forketyfork.composeuittest.endpoint.TextInputEndpoint
import io.github.forketyfork.composeuittest.endpoint.WaitEndpoints
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

/**
 * HTTP server that exposes Compose UI testing operations via REST API.
 *
 * This allows interactive control of a Compose application through HTTP requests,
 * enabling scripted UI interactions, screenshot capture, and automated testing scenarios.
 *
 * Example usage:
 * ```kotlin
 * runComposeUiTest {
 *     setContent { MyApp() }
 *
 *     val server = ComposeUiTestServer(this).start()
 *
 *     // Server is now running and can be controlled via HTTP
 *     // curl http://localhost:54345/health
 *     // curl http://localhost:54345/onNodeWithTag/button/performClick
 *
 *     server.awaitTermination()
 * }
 * ```
 *
 * @property composeTest The Compose UI test instance to control
 * @property config Server configuration
 */
class ComposeUiTestServer(
    private val composeTest: ComposeUiTest,
    private val config: ComposeUiTestServerConfig = ComposeUiTestServerConfig(),
) {
    private val customEndpoints = mutableListOf<TestEndpoint>()
    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

    /**
     * Register a custom endpoint.
     *
     * @param endpoint The endpoint to register
     * @return This server instance for chaining
     */
    fun registerEndpoint(endpoint: TestEndpoint): ComposeUiTestServer {
        customEndpoints.add(endpoint)
        return this
    }

    /**
     * Start the HTTP server.
     *
     * @return This server instance for chaining
     */
    fun start(): ComposeUiTestServer {
        server =
            embeddedServer(Netty, port = config.port, host = config.host) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                        },
                    )
                }

                routing {
                    if (config.enableHealthEndpoint) {
                        with(HealthEndpoint()) { configure(composeTest) }
                    }
                    if (config.enableClickEndpoints) {
                        with(ClickEndpoints()) { configure(composeTest) }
                    }
                    if (config.enableTextInputEndpoints) {
                        with(TextInputEndpoint()) { configure(composeTest) }
                    }
                    if (config.enableWaitEndpoints) {
                        with(WaitEndpoints(config)) { configure(composeTest) }
                    }
                    if (config.enableScreenshotEndpoint) {
                        with(ScreenshotEndpoint(config)) { configure(composeTest) }
                    }

                    for (endpoint in customEndpoints) {
                        with(endpoint) { configure(composeTest) }
                    }
                }
            }.start(wait = false)

        println("Compose UI test server started on http://${config.host}:${config.port}")
        println("Try: curl http://localhost:${config.port}/health")

        return this
    }

    /**
     * Stop the HTTP server.
     *
     * @param gracePeriodMillis Grace period for active requests to complete
     * @param timeoutMillis Maximum time to wait for shutdown
     */
    fun stop(
        gracePeriodMillis: Long = 1000,
        timeoutMillis: Long = 2000,
    ) {
        server?.stop(gracePeriodMillis, timeoutMillis)
        server = null
    }

    /**
     * Block until the server is stopped.
     *
     * This is useful for keeping the test running indefinitely while
     * the server accepts HTTP requests.
     */
    fun awaitTermination() {
        try {
            Thread.currentThread().join()
        } catch (e: InterruptedException) {
            stop()
        }
    }
}

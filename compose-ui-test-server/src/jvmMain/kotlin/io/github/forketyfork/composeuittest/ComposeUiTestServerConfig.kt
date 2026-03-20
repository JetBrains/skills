package io.github.forketyfork.composeuittest

/**
 * Configuration for the Compose UI test server.
 *
 * @property port The port to listen on (default: 54345)
 * @property host The host to bind to (default: "0.0.0.0")
 * @property defaultScreenshotPath Default path for screenshot output
 * @property defaultTimeout Default timeout for wait operations in milliseconds
 * @property enableHealthEndpoint Whether to enable the /health endpoint
 * @property enableClickEndpoints Whether to enable click-related endpoints
 * @property enableTextInputEndpoints Whether to enable text input endpoints
 * @property enableWaitEndpoints Whether to enable wait-related endpoints
 * @property enableScreenshotEndpoint Whether to enable the screenshot endpoint
 */
data class ComposeUiTestServerConfig(
    val port: Int = 54345,
    val host: String = "0.0.0.0",
    val defaultScreenshotPath: String = "/tmp/compose_screenshot.png",
    val defaultTimeout: Long = 5000L,
    val enableHealthEndpoint: Boolean = true,
    val enableClickEndpoints: Boolean = true,
    val enableTextInputEndpoints: Boolean = true,
    val enableWaitEndpoints: Boolean = true,
    val enableScreenshotEndpoint: Boolean = true,
)

package io.github.forketyfork.composeuittest

import androidx.compose.ui.test.ComposeUiTest
import io.ktor.server.routing.Route

/**
 * Interface for custom test endpoints.
 *
 * Implement this interface to add custom endpoints to the test server.
 * Custom endpoints can provide app-specific shortcuts like login helpers
 * or test data setup.
 *
 * Example:
 * ```kotlin
 * class LoginEndpoint(
 *     private val username: String,
 *     private val password: String
 * ) : TestEndpoint {
 *     override fun Route.configure(composeTest: ComposeUiTest) {
 *         get("/shortcuts/login") {
 *             composeTest.onNodeWithTag("username").performTextInput(username)
 *             composeTest.onNodeWithTag("password").performTextInput(password)
 *             composeTest.onNodeWithTag("login_button").performClick()
 *             call.respondText("Login completed")
 *         }
 *     }
 * }
 * ```
 */
interface TestEndpoint {
    /**
     * Configure routes for this endpoint.
     *
     * @receiver The Ktor [Route] to add endpoints to
     * @param composeTest The [ComposeUiTest] instance for UI operations
     */
    fun Route.configure(composeTest: ComposeUiTest)
}

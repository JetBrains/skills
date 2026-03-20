@file:Suppress("TooGenericExceptionCaught")

package io.github.forketyfork.composeuittest.endpoint

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.waitUntilExactlyOneExists
import io.github.forketyfork.composeuittest.ComposeUiTestServerConfig
import io.github.forketyfork.composeuittest.TestEndpoint
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

/**
 * Wait operation endpoints.
 *
 * GET /waitUntilExactlyOneExists/tag/{tag}?timeout=5000 - Wait for element by test tag
 * GET /waitUntilExactlyOneExists/text/{text}?exact=true&timeout=5000 - Wait for element by text
 * GET /waitForIdle - Wait for UI to become idle
 */
internal class WaitEndpoints(
    private val config: ComposeUiTestServerConfig,
) : TestEndpoint {
    override fun Route.configure(composeTest: ComposeUiTest) {
        get("/waitUntilExactlyOneExists/tag/{tag}") {
            val tag =
                call.parameters["tag"]
                    ?: return@get call.respondText(
                        "Missing tag parameter",
                        status = HttpStatusCode.BadRequest,
                    )
            val timeout =
                call.request.queryParameters["timeout"]?.toLongOrNull()
                    ?: config.defaultTimeout

            try {
                composeTest.waitUntilExactlyOneExists(hasTestTag(tag), timeoutMillis = timeout)
                call.respondText("Node with tag '$tag' exists", status = HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }

        get("/waitUntilExactlyOneExists/text/{text}") {
            val text =
                call.parameters["text"]
                    ?: return@get call.respondText(
                        "Missing text parameter",
                        status = HttpStatusCode.BadRequest,
                    )
            val timeout =
                call.request.queryParameters["timeout"]?.toLongOrNull()
                    ?: config.defaultTimeout
            val exact = call.request.queryParameters["exact"]?.toBoolean() ?: true

            try {
                if (exact) {
                    composeTest.waitUntilExactlyOneExists(hasTextExactly(text), timeoutMillis = timeout)
                } else {
                    composeTest.waitUntilExactlyOneExists(hasText(text), timeoutMillis = timeout)
                }
                call.respondText("Node with text '$text' exists", status = HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }

        get("/waitForIdle") {
            try {
                composeTest.waitForIdle()
                call.respondText("Waited for idle", status = HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}

@file:Suppress("TooGenericExceptionCaught")

package io.github.forketyfork.composeuittest.endpoint

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import io.github.forketyfork.composeuittest.TestEndpoint
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

/**
 * Text input endpoint.
 *
 * GET /onNodeWithTag/{tag}/performTextInput?text=... - Enter text into element by test tag
 */
internal class TextInputEndpoint : TestEndpoint {
    override fun Route.configure(composeTest: ComposeUiTest) {
        get("/onNodeWithTag/{tag}/performTextInput") {
            val tag =
                call.parameters["tag"]
                    ?: return@get call.respondText(
                        "Missing tag parameter",
                        status = HttpStatusCode.BadRequest,
                    )
            val text =
                call.request.queryParameters["text"]
                    ?: return@get call.respondText(
                        "Missing text parameter",
                        status = HttpStatusCode.BadRequest,
                    )

            try {
                composeTest.onNodeWithTag(tag).performTextInput(text)
                call.respondText(
                    "Entered text '$text' into node with tag: $tag",
                    status = HttpStatusCode.OK,
                )
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}

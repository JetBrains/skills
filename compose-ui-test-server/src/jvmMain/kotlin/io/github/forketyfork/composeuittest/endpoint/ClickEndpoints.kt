@file:Suppress("TooGenericExceptionCaught")

package io.github.forketyfork.composeuittest.endpoint

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.forketyfork.composeuittest.TestEndpoint
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

/**
 * Click operation endpoints.
 *
 * GET /onNodeWithTag/{tag}/performClick - Click element by test tag
 * GET /onNodeWithText/{text}/performClick - Click element by display text
 */
internal class ClickEndpoints : TestEndpoint {
    override fun Route.configure(composeTest: ComposeUiTest) {
        get("/onNodeWithTag/{tag}/performClick") {
            val tag =
                call.parameters["tag"]
                    ?: return@get call.respondText(
                        "Missing tag parameter",
                        status = HttpStatusCode.BadRequest,
                    )

            try {
                composeTest.onNodeWithTag(tag).performClick()
                call.respondText("Clicked node with tag: $tag", status = HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }

        get("/onNodeWithText/{text}/performClick") {
            val text =
                call.parameters["text"]
                    ?: return@get call.respondText(
                        "Missing text parameter",
                        status = HttpStatusCode.BadRequest,
                    )

            try {
                composeTest.onNodeWithText(text).performClick()
                call.respondText("Clicked node with text: $text", status = HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}

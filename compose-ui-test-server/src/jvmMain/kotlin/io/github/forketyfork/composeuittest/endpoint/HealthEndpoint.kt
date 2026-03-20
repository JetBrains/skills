package io.github.forketyfork.composeuittest.endpoint

import androidx.compose.ui.test.ComposeUiTest
import io.github.forketyfork.composeuittest.TestEndpoint
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

/**
 * Health check endpoint.
 *
 * GET /health - Returns "OK" with 200 status code
 */
internal class HealthEndpoint : TestEndpoint {
    override fun Route.configure(composeTest: ComposeUiTest) {
        get("/health") {
            call.respondText("OK", status = HttpStatusCode.OK)
        }
    }
}

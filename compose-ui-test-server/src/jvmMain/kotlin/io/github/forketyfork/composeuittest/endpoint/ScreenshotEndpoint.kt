@file:Suppress("TooGenericExceptionCaught")

package io.github.forketyfork.composeuittest.endpoint

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import io.github.forketyfork.composeuittest.ComposeUiTestServerConfig
import io.github.forketyfork.composeuittest.TestEndpoint
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import java.io.File

/**
 * Screenshot capture endpoint.
 *
 * GET /captureScreenshot?path=/tmp/screenshot.png - Capture and save screenshot
 */
internal class ScreenshotEndpoint(
    private val config: ComposeUiTestServerConfig,
) : TestEndpoint {
    override fun Route.configure(composeTest: ComposeUiTest) {
        get("/captureScreenshot") {
            val outputPath =
                call.request.queryParameters["path"]
                    ?: config.defaultScreenshotPath

            try {
                val screenshot = composeTest.onRoot().captureToImage()
                saveScreenshot(screenshot, outputPath)
                call.respondText("Screenshot saved to: $outputPath", status = HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }

    private fun saveScreenshot(
        imageBitmap: ImageBitmap,
        outputPath: String,
    ) {
        val skiaBitmap = imageBitmap.asSkiaBitmap()
        val image = Image.makeFromBitmap(skiaBitmap)
        val data =
            image.encodeToData(EncodedImageFormat.PNG)
                ?: error("Failed to encode image to PNG")
        File(outputPath).writeBytes(data.bytes)
    }
}

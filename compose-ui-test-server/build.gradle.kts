import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.mavenPublish)
}

group = "io.github.forketyfork"
version = "0.2.0"

kotlin {
    jvm()

    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.ui.InternalComposeUiApi",
            "-opt-in=androidx.compose.ui.test.ExperimentalTestApi",
        )
    }

    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    sourceSets {
        jvmMain.dependencies {
            compileOnly(compose.uiTest)
            compileOnly(compose.desktop.common)

            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.netty)
            implementation(libs.ktor.server.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation(compose.uiTest)
            implementation(compose.desktop.currentOs)
        }
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(group.toString(), "compose-ui-test-server", version.toString())

    pom {
        name.set("Compose UI Test Server")
        description.set("HTTP server for controlling Compose Desktop applications at runtime, designed for AI coding agents and automation tools")
        url.set("https://github.com/forketyfork/compose-ui-test-server")
        inceptionYear.set("2025")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("forketyfork")
                name.set("Sergei Petunin")
                email.set("sergei.petunin@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/forketyfork/compose-ui-test-server")
            connection.set("scm:git:git://github.com/forketyfork/compose-ui-test-server.git")
            developerConnection.set("scm:git:ssh://github.com/forketyfork/compose-ui-test-server.git")
        }
    }
}

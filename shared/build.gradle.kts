
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    android {
        namespace = "com.apps.stackoverflow.shared"

        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }

        androidResources {
            enable = true
        }

        // Enable local JVM tests for Android
        withHostTest {
            isIncludeAndroidResources = true
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner =
                "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {

        commonMain.dependencies {
            // Compose Multiplatform
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)

            // Lifecycle
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Kotlin Multiplatform utilities
            implementation(libs.kotlinx.datetime)

            // Navigation
            implementation(libs.navigation.compose)

            // Coil 3
            implementation(libs.coil.compose)
            implementation(libs.coil3.coil.network.ktor3)

            // Koin 4
            implementation(libs.koin.core.v404)
            implementation(libs.insert.koin.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.core.viewmodel)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)
        }

        androidMain.dependencies {
            // Android Compose tooling
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)

            // Android networking
            implementation(libs.ktor.client.okhttp)

            // Android-specific Koin integration
            implementation(libs.koin.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        // Android local JVM unit tests
        getByName("androidHostTest").dependencies {
            implementation(libs.kotlin.test)

            implementation(libs.kotlinx.coroutines.test)

            implementation(libs.mockk)

            implementation(libs.junit.jupiter.api)

            implementation(libs.junit.jupiter.engine)
        }

        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.testRunner)
        }
    }
}

compose.resources {
    publicResClass = true

    packageOfResClass =
        "com.apps.stackoverflow.shared.resources"

    generateResClass = always
}

// Enable JUnit 5 for JVM test tasks
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

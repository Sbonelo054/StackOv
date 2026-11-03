
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
            implementation("io.coil-kt.coil3:coil-compose:3.3.0")
            implementation("io.coil-kt.coil3:coil-network-ktor3:3.3.0")

            // Koin 4
            implementation("io.insert-koin:koin-core:4.0.4")
            implementation("io.insert-koin:koin-compose:4.0.4")
            implementation("io.insert-koin:koin-compose-viewmodel:4.0.4")
            implementation("io.insert-koin:koin-core-viewmodel:4.0.4")

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

            // Keep Retrofit only if Android-specific code uses it
            implementation(libs.retrofit.core)
            implementation(libs.retrofit.converter.gson)
            implementation(libs.okhttp.logging)

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

            implementation(
                "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2"
            )

            implementation("io.mockk:mockk:1.14.9")

            implementation(
                "org.junit.jupiter:junit-jupiter-api:5.11.4"
            )

            implementation(
                "org.junit.jupiter:junit-jupiter-engine:5.11.4"
            )
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

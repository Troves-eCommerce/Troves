plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {

    // Target declarations
    android {
        namespace = "com.troves.presintation"
        compileSdk = 34 // تم التوحيد لتجنب أخطاء الـ SDK
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // iOS targets
    val xcfName = "presintationKit"

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    // Source set declarations
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization)
                implementation(libs.androidx.navigation3.runtime)
                implementation(libs.androidx.navigation3.ui)
                implementation(libs.jetbrains.lifecycle.viewmodelNavigation3)
                implementation(libs.jetbrains.material3.adaptiveNavigation3)

                implementation(project(":domain"))
                implementation(compose.components.resources)
                api(project(":designSystem"))

                // Compose & Resources
                implementation(libs.compose.ui)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.runtime)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)

                // ViewModel
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                // Koin
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.koin.compose.navigation3)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here.
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
                implementation(libs.androidx.runner)
            }
        }
    }
}
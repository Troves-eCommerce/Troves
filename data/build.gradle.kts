@file:OptIn(ApolloExperimental::class)

import com.apollographql.apollo.annotations.ApolloExperimental
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

// ── Load credentials from local.properties (never committed to VCS) ──────────
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.apollo)

}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    android {
        namespace = "com.troves.data"
        compileSdk = 36
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "DataKit"


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

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.kotlinx.serialization)
                implementation(libs.androidx.sqlite.bundled)
                implementation(libs.androidx.room.runtime)
                implementation(project(":domain"))

                // Add KMP dependencies here
                implementation(libs.koin.core)
                implementation(libs.bundles.ktor)

                implementation(libs.androidx.datastore)
                // The Preferences DataStore library
                implementation(libs.androidx.datastore.preferences)

                // Firebase (GitLive KMP SDK — works on both Android & iOS)
                implementation(libs.firebase.auth)
                implementation(libs.firebase.firestore)

                // Apollo
                implementation(libs.apollo.runtime)
                // Memory Cache
                implementation(libs.apollo.normalized.cache)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.client.android)
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.androidx.sqlite.bundled)
                implementation(libs.androidx.room.runtime)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
                implementation(libs.androidx.runner)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
                implementation(libs.androidx.sqlite.bundled)
                implementation(libs.androidx.room.runtime)
            }
        }
    }

}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

buildkonfig {
    packageName = "com.troves.data"

    defaultConfigs {
        buildConfigField(
            STRING, "SHOPIFY_API_KEY",
            localProperties.getProperty("SHOPIFY_API_KEY") ?: error("SHOPIFY_API_KEY not set in local.properties")
        )
        buildConfigField(
            STRING, "SHOPIFY_REST_URL",
            localProperties.getProperty("SHOPIFY_REST_URL") ?: error("SHOPIFY_HOSTNAME not set in local.properties")
        )
    }
}
apollo {
    service(name = "admin") {
        packageName.set("com.troves.data.source.remote.service.apollo.graphql.admin")
        srcDir(file("src/commonMain/graphql/admin"))
        generateDataBuilders.set(true)
        val apiKey = localProperties.getProperty("SHOPIFY_API_KEY") ?: ""
        val url = localProperties.getProperty("SHOPIFY_REST_URL") ?: ""


        introspection {
            endpointUrl.set("${url}graphql.json")

            schemaFile.set(file("src/commonMain/graphql/admin/admin.graphqls"))

            headers.put("X-Shopify-Access-Token", apiKey)
        }
    }
}
apollo {
    service(name = "storefront") {
        packageName.set("com.troves.data.source.remote.service.apollo.graphql.storefront")
        srcDir(file("src/commonMain/graphql/storefront"))
        generateDataBuilders.set(true)
        val apiKey = localProperties.getProperty("SHOPIFY_STOREFRONT_ACCESS_TOKEN") ?: ""
        val url = localProperties.getProperty("SHOPIFY_STOREFRONT_URL") ?: ""


        introspection {
            endpointUrl.set("${url}graphql.json")

            schemaFile.set(file("src/commonMain/graphql/storefront/storefront.graphqls"))

            headers.put("X-Shopify-Storefront-Access-Token", apiKey)
        }
    }
}
rootProject.name = "Troves"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("org.chromium")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("org.chromium")
            }
        }
        maven {
            url = rootProject.projectDir.toURI().resolve("androidApp/libs/PaymobAndroidSDK1.9.2/")
        }
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
        }
        
        maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        // GitLive Firebase KMP SDK
        maven(url = "https://jitpack.io")
    }
}

include(":androidApp")
include(":shared")
include(":data")
include(":domain")
include(":presintation")
include(":designSystem")

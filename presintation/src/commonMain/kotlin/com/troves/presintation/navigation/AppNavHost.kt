package com.troves.presintation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.troves.presintation.ui.home.HomeScreen
import com.troves.presintation.ui.productDetails.ProductDetailsScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


private val navSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(AppRoute.Splash::class, AppRoute.Splash.serializer())
            subclass(AppRoute.Onboarding::class, AppRoute.Onboarding.serializer())
            subclass(AppRoute.Login::class, AppRoute.Login.serializer())
            subclass(AppRoute.Register::class, AppRoute.Register.serializer())
            subclass(AppRoute.Home::class, AppRoute.Home.serializer())
            subclass(AppRoute.ProductDetails::class, AppRoute.ProductDetails.serializer())
        }
    }
}

@Composable
fun AppNavHost() {
    val backStack = rememberNavBackStack(navSavedStateConfiguration, AppRoute.Home)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AppRoute.Home> {
                HomeScreen(
                    onNavigateToProduct = { productId ->
                        backStack.add(AppRoute.ProductDetails(productId))
                    }
                )
            }

            entry<AppRoute.ProductDetails> { key ->
                ProductDetailsScreen(
                    productId = key.productId,
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}

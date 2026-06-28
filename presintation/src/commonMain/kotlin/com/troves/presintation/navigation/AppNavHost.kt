package com.troves.presintation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.troves.presintation.ui.Auth.LoginScreen
import com.troves.presintation.ui.Auth.RegisterScreen
import com.troves.presintation.ui.Home.HomeScreen
import com.troves.presintation.ui.Home.ProductDetailsScreen
import com.troves.presintation.ui.Splash.SplashScreen
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
    val backStack = rememberNavBackStack(navSavedStateConfiguration, AppRoute.Splash)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {

            entry<AppRoute.Splash> {
                SplashScreen(
                    onNavigateToHome = {
                        backStack.clear()
                        backStack.add(AppRoute.Home)
                    },
                    onNavigateToLogin = {
                        backStack.clear()
                        backStack.add(AppRoute.Login)
                    }
                )
            }

            entry<AppRoute.Login> {
                LoginScreen(
                    onNavigateToRegister = {
                        backStack.add(AppRoute.Register)
                    },
                    onLoginSuccess = {
                        backStack.clear()
                        backStack.add(AppRoute.Home)
                    }
                )
            }

            entry<AppRoute.Register> {
                RegisterScreen(
                    onNavigateToLogin = {
                        backStack.removeLastOrNull()
                    },
                    onRegisterSuccess = {
                        backStack.clear()
                        backStack.add(AppRoute.Home)
                    }
                )
            }

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

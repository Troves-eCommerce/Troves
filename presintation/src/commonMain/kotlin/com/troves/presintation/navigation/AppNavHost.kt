package com.troves.presintation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.troves.presintation.ui.Home.HomeScreen
import com.troves.presintation.ui.auth.LoginScreen
import com.troves.presintation.ui.auth.RegisterScreen
import com.troves.presintation.ui.fav.FavoriteScreen
import com.troves.presintation.ui.home.ProductDetailsScreen
import com.troves.presintation.ui.onboarding.OnboardingScreen
import com.troves.presintation.ui.profile.ProfileScreen
import com.troves.presintation.ui.splash.SplashScreen
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
            subclass(AppRoute.Products::class, AppRoute.Products.serializer())
            subclass(AppRoute.Favorites::class, AppRoute.Favorites.serializer())
            subclass(AppRoute.Profile::class, AppRoute.Profile.serializer())
            subclass(AppRoute.ProductDetails::class, AppRoute.ProductDetails.serializer())
        }
    }
}

@Composable
fun AppNavHost() {
    val backStack = rememberNavBackStack(navSavedStateConfiguration, AppRoute.Splash)

    val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
        entry<AppRoute.Home> {
            HomeScreen(
                onNavigateToProduct = { productId ->
                    backStack.add(AppRoute.ProductDetails(productId))
                }
            )
        }
        
        entry<AppRoute.Products> {
            ProductDetailsScreen(
                productId = "123",
                onNavigateBack = { backStack.removeLastOrNull() }
            )
        }
        
        entry<AppRoute.Favorites> {
            FavoriteScreen()
        }
        
        entry<AppRoute.Profile> {
            ProfileScreen()
        }

        entry<AppRoute.ProductDetails> { key ->
            ProductDetailsScreen(
                productId = key.productId,
                onNavigateBack = { backStack.removeLastOrNull() }
            )
        }
        entry<AppRoute.Onboarding> {
            OnboardingScreen(
                onNavigateToLogin = {
                    backStack.removeLastOrNull()
                    backStack.add(AppRoute.Home)
                }
            )
        }
        entry<AppRoute.Splash> {
            SplashScreen(
                onNavigateToOnboarding = {
                    backStack.removeLastOrNull()
                    backStack.add(AppRoute.Onboarding)
                }
            )
        }
        entry<AppRoute.Login> {
            LoginScreen()
        }
        entry<AppRoute.Register> {
            RegisterScreen()
        }
    }

    NavDisplay<NavKey>(
        entries = rememberDecoratedNavEntries(
            backStack = backStack,
            entryProvider = entryProvider
        ),
        onBack = { backStack.removeLastOrNull() }
    )
}

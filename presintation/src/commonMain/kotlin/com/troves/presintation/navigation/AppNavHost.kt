package com.troves.presintation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.troves.presintation.ui.MainViewModel
import com.troves.presintation.ui.StartDestination
import com.troves.presintation.ui.auth.LoginScreen
import com.troves.presintation.ui.auth.RegisterScreen
import com.troves.presintation.ui.fav.FavoriteScreen
import com.troves.presintation.ui.home.HomeScreen
import com.troves.presintation.ui.onboarding.OnboardingScreen
import com.troves.presintation.ui.productDetails.ProductDetailsScreen
import com.troves.presintation.ui.products.ProductsScreen
import com.troves.presintation.ui.profile.ProfileScreen
import com.troves.presintation.ui.splash.SplashScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

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
    val mainViewModel: MainViewModel = koinViewModel()
    val uiState by mainViewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        SplashScreen(onNavigateToOnboarding = {})
        return
    }

    val initialRoute: NavKey = when (uiState.startDestination) {
        StartDestination.Onboarding -> AppRoute.Onboarding
        StartDestination.Login -> AppRoute.Login
        StartDestination.Home -> AppRoute.Home
    }

    val backStack = rememberNavBackStack(navSavedStateConfiguration, initialRoute)


    fun replaceWith(route: NavKey) {
        Snapshot.withMutableSnapshot {
            backStack.clear()
            backStack.add(route)
        }
    }

    val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
        entry<AppRoute.Home> {
            HomeScreen(
                onNavigateToProduct = { productId ->
                    backStack.add(AppRoute.ProductDetails(productId))
                }
            )
        }
        entry<AppRoute.Favorites> {
            FavoriteScreen()
        }
        entry<AppRoute.ProductDetails> { key ->
            ProductDetailsScreen(
                productId = key.productId,
                onNavigateBack = { backStack.removeLastOrNull() }
            )
        }
        entry<AppRoute.Onboarding> {
            OnboardingScreen(
                onOnboardingComplete = { replaceWith(AppRoute.Login) }
            )
        }
        entry<AppRoute.Splash> {
            SplashScreen(
                onNavigateToOnboarding = { replaceWith(AppRoute.Onboarding) }
            )
        }
        entry<AppRoute.Login> {
            LoginScreen(
                onNavigateToRegister = { backStack.add(AppRoute.Register) },
                onLoginSuccess = { replaceWith(AppRoute.Home) }
            )
        }
        entry<AppRoute.Register> {
            RegisterScreen(
                onNavigateToLogin = { backStack.removeLastOrNull() },
                onRegisterSuccess = { replaceWith(AppRoute.Home) }
            )
        }
        entry<AppRoute.Products> {
            ProductsScreen()
        }
        entry<AppRoute.Profile> {
            ProfileScreen()
        }
    }

    NavDisplay<NavKey>(
        entries = rememberDecoratedNavEntries(
            backStack = backStack,
            entryProvider = entryProvider
        ),
        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() }
    )
}

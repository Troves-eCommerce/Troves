package com.troves.presintation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey {
    @Serializable
    data object Splash : AppRoute

    @Serializable
    data object Onboarding : AppRoute

    @Serializable
    data object Login : AppRoute

    @Serializable
    data object Register : AppRoute

    @Serializable
    data object Home : AppRoute

    @Serializable
    data object Products : AppRoute

    @Serializable
    data object Favorites : AppRoute
    @Serializable
    data object Search : AppRoute

    @Serializable
    data object Profile : AppRoute

    @Serializable
    data class ProductDetails(val productId: String) : AppRoute
}

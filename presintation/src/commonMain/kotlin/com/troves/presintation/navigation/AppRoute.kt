package com.troves.presintation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey {
    @Serializable
    data object Splash : AppRoute

    @Serializable
    data object Onboarding : AppRoute

    @Serializable
    data object Checkout : AppRoute

    @Serializable
    data object Login : AppRoute

    @Serializable
    data object Register : AppRoute

    @Serializable
    data object Home : AppRoute

    @Serializable
    data class Products(
        val sourceType: String = "",
        val sourceId: String = "",
        val sourceName: String = "",
    ) : AppRoute

    @Serializable
    data object AllBrands : AppRoute

    @Serializable
    data object Favorites : AppRoute
    @Serializable
    data object Search : AppRoute

    @Serializable
    data object Profile : AppRoute

    @Serializable
    data class ProductDetails(val productId: String) : AppRoute

    @Serializable
    data object Cart : AppRoute

    @Serializable
    data object Checkout : AppRoute

    @Serializable
    data object Orders : AppRoute
    @Serializable
    data object PaymentMethods : AppRoute

    @Serializable
    data object ManageAddresses : AppRoute

    @Serializable
    data object NewAddress : AppRoute
}

package com.troves.presintation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey {
    @Serializable
    data class SeeAll(
        val type: SeeAllType,
        val id: String? = null,
        val name: String? = null
    ) : AppRoute

    @Serializable
    enum class SeeAllType {
        CATEGORIES, BRANDS, PRODUCTS
    }

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
    data object Favorites : AppRoute

    @Serializable
    data object Search : AppRoute

    @Serializable
    data object Profile : AppRoute

    @Serializable
    data object AiChat : AppRoute

    @Serializable
    data object Survey : AppRoute

    @Serializable
    data class ProductDetails(val productId: String) : AppRoute

    @Serializable
    data object Cart : AppRoute

    @Serializable
    data object Orders : AppRoute

    @Serializable
    data object PaymentMethods : AppRoute

    @Serializable
    data object ManageAddresses : AppRoute

    @Serializable
    data class NewAddress(val addressId: String? = null) : AppRoute

    @Serializable
    data class OrderResult(
        val success: Boolean,
        val orderName: String? = null,
        val errorMessage: String? = null,
        val paymentLabel: String = "",
        val recipientName: String = "",
        val addressLines: List<String> = emptyList(),
        val phone: String = "",
        val itemImageUrls: List<String> = emptyList(),
        val itemCount: Int = 0,
        val subtotalFormatted: String = "",
        val discountLabel: String? = null,
        val discountValueFormatted: String? = null,
        val totalFormatted: String = "",
        val statusUrl: String? = null,
    ) : AppRoute
}

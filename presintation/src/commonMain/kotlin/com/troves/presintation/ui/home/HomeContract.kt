package com.troves.presintation.ui.home

import com.troves.domain.entity.Ad
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Category
import com.troves.domain.entity.Product

data class HomeUiState(
    val isLoading: Boolean = true,
    val ads: List<Ad> = emptyList(),
    val brands: List<Brand> = emptyList(),
    val justForYou: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val trending: List<Product> = emptyList(),
    val favoriteProductIds: Set<Long> = emptySet(),
    val errorMessage: String? = null,
    val showSignUpPrompt: Boolean = false,
) {
    val hasError: Boolean get() = errorMessage != null
}

sealed interface HomeEffect {
    data class NavigateToProduct(val productId: String) : HomeEffect
    data class NavigateToProducts(
        val sourceType: String = "",
        val sourceId: String = "",
        val sourceName: String = "",
    ) : HomeEffect
    data class ShowToast(val message: String) : HomeEffect
    data object NavigateToAllBrands : HomeEffect
    data object NavigateToRegister : HomeEffect
    data object NavigateToCart : HomeEffect
    data object ShowLoginRequiredDialog : HomeEffect
}

sealed interface HomeIntent {
    data object Load : HomeIntent
    data object Retry : HomeIntent
    data object SearchClicked : HomeIntent
    data object CartClicked : HomeIntent
    data object SeeAllBrandsClicked : HomeIntent
    data object SignUpPromptConfirmed : HomeIntent
    data object SignUpPromptDismissed : HomeIntent
    data class AdClicked(val ad: Ad) : HomeIntent
    data class BrandClicked(val brand: Brand) : HomeIntent
    data class CategoryClicked(val category: Category) : HomeIntent
    data class ProductClicked(val product: Product) : HomeIntent
    data class FavoriteToggled(val product: Product) : HomeIntent
}

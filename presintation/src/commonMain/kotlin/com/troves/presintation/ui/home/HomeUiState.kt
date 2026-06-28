package com.troves.presintation.ui.Home

import com.troves.domain.Product
import com.troves.domain.home.Ad
import com.troves.domain.home.Brand
import com.troves.domain.home.Category

/**
 * Single immutable snapshot of everything the Home screen renders.
 * The screen is a pure function of this state.
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val ads: List<Ad> = emptyList(),
    val brands: List<Brand> = emptyList(),
    val justForYou: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val trending: List<Product> = emptyList(),
    val favoriteProductIds: Set<Long> = emptySet(),
    val errorMessage: String? = null,
) {
    val hasError: Boolean get() = errorMessage != null
}

/**
 * User (and lifecycle) driven actions the Home screen can dispatch to the
 * [HomeViewModel]. The View only ever sends intents — never mutates state.
 */
sealed interface HomeIntent {
    data object Load : HomeIntent
    data object Retry : HomeIntent
    data object SearchClicked : HomeIntent
    data object CartClicked : HomeIntent
    data object SeeAllBrandsClicked : HomeIntent
    data class AdClicked(val ad: Ad) : HomeIntent
    data class BrandClicked(val brand: Brand) : HomeIntent
    data class CategoryClicked(val category: Category) : HomeIntent
    data class ProductClicked(val product: Product) : HomeIntent
    data class FavoriteToggled(val product: Product) : HomeIntent
}

/**
 * One-shot side effects (navigation, transient messages). Consumed once by the
 * View; they are not part of the persistent [HomeUiState].
 */
sealed interface HomeEffect {
    data class NavigateToProduct(val productId: String) : HomeEffect
    data class ShowToast(val message: String) : HomeEffect
}

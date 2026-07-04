package com.troves.presintation.ui.seeall

import com.troves.domain.entity.Brand
import com.troves.domain.entity.Category
import com.troves.domain.entity.Product
import com.troves.presintation.navigation.AppRoute

data class SeeAllUiState(
    val type: AppRoute.SeeAllType = AppRoute.SeeAllType.PRODUCTS,
    val title: String = "",
    val isLoading: Boolean = false,
    val brands: List<Brand> = emptyList(),
    val categories: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val errorMessage: String? = null,
) {
    val hasError: Boolean get() = errorMessage != null
}

sealed interface SeeAllEffect {
    data object NavigateBack : SeeAllEffect
    data class NavigateToProducts(val sourceType: String, val sourceId: String, val sourceName: String) : SeeAllEffect
    data class NavigateToProductDetails(val productId: String) : SeeAllEffect
    data class ShowToast(val message: String) : SeeAllEffect
}

sealed interface SeeAllIntent {
    data class Init(val type: AppRoute.SeeAllType, val id: String?, val name: String?) : SeeAllIntent
    data object Refresh : SeeAllIntent
    data object OnBackClick : SeeAllIntent
    data class BrandClicked(val brand: Brand) : SeeAllIntent
    data class CategoryClicked(val category: Category) : SeeAllIntent
    data class ProductClicked(val product: Product) : SeeAllIntent
}

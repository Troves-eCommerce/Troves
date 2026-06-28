package com.troves.presintation.ui.home

import com.troves.domain.entity.Product
import com.troves.domain.entity.Ad
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Category


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




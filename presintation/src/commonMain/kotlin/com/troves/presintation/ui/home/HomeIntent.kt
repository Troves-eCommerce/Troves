package com.troves.presintation.ui.home

import com.troves.domain.entity.Product
import com.troves.domain.entity.Ad
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Category

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

package com.troves.presintation.ui.productDetails

import com.troves.presintation.ui.productDetails.models.ColorUi
import com.troves.presintation.ui.productDetails.models.ReviewUi
import com.troves.presintation.ui.productDetails.models.SizeUi

data class ProductDetailUiState(
    val title: String = "",
    val priceFormatted: String= "",
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val description: String= "",
    val images: List<String> = emptyList(),
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val reviews: List<ReviewUi> = emptyList(),
    val selectedSizeLabel: String = "",
    val selectedColorIndex: Int = 0,
    val currentImageIndex: Int = 0,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
){
    val hasError = errorMessage != null
}
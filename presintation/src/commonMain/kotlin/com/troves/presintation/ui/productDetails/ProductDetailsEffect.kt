package com.troves.presintation.ui.productDetails

sealed interface ProductDetailsEffect {
    data object NavigateBack: ProductDetailsEffect
    data class ShowToast(val message: String): ProductDetailsEffect
}
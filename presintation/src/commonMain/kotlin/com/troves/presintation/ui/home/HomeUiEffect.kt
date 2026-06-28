package com.troves.presintation.ui.home

sealed interface HomeEffect {
    data class NavigateToProduct(val productId: String) : HomeEffect
    data class ShowToast(val message: String) : HomeEffect
}

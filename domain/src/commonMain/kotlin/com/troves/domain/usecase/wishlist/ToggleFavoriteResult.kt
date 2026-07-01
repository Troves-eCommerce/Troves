package com.troves.domain.usecase.wishlist

sealed interface ToggleFavoriteResult {
    data object Added : ToggleFavoriteResult
    data object Removed : ToggleFavoriteResult
    data object RequiresLogin : ToggleFavoriteResult
    data class Error(val throwable: Throwable) : ToggleFavoriteResult
}
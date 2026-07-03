package com.troves.domain.usecase.cart

sealed interface CartOperationResult {
    data object Success : CartOperationResult
    data object RequiresLogin : CartOperationResult
    data class Error(val throwable: Throwable) : CartOperationResult
}

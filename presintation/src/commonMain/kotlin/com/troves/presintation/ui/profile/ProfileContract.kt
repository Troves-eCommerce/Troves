package com.troves.presintation.ui.profile

data class ProfileState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface ProfileEffect {
    data class ShowError(val message: String) : ProfileEffect
    data object NavigateToLogin : ProfileEffect
}

sealed interface ProfileIntent {
    data object Logout : ProfileIntent
}

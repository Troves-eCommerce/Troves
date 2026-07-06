package com.troves.presintation.ui.auth

data class AuthState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface AuthEffect {
    data class ShowMessage(val message: String) : AuthEffect
    data class ShowError(val message: String) : AuthEffect
    data object OnRegistered : AuthEffect

    data object NavigateToHome : AuthEffect
    data object NavigateToEmailVerification : AuthEffect
}

sealed interface AuthIntent {
    data class EmailChanged(val value: String) : AuthIntent
    data class PasswordChanged(val value: String) : AuthIntent
    data class ConfirmPasswordChanged(val value: String) : AuthIntent
    data object TogglePasswordVisibility : AuthIntent
    data object ToggleConfirmPasswordVisibility : AuthIntent
    data object Login : AuthIntent
    data object Register : AuthIntent
    data class GoogleSignIn(val idToken: String, val accessToken: String?) : AuthIntent
}
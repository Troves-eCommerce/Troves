package com.troves.presintation.ui.auth

data class EmailVerificationState(
    val email: String = "",
    val isResending: Boolean = false,
    val isChecking: Boolean = false,
    val isEmailVerified: Boolean = false,
    val errorMessage: String? = null,
    val resendCooldownSeconds: Int = 0,
) {
    val canResend: Boolean
        get() = resendCooldownSeconds <= 0 && !isResending
}

sealed interface EmailVerificationEffect {
    data class ShowMessage(val message: String) : EmailVerificationEffect
    data class ShowError(val message: String) : EmailVerificationEffect
    data object NavigateToHome : EmailVerificationEffect
}

sealed interface EmailVerificationIntent {
    data object ResendVerificationEmail : EmailVerificationIntent
    data object CheckVerificationStatus : EmailVerificationIntent
}
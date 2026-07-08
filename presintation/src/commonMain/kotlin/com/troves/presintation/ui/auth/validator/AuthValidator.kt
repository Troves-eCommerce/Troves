package com.troves.presintation.ui.auth.validator

import org.jetbrains.compose.resources.getString
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.auth_email_empty
import troves.presintation.generated.resources.auth_email_invalid
import troves.presintation.generated.resources.auth_password_too_short

object AuthValidator {
    suspend fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> getString(Res.string.auth_email_empty)
            !isValidEmail(email) -> getString(Res.string.auth_email_invalid)
            else -> null
        }
    }

    suspend fun validatePassword(password: String): String? {
        return if (password.length < 6) {
            getString(Res.string.auth_password_too_short)
        } else {
            null
        }
    }

    private fun isValidEmail(email: String): Boolean =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$").matches(email)
}

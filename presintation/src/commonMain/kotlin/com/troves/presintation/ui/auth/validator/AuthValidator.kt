package com.troves.presintation.ui.auth.validator

object AuthValidator {
    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email cannot be empty"
            !isValidEmail(email) -> "Please enter a valid email address"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return if (password.length < 6) {
            "Password must be at least 6 characters"
        } else {
            null
        }
    }

    private fun isValidEmail(email: String): Boolean =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$").matches(email)
}

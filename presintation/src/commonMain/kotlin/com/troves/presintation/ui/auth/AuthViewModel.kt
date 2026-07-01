package com.troves.presintation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.auth.LoginUseCase
import com.troves.domain.usecase.auth.RegisterUseCase
import com.troves.domain.utils.Result
import com.troves.domain.usecase.auth.SignInWithGoogleUseCase
import com.troves.domain.usecase.wishlist.SyncWishlistUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import com.troves.presintation.ui.auth.validator.AuthValidator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val syncWishlistUseCase: SyncWishlistUseCase
) : ViewModel(),
    StateHolder<AuthState> by DefaultStateHolder(AuthState()),
    EffectPublisher<AuthEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.EmailChanged ->
                updateState { copy(email = intent.value, errorMessage = null) }

            is AuthIntent.PasswordChanged ->
                updateState { copy(password = intent.value, errorMessage = null) }

            is AuthIntent.ConfirmPasswordChanged ->
                updateState { copy(confirmPassword = intent.value, errorMessage = null) }

            AuthIntent.TogglePasswordVisibility ->
                updateState { copy(passwordVisible = !passwordVisible) }

            AuthIntent.ToggleConfirmPasswordVisibility ->
                updateState { copy(confirmPasswordVisible = !confirmPasswordVisible) }

            AuthIntent.Login -> login()
            AuthIntent.Register -> register()
            is AuthIntent.GoogleSignIn -> signInWithGoogle(intent.idToken, intent.accessToken)
        }
    }

    private fun login() {
        val email = currentState.email
        val password = currentState.password
        if (!validateInputs(email, password)) return

        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> onAuthenticated("Logged in successfully")
                is Result.Error -> updateState {
                    copy(
                        isLoading = false,
                        errorMessage = result.throwable.message
                            ?: "Login failed. Please try again.",
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }

    private fun register() {
        val email = currentState.email
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword
        if (!validateInputs(email, password)) return
        if (password != confirmPassword) {
            updateState { copy(errorMessage = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            when (val result = registerUseCase(email, password)) {
                is Result.Success -> onAuthenticated("Account created successfully")
                is Result.Error -> updateState {
                    copy(
                        isLoading = false,
                        errorMessage = result.throwable.message
                            ?: "Registration failed. Please try again.",
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }

    private fun signInWithGoogle(idToken: String, accessToken: String?) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            when (val result = signInWithGoogleUseCase(idToken, accessToken)) {
                is Result.Success -> onAuthenticated("Signed in with Google successfully")
                is Result.Error -> updateState {
                    copy(
                        isLoading = false,
                        errorMessage = result.throwable.message
                            ?: "Google Sign-In failed. Please try again.",
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }

    private suspend fun onAuthenticated(message: String) {
        runCatching { syncWishlistUseCase() }
        updateState { copy(isLoading = false) }
        sendEffect(AuthEffect.ShowMessage(message))
        delay(SUCCESS_NAV_DELAY_MS)
        sendEffect(AuthEffect.NavigateToHome)
    }

    private fun validateInputs(email: String, password: String): Boolean {
        val error = AuthValidator.validateEmail(email) ?: AuthValidator.validatePassword(password)
        if (error != null) {
            updateState { copy(errorMessage = error) }
            return false
        }
        return true
    }

    private companion object {
        const val SUCCESS_NAV_DELAY_MS = 1000L
    }
}
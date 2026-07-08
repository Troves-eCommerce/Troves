package com.troves.presintation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.usecase.auth.LoginUseCase
import com.troves.domain.usecase.auth.RegisterUseCase
import com.troves.domain.usecase.auth.SendEmailVerificationUseCase
import com.troves.domain.usecase.auth.SignInWithGoogleUseCase
import com.troves.domain.usecase.shared.ObserveConnectivityUseCase
import com.troves.domain.usecase.wishlist.SyncWishlistUseCase
import com.troves.domain.usecase.cart.SyncCartUseCase
import com.troves.domain.utils.Result
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import com.troves.presintation.ui.auth.validator.AuthValidator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import org.jetbrains.compose.resources.getString
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.auth_account_created
import troves.presintation.generated.resources.auth_google_failed
import troves.presintation.generated.resources.auth_google_success
import troves.presintation.generated.resources.auth_login_failed
import troves.presintation.generated.resources.auth_login_success
import troves.presintation.generated.resources.auth_passwords_no_match
import troves.presintation.generated.resources.auth_register_failed
import troves.presintation.generated.resources.no_internet_connection

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val syncWishlistUseCase: SyncWishlistUseCase,
    private val syncCartUseCase: SyncCartUseCase,
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val authenticationRepository: AuthenticationRepository,
    private val observeConnectivity: ObserveConnectivityUseCase,
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
        viewModelScope.launch {
            val email = currentState.email
            val password = currentState.password
            if (!validateInputs(email, password)) return@launch
            if (!ensureOnline()) return@launch

            updateState { copy(isLoading = true, errorMessage = null) }
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> onAuthenticated(getString(Res.string.auth_login_success))
                is Result.Error -> {
                    val msg = result.throwable.message ?: getString(Res.string.auth_login_failed)
                    updateState { copy(isLoading = false, errorMessage = msg) }
                }
                is Result.Loading -> Unit
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            val email = currentState.email
            val password = currentState.password
            val confirmPassword = currentState.confirmPassword
            if (!validateInputs(email, password)) return@launch
            if (password != confirmPassword) {
                val msg = getString(Res.string.auth_passwords_no_match)
                updateState { copy(errorMessage = msg) }
                return@launch
            }
            if (!ensureOnline()) return@launch

            updateState { copy(isLoading = true, errorMessage = null) }
            when (val result = registerUseCase(email, password)) {
                is Result.Success -> {
                    runCatching { sendEmailVerificationUseCase() }
                    onAuthenticated(getString(Res.string.auth_account_created))
                }
                is Result.Error -> {
                    val msg = result.throwable.message ?: getString(Res.string.auth_register_failed)
                    updateState { copy(isLoading = false, errorMessage = msg) }
                }
                is Result.Loading -> Unit
            }
        }
    }

    private fun signInWithGoogle(idToken: String, accessToken: String?) {
        viewModelScope.launch {
            if (!ensureOnline()) return@launch
            updateState { copy(isGoogleLoading = true, errorMessage = null) }
            when (val result = signInWithGoogleUseCase(idToken, accessToken)) {
                is Result.Success -> {
                    onAuthenticated(getString(Res.string.auth_google_success), isGoogle = true)
                }
                is Result.Error -> {
                    val msg = result.throwable.message ?: getString(Res.string.auth_google_failed)
                    updateState { copy(isGoogleLoading = false, errorMessage = msg) }
                }
                is Result.Loading -> Unit
            }
        }
    }

    /**
     * Routing depends on the freshly-read `isEmailVerified` flag, never on
     * whether this was login/register/google. That single check is what makes
     * Google sign-ins (already verified by Google) go straight to Home, and
     * makes any unverified email/password account always land on the
     * verification screen — whether it just registered or is logging back in
     * later without ever confirming its email. Shopify provisioning already
     * happened inside the repository's login/register/signInWithGoogle calls
     * above; nothing about that is touched here.
     */
    private suspend fun onAuthenticated(message: String, isGoogle: Boolean = false) {
        runCatching { syncWishlistUseCase() }
        runCatching { syncCartUseCase() }
        updateState { copy(isLoading = false, isGoogleLoading = false) }
        sendEffect(AuthEffect.ShowMessage(message))
        delay(SUCCESS_NAV_DELAY_MS.milliseconds)
        sendEffect(AuthEffect.OnRegistered)

        val isVerified = authenticationRepository.getCurrentUserProfile()?.isEmailVerified ?: false
        if (isVerified) {
            sendEffect(AuthEffect.NavigateToHome)
        } else {
            sendEffect(AuthEffect.NavigateToEmailVerification)
        }
    }

    /**
     * Pre-flight connectivity guard. When offline, surfaces the message both
     * inline (errorMessage) and as an error toast, and returns false so the
     * caller skips the network request entirely.
     */
    private suspend fun ensureOnline(): Boolean {
        if (observeConnectivity.isOnlineNow()) return true
        val msg = getString(Res.string.no_internet_connection)
        updateState { copy(isLoading = false, isGoogleLoading = false, errorMessage = msg) }
        sendEffect(AuthEffect.ShowError(msg))
        return false
    }

    private suspend fun validateInputs(email: String, password: String): Boolean {
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
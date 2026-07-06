package com.troves.presintation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.usecase.auth.ReloadUserUseCase
import com.troves.domain.usecase.auth.SendEmailVerificationUseCase
import com.troves.domain.utils.Result
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class EmailVerificationViewModel(
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val reloadUserUseCase: ReloadUserUseCase,
    private val authenticationRepository: AuthenticationRepository
) : ViewModel(),
    StateHolder<EmailVerificationState> by DefaultStateHolder(EmailVerificationState()),
    EffectPublisher<EmailVerificationEffect> by DefaultEffectPublisher() {

    private var cooldownJob: Job? = null

    init {
        authenticationRepository.currentUserStream
            .onEach { user ->
                updateState {
                    copy(
                        email = user?.email ?: "",
                        isEmailVerified = user?.isEmailVerified ?: false
                    )
                }
                if (user?.isEmailVerified == true) {
                    sendEffect(EmailVerificationEffect.NavigateToHome)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: EmailVerificationIntent) {
        when (intent) {
            EmailVerificationIntent.ResendVerificationEmail -> resendEmail()
            EmailVerificationIntent.CheckVerificationStatus -> checkStatus()
        }
    }

    private fun resendEmail() {
        if (!currentState.canResend) return

        viewModelScope.launch {
            updateState { copy(isResending = true, errorMessage = null) }
            when (val result = sendEmailVerificationUseCase()) {
                is Result.Success -> {
                    updateState { copy(isResending = false) }
                    sendEffect(EmailVerificationEffect.ShowMessage("Verification email sent!"))
                    startResendCooldown()
                }
                is Result.Error -> {
                    updateState {
                        copy(
                            isResending = false,
                            errorMessage = result.throwable.message ?: "Failed to send email"
                        )
                    }
                }
                is Result.Loading -> Unit
            }
        }
    }

    private fun startResendCooldown(seconds: Int = RESEND_COOLDOWN_SECONDS) {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            var remaining = seconds
            updateState { copy(resendCooldownSeconds = remaining) }
            while (remaining > 0) {
                delay(1_000.milliseconds)
                remaining -= 1
                updateState { copy(resendCooldownSeconds = remaining) }
            }
        }
    }

    private fun checkStatus() {
        viewModelScope.launch {
            updateState { copy(isChecking = true, errorMessage = null) }
            when (val result = reloadUserUseCase()) {
                is Result.Success -> {
                    val profile = authenticationRepository.getCurrentUserProfile()
                    val verified = profile?.isEmailVerified ?: false
                    updateState {
                        copy(
                            isChecking = false,
                            isEmailVerified = verified,
                            errorMessage = if (verified) null else "Email is not verified yet. Please check your inbox."
                        )
                    }
                    if (verified) {
                        sendEffect(EmailVerificationEffect.NavigateToHome)
                    }
                }
                is Result.Error -> {
                    updateState {
                        copy(
                            isChecking = false,
                            errorMessage = result.throwable.message ?: "Failed to check status"
                        )
                    }
                }
                is Result.Loading -> Unit
            }
        }
    }

    override fun onCleared() {
        cooldownJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val RESEND_COOLDOWN_SECONDS = 60
    }
}
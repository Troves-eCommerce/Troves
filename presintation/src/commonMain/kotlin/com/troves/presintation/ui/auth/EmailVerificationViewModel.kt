package com.troves.presintation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.usecase.auth.ReloadUserUseCase
import com.troves.domain.usecase.auth.SendEmailVerificationUseCase
import com.troves.domain.usecase.shared.ObserveConnectivityUseCase
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
import org.jetbrains.compose.resources.getString
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.no_internet_connection
import troves.presintation.generated.resources.verify_email_check_failed
import troves.presintation.generated.resources.verify_email_not_verified_yet
import troves.presintation.generated.resources.verify_email_send_failed
import troves.presintation.generated.resources.verify_email_sent

class EmailVerificationViewModel(
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val reloadUserUseCase: ReloadUserUseCase,
    private val authenticationRepository: AuthenticationRepository,
    private val observeConnectivity: ObserveConnectivityUseCase,
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
            if (!ensureOnline()) return@launch
            updateState { copy(isResending = true, errorMessage = null) }
            when (val result = sendEmailVerificationUseCase()) {
                is Result.Success -> {
                    updateState { copy(isResending = false) }
                    sendEffect(EmailVerificationEffect.ShowMessage(getString(Res.string.verify_email_sent)))
                    startResendCooldown()
                }
                is Result.Error -> {
                    val msg = result.throwable.message ?: getString(Res.string.verify_email_send_failed)
                    updateState { copy(isResending = false, errorMessage = msg) }
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
            if (!ensureOnline()) return@launch
            updateState { copy(isChecking = true, errorMessage = null) }
            when (val result = reloadUserUseCase()) {
                is Result.Success -> {
                    val profile = authenticationRepository.getCurrentUserProfile()
                    val verified = profile?.isEmailVerified ?: false
                    val notVerifiedMsg = if (verified) null else getString(Res.string.verify_email_not_verified_yet)
                    updateState {
                        copy(
                            isChecking = false,
                            isEmailVerified = verified,
                            errorMessage = notVerifiedMsg
                        )
                    }
                    if (verified) {
                        sendEffect(EmailVerificationEffect.NavigateToHome)
                    }
                }
                is Result.Error -> {
                    val msg = result.throwable.message ?: getString(Res.string.verify_email_check_failed)
                    updateState { copy(isChecking = false, errorMessage = msg) }
                }
                is Result.Loading -> Unit
            }
        }
    }

    /**
     * Pre-flight connectivity guard: when offline, show the message and skip the
     * network call rather than spinning.
     */
    private suspend fun ensureOnline(): Boolean {
        if (observeConnectivity.isOnlineNow()) return true
        val msg = getString(Res.string.no_internet_connection)
        updateState { copy(isResending = false, isChecking = false, errorMessage = msg) }
        sendEffect(EmailVerificationEffect.ShowError(msg))
        return false
    }

    override fun onCleared() {
        cooldownJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val RESEND_COOLDOWN_SECONDS = 60
    }
}
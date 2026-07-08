package com.troves.presintation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.designsystem.components.connectivity.ConnectivityBanner
import com.troves.domain.entity.ExchangeRate
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.usecase.onboarding.IsOnboardingDoneUseCase
import com.troves.domain.usecase.settings.GetExchangeRatesUseCase
import com.troves.domain.usecase.settings.ObserveProfilePreferencesUseCase
import com.troves.domain.usecase.shared.ObserveConnectivityUseCase
import com.troves.domain.utils.connectivity.ConnectivityStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    observeProfilePreferences: ObserveProfilePreferencesUseCase,
    private val isOnboardingDone: IsOnboardingDoneUseCase,
    getExchangeRatesUseCase: GetExchangeRatesUseCase,
    private val authenticationRepository: AuthenticationRepository,
    observeConnectivity: ObserveConnectivityUseCase,
) : ViewModel() {

    /** Live online/offline flag for pre-flight guards (checkout, mutations, auth). */
    val isOnline: StateFlow<Boolean> = observeConnectivity()
        .map { it == ConnectivityStatus.Available }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true,
        )

    /**
     * Drives the app-wide banner: persistent [ConnectivityBanner.Offline] while
     * disconnected, a brief [ConnectivityBanner.BackOnline] on reconnect, then
     * [ConnectivityBanner.Hidden]. Kept out of the main [combine] so the
     * transient auto-hide timing stays isolated.
     */
    val banner: StateFlow<ConnectivityBanner> = flow {
        var wasOffline = false
        observeConnectivity().collect { status ->
            when (status) {
                ConnectivityStatus.Unavailable -> {
                    wasOffline = true
                    emit(ConnectivityBanner.Offline)
                }
                ConnectivityStatus.Available -> {
                    if (wasOffline) {
                        wasOffline = false
                        emit(ConnectivityBanner.BackOnline)
                        delay(BACK_ONLINE_VISIBLE_MS)
                        emit(ConnectivityBanner.Hidden)
                    } else {
                        emit(ConnectivityBanner.Hidden)
                    }
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ConnectivityBanner.Hidden,
    )

    /** Fires once on every offline → online transition; screens use it to refetch fresh data. */
    val reconnectSignal: SharedFlow<Unit> = observeConnectivity()
        .map { it == ConnectivityStatus.Available }
        .distinctUntilChanged()
        .drop(1)          // ignore the initial emission
        .filter { it }    // only online transitions
        .map { }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000))

    val uiState: StateFlow<MainUiState> = combine(
        observeProfilePreferences(),
        getExchangeRatesUseCase()
    ) { prefs, rates ->
        val destination = try {
            when {
                !isOnboardingDone() -> StartDestination.Onboarding
                hasUnverifiedSession() -> StartDestination.EmailVerification
                else -> StartDestination.Home
            }
        } catch (e: Exception) {
            StartDestination.Onboarding
        }

        MainUiState(
            themeMode = prefs.themeMode,
            language = prefs.language,
            currency = prefs.currency,
            exchangeRate = rates,
            startDestination = destination,
            isLoading = false
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState(isLoading = true)
        )

    /**
     * True only when there's an actual Firebase session (the user registered
     * or logged in before) whose email is still not verified. A guest with no
     * session at all returns false here and falls through to Home normally.
     */
    private suspend fun hasUnverifiedSession(): Boolean {
        val profile = authenticationRepository.getCurrentUserProfile() ?: return false
        return !profile.isEmailVerified
    }

    private companion object {
        const val BACK_ONLINE_VISIBLE_MS = 3000L
    }
}

data class MainUiState(
    val isLoading: Boolean = true,
    val themeMode: String = "system",
    val language: String = "en",
    val currency: String = "USD",
    val exchangeRate: ExchangeRate? = null,
    val startDestination: StartDestination = StartDestination.Onboarding
)

enum class StartDestination {
    Onboarding,
    EmailVerification,
    Home
}
package com.troves.presintation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.auth.LogoutUseCase
import com.troves.domain.usecase.settings.FetchLatestRatesUseCase
import com.troves.domain.usecase.settings.GetExchangeRatesUseCase
import com.troves.domain.usecase.settings.ObserveProfilePreferencesUseCase
import com.troves.domain.usecase.settings.SetCurrencyUseCase
import com.troves.domain.usecase.settings.SetLanguageUseCase
import com.troves.domain.usecase.settings.SetThemeModeUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val observeProfilePreferences: ObserveProfilePreferencesUseCase,
    private val setLanguageUseCase: SetLanguageUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val setCurrencyUseCase: SetCurrencyUseCase,
    private val getExchangeRatesUseCase: GetExchangeRatesUseCase,
    private val fetchLatestRatesUseCase: FetchLatestRatesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileEffect>()
    val effect = _effect.asSharedFlow()

    init {
        onIntent(ProfileIntent.LoadData)
        observeExchangeRates()
        refreshExchangeRates()
    }

    private fun observeExchangeRates() {
        getExchangeRatesUseCase()
            .onEach { rates ->
                _uiState.update { it.copy(exchangeRate = rates) }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshExchangeRates() {
        viewModelScope.launch {
            fetchLatestRatesUseCase("EGP").onFailure { e ->
                _effect.emit(ProfileEffect.ShowError("Rates: ${e.message}"))
            }
        }
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadData -> loadData()
            is ProfileIntent.EditProfileClicked -> emitEffect(ProfileEffect.NavigateToEditProfile)
            is ProfileIntent.ManageAddressesClicked -> emitEffect(ProfileEffect.NavigateToAddresses)
            is ProfileIntent.OrderHistoryClicked -> emitEffect(ProfileEffect.NavigateToOrders)
            is ProfileIntent.PaymentMethodsClicked -> emitEffect(ProfileEffect.NavigateToPaymentMethods)
            is ProfileIntent.AiAssistantClicked -> emitEffect(ProfileEffect.NavigateToAiAssistant)
            is ProfileIntent.LanguageClicked -> Unit
            is ProfileIntent.LanguageSelected -> selectLanguage(intent.language)
            is ProfileIntent.DarkModeChanged -> setDarkMode(intent.enabled)
            is ProfileIntent.CurrencySelected -> selectCurrency(intent.currency)
            is ProfileIntent.LogoutClicked -> _uiState.update { it.copy(showLogoutDialog = true) }
            is ProfileIntent.LogoutConfirmed -> logout()
            is ProfileIntent.LogoutDismissed -> _uiState.update { it.copy(showLogoutDialog = false) }
            is ProfileIntent.LoginClicked -> emitEffect(ProfileEffect.NavigateToLogin)
        }
    }

    private fun loadData() {
        _uiState.update { it.copy(isLoading = true) }
        observeProfilePreferences()
            .onEach { prefs ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isGuest = !prefs.isLoggedIn,
                        userName = prefs.displayName ?: "",
                        userEmail = prefs.email ?: "",
                        selectedLanguage = prefs.language,
                        themeMode = prefs.themeMode,
                        selectedCurrency = prefs.currency,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun selectLanguage(language: String) {
        viewModelScope.launch {
            setLanguageUseCase(language)
        }
    }

    private fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            setThemeModeUseCase(enabled)
        }
    }

    private fun selectCurrency(currency: String) {
        viewModelScope.launch {
            setCurrencyUseCase(currency)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showLogoutDialog = false) }
            try {
                logoutUseCase()
                _effect.emit(ProfileEffect.NavigateToLogin)
            } catch (e: Exception) {
                _effect.emit(ProfileEffect.ShowError(e.message ?: "Failed to logout"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun emitEffect(effect: ProfileEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}

package com.troves.presintation.ui.profile

import com.troves.domain.entity.ExchangeRate

data class ProfileState(
    val isLoading: Boolean = false,
    val isGuest: Boolean = true,
    val userName: String = "",
    val userEmail: String = "",
    val selectedLanguage: String = "en",
    val themeMode: String = "system",
    val selectedCurrency: String = "USD",
    val exchangeRate: ExchangeRate? = null,
    val showLogoutDialog: Boolean = false,
    val errorMessage: String? = null
)

sealed interface ProfileEffect {
    data object NavigateToLogin : ProfileEffect

    data object NavigateToAddresses : ProfileEffect

    data object NavigateToOrders : ProfileEffect

    data object NavigateToPaymentMethods : ProfileEffect

    data object NavigateToEditProfile : ProfileEffect

    data object NavigateToAiAssistant : ProfileEffect

    data object NavigateToSurvey : ProfileEffect

    data class ShowError(
        val message: String
    ) : ProfileEffect
}

sealed interface ProfileIntent {

    data object LoadData : ProfileIntent

    data object EditProfileClicked : ProfileIntent

    data object ManageAddressesClicked : ProfileIntent

    data object OrderHistoryClicked : ProfileIntent

    data object PaymentMethodsClicked : ProfileIntent

    data object AiAssistantClicked : ProfileIntent

    data object SurveyClicked : ProfileIntent

    data object LanguageClicked : ProfileIntent

    data class LanguageSelected(
        val language: String
    ) : ProfileIntent

    data class DarkModeChanged(
        val enabled: Boolean
    ) : ProfileIntent

    data class CurrencySelected(
        val currency: String
    ) : ProfileIntent

    data object LogoutClicked : ProfileIntent

    data object LogoutConfirmed : ProfileIntent

    data object LogoutDismissed : ProfileIntent

    data object LoginClicked : ProfileIntent
}

package com.troves.presintation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.onboarding.IsOnboardingDoneUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val isOnboardingDone: IsOnboardingDoneUseCase
) : ViewModel() {

    private val _startDestination = MutableStateFlow<StartDestination?>(null)
    val startDestination: StateFlow<StartDestination?> = _startDestination.asStateFlow()

    init {
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            val done = isOnboardingDone()
            _startDestination.value = if (done) {
                StartDestination.Login
            } else {
                StartDestination.Onboarding
            }
        }
    }
}

enum class StartDestination {
    Onboarding,
    Login
}

package com.troves.presintation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.auth.LogoutUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.Logout -> handleLogout()
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
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
}

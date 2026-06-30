package com.troves.presintation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.auth.LoginUseCase
import com.troves.domain.usecase.auth.RegisterUseCase
import com.troves.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ── UI State ─────────────────────────────────────────────────────────────────

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data object Success : AuthUiState
    data class Error(val message: String) : AuthUiState
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (!validateInputs(email, password)) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = when (val result = loginUseCase(email, password)) {
                is Result.Success -> AuthUiState.Success
                is Result.Error   -> AuthUiState.Error(
                    result.throwable.message ?: "Login failed. Please try again."
                )
                is Result.Loading -> AuthUiState.Loading
            }
        }
    }

    fun register(email: String, password: String) {
        if (!validateInputs(email, password)) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = when (val result = registerUseCase(email, password)) {
                is Result.Success -> AuthUiState.Success
                is Result.Error   -> AuthUiState.Error(
                    result.throwable.message ?: "Registration failed. Please try again."
                )
                is Result.Loading -> AuthUiState.Loading
            }
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isBlank()) {
            _uiState.value = AuthUiState.Error("Email cannot be empty")
            return false
        }
        if (!isValidEmail(email)) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address")
            return false
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters")
            return false
        }
        return true
    }

    private fun isValidEmail(email: String): Boolean =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$").matches(email)
}

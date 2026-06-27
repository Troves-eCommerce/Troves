package com.example.presintation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.GetProductsUseCase
import com.example.domain.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ── UI State ─────────────────────────────────────────────────────────────────

sealed interface ProductsUiState {
    data object Loading : ProductsUiState
    data class Success(val products: List<Product>) : ProductsUiState
    data class Error(val message: String) : ProductsUiState
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class ProductsViewModel(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = ProductsUiState.Loading
            
            when (val result = getProductsUseCase()) {
                is com.example.domain.Result.Success -> {
                    _uiState.value = ProductsUiState.Success(result.value)
                }
                is com.example.domain.Result.Error -> {
                    _uiState.value = ProductsUiState.Error(result.throwable.message ?: "Unknown error")
                }
                is com.example.domain.Result.Loading -> {
                    _uiState.value = ProductsUiState.Loading
                }
            }
        }
    }
}

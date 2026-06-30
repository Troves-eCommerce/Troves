package com.troves.presintation.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.Result
import com.troves.domain.usecase.shared.GetProductsUseCase
import com.troves.domain.entity.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProductsUiState {
    data object Loading : ProductsUiState
    data class Success(val products: List<Product>) : ProductsUiState
    data class Error(val message: String) : ProductsUiState
}

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
                is Result.Success -> {
                    _uiState.value = ProductsUiState.Success(result.value)
                }
                is Result.Error -> {
                    _uiState.value = ProductsUiState.Error(result.throwable.message ?: "Unknown error")
                }
                is Result.Loading -> {
                    _uiState.value = ProductsUiState.Loading
                }
            }
        }
    }
}

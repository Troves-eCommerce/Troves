package com.troves.presintation.ui.productDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.Result
import com.troves.domain.entity.Product
import com.troves.domain.usecase.details.GetProductByIdUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailUiState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ProductDetailsEffect>(
        Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.receiveAsFlow()


    fun onIntent(intent: ProductDetailsIntent) {
        when (intent) {
            is ProductDetailsIntent.Load ->{
                fetchProduct(intent.productId)
            }
            ProductDetailsIntent.OnBackClick -> TODO()
            is ProductDetailsIntent.OnColorSelectedChange -> TODO()
            is ProductDetailsIntent.OnFavoriteClick -> TODO()
            ProductDetailsIntent.OnSeeAllReviews -> TODO()
            ProductDetailsIntent.OnSizeGuide -> TODO()
            is ProductDetailsIntent.OnSizeSelectedChange -> TODO()
            is ProductDetailsIntent.Retry -> {
                fetchProduct(intent.productId)
            }
        }
    }

    private fun fetchProduct(productId: String) {
        viewModelScope.launch {
            val product = getProductByIdUseCase(productId)
            when(product){
                is Result.Error -> _state.update {
                    it.copy(errorMessage = product.throwable.message.toString())
                }
                Result.Loading -> {
                    _state.update {
                        it.copy(isLoading = true)
                    }
                }
                is Result.Success<Product> -> {
                    _state.update {
                        it.copy()
                    }
                }
            }

        }



    }

    private fun sendEffect(effect: ProductDetailsEffect) {
        when (effect) {
            ProductDetailsEffect.NavigateBack -> TODO()
            is ProductDetailsEffect.ShowToast -> TODO()
        }
    }


}
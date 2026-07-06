package com.troves.presintation.ui.allbrands

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.utils.Result
import com.troves.domain.usecase.home.GetBrandsUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.launch

class AllBrandsViewModel(
    private val getBrands: GetBrandsUseCase,
) : ViewModel(),
    StateHolder<AllBrandsUiState> by DefaultStateHolder(AllBrandsUiState()),
    EffectPublisher<AllBrandsEffect> by DefaultEffectPublisher() {

    init {
        onIntent(AllBrandsIntent.Load)
    }

    fun onIntent(intent: AllBrandsIntent) {
        when (intent) {
            AllBrandsIntent.Load, AllBrandsIntent.Retry -> loadBrands()
            AllBrandsIntent.OnBackClick -> sendEffect(AllBrandsEffect.NavigateBack)
            is AllBrandsIntent.BrandClicked -> sendEffect(
                AllBrandsEffect.NavigateToProducts(
                    sourceType = "brand",
                    sourceId = intent.brand.id.toString(),
                    sourceName = intent.brand.name,
                ),
            )
        }
    }

    private fun loadBrands() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            when (val result = getBrands()) {
                is Result.Success -> updateState {
                    copy(isLoading = false, errorMessage = null, brands = result.value)
                }

                is Result.Error -> updateState {
                    copy(
                        isLoading = false,
                        errorMessage = result.throwable.message ?: "Something went wrong",
                    )
                }

                is Result.Loading -> Unit
            }
        }
    }
}

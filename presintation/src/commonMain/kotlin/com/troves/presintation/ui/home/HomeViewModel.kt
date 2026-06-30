package com.troves.presintation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.auth.IsLoggedInUseCase
import com.troves.domain.Result
import com.troves.domain.getOrElse
import com.troves.domain.usecase.home.GetAdsUseCase
import com.troves.domain.usecase.home.GetBrandsUseCase
import com.troves.domain.usecase.home.GetCategoriesUseCase
import com.troves.domain.usecase.home.GetJustForYouProductsUseCase
import com.troves.domain.usecase.home.GetTrendingProductsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class HomeViewModel(
    private val getAds: GetAdsUseCase,
    private val getBrands: GetBrandsUseCase,
    private val getCategories: GetCategoriesUseCase,
    private val getJustForYou: GetJustForYouProductsUseCase,
    private val getTrending: GetTrendingProductsUseCase,
    private val isLoggedIn: IsLoggedInUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        onIntent(HomeIntent.Load)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Load, HomeIntent.Retry -> loadHomeFeed()
            HomeIntent.SearchClicked -> sendEffect(HomeEffect.ShowToast("Search is coming soon"))
            HomeIntent.CartClicked -> onCartClicked()
            HomeIntent.SignUpPromptConfirmed -> {
                _state.update { it.copy(showSignUpPrompt = false) }
                sendEffect(HomeEffect.NavigateToRegister)
            }
            HomeIntent.SignUpPromptDismissed -> _state.update { it.copy(showSignUpPrompt = false) }
            HomeIntent.SeeAllBrandsClicked -> sendEffect(HomeEffect.ShowToast("All brands coming soon"))
            is HomeIntent.AdClicked -> sendEffect(HomeEffect.ShowToast(intent.ad.titleTop))
            is HomeIntent.BrandClicked -> sendEffect(HomeEffect.ShowToast(intent.brand.name))
            is HomeIntent.CategoryClicked -> sendEffect(HomeEffect.ShowToast(intent.category.name))
            is HomeIntent.ProductClicked ->
                sendEffect(HomeEffect.NavigateToProduct(intent.product.id.toString()))
            is HomeIntent.FavoriteToggled -> toggleFavorite(intent.product.id)
        }
    }

    private fun loadHomeFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val adsDeferred = async { getAds() }
            val brandsDeferred = async { getBrands() }
            val categoriesDeferred = async { getCategories() }
            val justForYouDeferred = async { getJustForYou() }
            val trendingDeferred = async { getTrending() }

            val adsResult = adsDeferred.await()
            val brandsResult = brandsDeferred.await()
            val categoriesResult = categoriesDeferred.await()
            val justForYouResult = justForYouDeferred.await()
            val trendingResult = trendingDeferred.await()

            val firstError = listOf(
                adsResult,
                brandsResult,
                categoriesResult,
                justForYouResult,
                trendingResult,
            ).firstNotNullOfOrNull { (it as? Result.Error)?.throwable }

            _state.update {
                it.copy(
                    isLoading = false,
                    ads = adsResult.getOrElse(emptyList()),
                    brands = brandsResult.getOrElse(emptyList()),
                    categories = categoriesResult.getOrElse(emptyList()),
                    justForYou = justForYouResult.getOrElse(emptyList()),
                    trending = trendingResult.getOrElse(emptyList()),
                    errorMessage = firstError?.message ?: firstError?.let { "Something went wrong" },
                )
            }
        }
    }

    /**
     * The cart is a gated action: only signed-in users may open it. When the
     * persisted login flag is false we surface the sign-up prompt instead of
     * navigating, leaving browsing open to everyone.
     */
    private fun onCartClicked() {
        viewModelScope.launch {
            if (isLoggedIn()) {
                sendEffect(HomeEffect.ShowToast("Your cart is empty"))
            } else {
                _state.update { it.copy(showSignUpPrompt = true) }
            }
        }
    }

    private fun toggleFavorite(productId: Long) {
        _state.update { current ->
            val updated = current.favoriteProductIds.toMutableSet().apply {
                if (!add(productId)) remove(productId)
            }
            current.copy(favoriteProductIds = updated)
        }
    }

    private fun sendEffect(newEffect: HomeEffect) {
        viewModelScope.launch { _effect.send(newEffect) }
    }
}

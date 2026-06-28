package com.troves.presintation.ui.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.Result
import com.troves.domain.getOrElse
import com.troves.domain.home.GetAdsUseCase
import com.troves.domain.home.GetBrandsUseCase
import com.troves.domain.home.GetCategoriesUseCase
import com.troves.domain.home.GetJustForYouProductsUseCase
import com.troves.domain.home.GetTrendingProductsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI ViewModel for the Home screen.
 *
 * - Exposes a single [state] stream (the Model).
 * - Accepts [HomeIntent]s via [onIntent] (the Intent).
 * - Emits one-shot [HomeEffect]s through [effect] for navigation / toasts.
 *
 * All five home feeds are fetched concurrently so the screen settles in the
 * time of the slowest source rather than the sum of all of them.
 */
class HomeViewModel(
    private val getAds: GetAdsUseCase,
    private val getBrands: GetBrandsUseCase,
    private val getCategories: GetCategoriesUseCase,
    private val getJustForYou: GetJustForYouProductsUseCase,
    private val getTrending: GetTrendingProductsUseCase,
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
            HomeIntent.CartClicked -> sendEffect(HomeEffect.ShowToast("Your cart is empty"))
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

            // Fetch every section concurrently.
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

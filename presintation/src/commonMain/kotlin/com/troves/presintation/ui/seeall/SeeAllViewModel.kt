package com.troves.presintation.ui.seeall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.home.GetBrandsUseCase
import com.troves.domain.usecase.home.GetCategoriesUseCase
import com.troves.domain.usecase.home.GetJustForYouProductsUseCase
import com.troves.domain.usecase.home.GetTrendingProductsUseCase
import com.troves.domain.usecase.wishlist.GetWishlistUseCase
import com.troves.domain.usecase.wishlist.ToggleFavoriteUseCase
import com.troves.domain.utils.getOrElse
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import com.troves.presintation.navigation.AppRoute
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SeeAllViewModel(
    private val getBrands: GetBrandsUseCase,
    private val getCategories: GetCategoriesUseCase,
    private val getJustForYou: GetJustForYouProductsUseCase,
    private val getTrending: GetTrendingProductsUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel(),
    StateHolder<SeeAllUiState> by DefaultStateHolder(SeeAllUiState()),
    EffectPublisher<SeeAllEffect> by DefaultEffectPublisher() {

    init {
        getWishlistUseCase()
            .onEach { wishlist ->
                updateState { copy(favoriteProductIds = wishlist.map { it.id.toString() }.toSet()) }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: SeeAllIntent) {
        when (intent) {
            is SeeAllIntent.Init -> {
                updateState { copy(type = intent.type, title = intent.name ?: "") }
                loadData(intent.type, intent.id)
            }
            SeeAllIntent.Refresh -> loadData(state.value.type, null)
            SeeAllIntent.OnBackClick -> sendEffect(SeeAllEffect.NavigateBack)
            is SeeAllIntent.BrandClicked -> sendEffect(
                SeeAllEffect.NavigateToProducts(
                    sourceType = "brand",
                    sourceId = intent.brand.id.toString(),
                    sourceName = intent.brand.name
                )
            )
            is SeeAllIntent.CategoryClicked -> sendEffect(
                SeeAllEffect.NavigateToProducts(
                    sourceType = "category",
                    sourceId = intent.category.id.toString(),
                    sourceName = intent.category.name
                )
            )
            is SeeAllIntent.ProductClicked -> sendEffect(
                SeeAllEffect.NavigateToProductDetails(intent.product.id.toString())
            )
            is SeeAllIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    when (val result = toggleFavoriteUseCase(intent.product)) {
                        is com.troves.domain.usecase.wishlist.ToggleFavoriteResult.RequiresLogin -> {
                            sendEffect(SeeAllEffect.ShowToast("Please login to add to wishlist"))
                        }
                        is com.troves.domain.usecase.wishlist.ToggleFavoriteResult.Error -> {
                            sendEffect(SeeAllEffect.ShowToast(result.throwable.message ?: "An error occurred"))
                        }
                        com.troves.domain.usecase.wishlist.ToggleFavoriteResult.Added -> {
                            sendEffect(SeeAllEffect.ShowToast("Added to wishlist"))
                        }
                        com.troves.domain.usecase.wishlist.ToggleFavoriteResult.Removed -> {
                            sendEffect(SeeAllEffect.ShowToast("Removed from wishlist"))
                        }
                    }
                }
            }
        }
    }

    private fun loadData(type: AppRoute.SeeAllType, id: String?) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            when (type) {
                AppRoute.SeeAllType.BRANDS -> {
                    val result = getBrands()
                    updateState { copy(isLoading = false, brands = result.getOrElse(emptyList()).take(12)) }
                }
                AppRoute.SeeAllType.CATEGORIES -> {
                    val result = getCategories()
                    updateState { copy(isLoading = false, categories = result.getOrElse(emptyList()).take(9)) }
                }
                AppRoute.SeeAllType.PRODUCTS -> {
                    // Handle Just For You or Trending based on id/name
                    val result = if (id == "trending") getTrending() else getJustForYou()
                    updateState { copy(isLoading = false, products = result.getOrElse(emptyList())) }
                }
            }
        }
    }
}

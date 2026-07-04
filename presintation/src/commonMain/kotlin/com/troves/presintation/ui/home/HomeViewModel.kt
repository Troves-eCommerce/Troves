package com.troves.presintation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Product
import com.troves.domain.usecase.auth.IsLoggedInUseCase
import com.troves.domain.usecase.home.GetAdsUseCase
import com.troves.domain.usecase.home.GetBrandsUseCase
import com.troves.domain.usecase.home.GetCategoriesUseCase
import com.troves.domain.usecase.home.GetJustForYouProductsUseCase
import com.troves.domain.usecase.home.GetTrendingProductsUseCase
import com.troves.domain.usecase.wishlist.GetWishlistUseCase
import com.troves.domain.usecase.wishlist.ToggleFavoriteResult
import com.troves.domain.usecase.wishlist.ToggleFavoriteUseCase
import com.troves.domain.utils.Result
import com.troves.domain.utils.getOrElse
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class HomeViewModel(
    private val getAds: GetAdsUseCase,
    private val getBrands: GetBrandsUseCase,
    private val getCategories: GetCategoriesUseCase,
    private val getJustForYou: GetJustForYouProductsUseCase,
    private val getTrending: GetTrendingProductsUseCase,
    private val isLoggedIn: IsLoggedInUseCase,
    private val getWishlist: GetWishlistUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel(),
    StateHolder<HomeUiState> by DefaultStateHolder(HomeUiState()),
    EffectPublisher<HomeEffect> by DefaultEffectPublisher() {

    init {
        onIntent(HomeIntent.Load)
        observeWishlist()
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Load, HomeIntent.Retry -> loadHomeFeed()
            HomeIntent.SearchClicked -> sendEffect(HomeEffect.NavigateToSearch)
            HomeIntent.CartClicked -> onCartClicked()
            HomeIntent.SignUpPromptConfirmed -> {
                updateState { copy(showSignUpPrompt = false) }
                sendEffect(HomeEffect.NavigateToRegister)
            }
            HomeIntent.SignUpPromptDismissed -> updateState { copy(showSignUpPrompt = false) }
            HomeIntent.SeeAllBrandsClicked -> sendEffect(HomeEffect.NavigateToAllBrands)
            HomeIntent.ViewAllCategoriesClicked -> sendEffect(HomeEffect.NavigateToAllCategories)
            HomeIntent.ViewAllJustForYouClicked -> sendEffect(
                HomeEffect.NavigateToProducts(
                    sourceType = "collection",
                    sourceId = "just-for-you",
                    sourceName = "Just For You",
                ),
            )
            HomeIntent.ViewAllTrendingClicked -> sendEffect(
                HomeEffect.NavigateToProducts(
                    sourceType = "collection",
                    sourceId = "trending",
                    sourceName = "Trending Now",
                ),
            )
            is HomeIntent.AdClicked -> {
                if (intent.ad.buttonText == "Copy code") {
                    sendEffect(HomeEffect.ShowToast("Copied ${intent.ad.titleTop} to clipboard"))
                } else {
                    sendEffect(HomeEffect.ShowToast(intent.ad.titleTop))
                }
            }
            is HomeIntent.BrandClicked -> sendEffect(
                HomeEffect.NavigateToProducts(
                    sourceType = "brand",
                    sourceId = intent.brand.id.toString(),
                    sourceName = intent.brand.name,
                ),
            )
            is HomeIntent.CategoryClicked -> sendEffect(
                HomeEffect.NavigateToProducts(
                    sourceType = "category",
                    sourceId = intent.category.id.toString(),
                    sourceName = intent.category.name,
                ),
            )
            is HomeIntent.ProductClicked ->
                sendEffect(HomeEffect.NavigateToProduct(intent.product.id.toString()))
            is HomeIntent.FavoriteToggled -> toggleFavorite(intent.product)
        }
    }

    private fun loadHomeFeed() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }

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

            updateState {
                copy(
                    isLoading = false,
                    ads = adsResult.getOrElse(emptyList()),
                    brands = brandsResult.getOrElse(emptyList()).take(5),
                    categories = categoriesResult.getOrElse(emptyList()),
                    justForYou = justForYouResult.getOrElse(emptyList()),
                    trending = trendingResult.getOrElse(emptyList()),
                    errorMessage = firstError?.message,
                )
            }
        }
    }


    private fun observeWishlist() {
        viewModelScope.launch {
            getWishlist().collect { favorites ->
                updateState { copy(favoriteProductIds = favorites.map { it.id }.toSet()) }
            }
        }
    }


    private fun onCartClicked() {
        viewModelScope.launch {
            if (isLoggedIn()) {
                sendEffect(HomeEffect.NavigateToCart)
            } else {
                updateState { copy(showSignUpPrompt = true) }
            }
        }
    }

    private fun toggleFavorite(product: Product) {
        val wasFavorite = state.value.favoriteProductIds.contains(product.id)

        updateState {
            val updated = favoriteProductIds.toMutableSet().apply {
                if (wasFavorite) remove(product.id) else add(product.id)
            }
            copy(favoriteProductIds = updated)
        }

        viewModelScope.launch {
            when (toggleFavoriteUseCase(product)) {
                ToggleFavoriteResult.Added ->
                    sendEffect(HomeEffect.ShowToast("${product.title} added to favorites"))

                ToggleFavoriteResult.Removed ->
                    sendEffect(HomeEffect.ShowToast("${product.title} removed from favorites"))

                ToggleFavoriteResult.RequiresLogin -> {
                    updateState {
                        val reverted = favoriteProductIds.toMutableSet().apply {
                            if (wasFavorite) add(product.id) else remove(product.id)
                        }
                        copy(favoriteProductIds = reverted)
                    }
                    sendEffect(HomeEffect.ShowLoginRequiredDialog)
                }

                is ToggleFavoriteResult.Error -> {
                    updateState {
                        val reverted = favoriteProductIds.toMutableSet().apply {
                            if (wasFavorite) add(product.id) else remove(product.id)
                        }
                        copy(favoriteProductIds = reverted)
                    }
                    sendEffect(HomeEffect.ShowToast("Couldn't update favorites"))
                }
            }
        }
    }
}
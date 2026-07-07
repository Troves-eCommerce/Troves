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
import com.troves.domain.usecase.survey.IsSurveyDoneUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import com.troves.presintation.ui.home.HomeEffect.*
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
    private val observeSurveyDone: com.troves.domain.usecase.survey.ObserveSurveyDoneUseCase,
    private val completeSurvey: com.troves.domain.usecase.survey.CompleteSurveyUseCase,
    private val getCartStream: com.troves.domain.usecase.cart.GetCartStreamUseCase,
) : ViewModel(),
    StateHolder<HomeUiState> by DefaultStateHolder(HomeUiState()),
    EffectPublisher<HomeEffect> by DefaultEffectPublisher() {

    init {
        onIntent(HomeIntent.Load)
        observeWishlist()
        loadSurveyStatus()
        observeCartCount()
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Load, HomeIntent.Retry -> loadHomeFeed()
            HomeIntent.SearchClicked -> sendEffect(HomeEffect.NavigateToSearch)
            HomeIntent.CartClicked -> onCartClicked()
            HomeIntent.SurveyBannerClicked -> sendEffect(HomeEffect.NavigateToSurvey)
            HomeIntent.SurveyBannerNeverShowAgain -> dismissSurveyPermanently()
            HomeIntent.SignUpPromptConfirmed -> {
                updateState { copy(showSignUpPrompt = false) }
                sendEffect(HomeEffect.NavigateToRegister)
            }
            HomeIntent.SignUpPromptDismissed -> updateState { copy(showSignUpPrompt = false) }
            HomeIntent.SeeAllBrandsClicked -> sendEffect(HomeEffect.NavigateToAllBrands)
            HomeIntent.ViewAllCategoriesClicked -> sendEffect(HomeEffect.NavigateToAllCategories)
            HomeIntent.ViewAllJustForYouClicked -> sendEffect(
                NavigateToProducts(
                    sourceType = "collection",
                    sourceId = "just-for-you",
                    sourceName = "Just For You",
                ),
            )
            HomeIntent.ViewAllTrendingClicked -> sendEffect(
                NavigateToProducts(
                    sourceType = "collection",
                    sourceId = "trending",
                    sourceName = "Trending Now",
                ),
            )
            is HomeIntent.AdClicked -> {
                val targetType = intent.ad.targetType
                val targetId = intent.ad.targetId
                val targetName = intent.ad.targetName
                
                if (intent.ad.buttonText == "Copy code") {
                    sendEffect(ShowToast("Copied ${intent.ad.titleTop} to clipboard"))
                } else if (targetType != null && targetId != null && targetName != null) {
                    sendEffect(
                        NavigateToProducts(
                            sourceType = targetType,
                            sourceId = targetId,
                            sourceName = targetName,
                        )
                    )
                } else {
                    sendEffect(ShowToast(intent.ad.titleTop))
                }
            }
            is HomeIntent.BrandClicked -> sendEffect(
                NavigateToProducts(
                    sourceType = "brand",
                    sourceId = intent.brand.id.toString(),
                    sourceName = intent.brand.name,
                ),
            )
            is HomeIntent.CategoryClicked -> sendEffect(
                NavigateToProducts(
                    sourceType = "category",
                    sourceId = intent.category.id.toString(),
                    sourceName = intent.category.name,
                ),
            )
            is HomeIntent.ProductClicked ->
                sendEffect(NavigateToProduct(intent.product.id.toString()))
            is HomeIntent.FavoriteToggled -> toggleFavorite(intent.product)
            HomeIntent.AiClicked -> {sendEffect(NavigateToAiChat)}
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

            val loggedIn = isLoggedIn()

            updateState {
                copy(
                    isLoading = false,
                    ads = adsResult.getOrElse(emptyList()),
                    brands = brandsResult.getOrElse(emptyList()).take(12),
                    categories = categoriesResult.getOrElse(emptyList()).take(9),
                    justForYou = justForYouResult.getOrElse(emptyList()),
                    trending = trendingResult.getOrElse(emptyList()),
                    errorMessage = firstError?.message,
                    isLoggedIn = loggedIn,
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

    private fun loadSurveyStatus() {
        viewModelScope.launch {
            observeSurveyDone().collect { done ->
                updateState { copy(isSurveyDone = done) }
            }
        }
    }

    private fun dismissSurveyPermanently() {
        viewModelScope.launch {
            updateState { copy(isSurveyDone = true) }
            completeSurvey(com.troves.domain.entity.SurveyAnswers())
        }
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            getCartStream().collect { cart ->
                updateState { copy(cartItemCount = cart?.lines?.size ?: 0) }
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
                ToggleFavoriteResult.Added,
                ToggleFavoriteResult.Removed -> Unit // no toast on wishlist add/remove

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
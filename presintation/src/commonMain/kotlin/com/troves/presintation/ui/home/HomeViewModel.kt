package com.troves.presintation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Product
import com.troves.domain.entity.SurveyRecommendedItem
import com.troves.domain.usecase.auth.IsLoggedInUseCase
import com.troves.domain.usecase.home.GetAdsUseCase
import com.troves.domain.usecase.home.GetBrandsUseCase
import com.troves.domain.usecase.home.GetCategoriesUseCase
import com.troves.domain.usecase.home.GetJustForYouProductsUseCase
import com.troves.domain.usecase.home.GetSurveyRecommendationsUseCase
import com.troves.domain.usecase.home.GetTrendingProductsUseCase
import com.troves.domain.usecase.shared.ObserveConnectivityUseCase
import com.troves.domain.usecase.wishlist.GetWishlistUseCase
import com.troves.domain.usecase.wishlist.ToggleFavoriteResult
import com.troves.domain.usecase.wishlist.ToggleFavoriteUseCase
import com.troves.domain.utils.Result
import com.troves.domain.utils.connectivity.ConnectivityStatus
import com.troves.domain.utils.getOrElse
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import com.troves.presintation.ui.home.HomeEffect.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.favorites_added
import troves.presintation.generated.resources.favorites_removed
import troves.presintation.generated.resources.favorites_update_failed
import troves.presintation.generated.resources.home_copied_to_clipboard
import troves.presintation.generated.resources.home_source_just_for_you
import troves.presintation.generated.resources.home_source_trending_now
import troves.presintation.generated.resources.home_source_your_troves


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
    private val dismissSurveyBanner: com.troves.domain.usecase.survey.DismissSurveyBannerUseCase,
    private val getCartStream: com.troves.domain.usecase.cart.GetCartStreamUseCase,
    private val refreshCart: com.troves.domain.usecase.cart.RefreshCartUseCase,
    private val observeConnectivity: ObserveConnectivityUseCase,
    private val getSurveyRecommendations: GetSurveyRecommendationsUseCase,
) : ViewModel(),
    StateHolder<HomeUiState> by DefaultStateHolder(HomeUiState()),
    EffectPublisher<HomeEffect> by DefaultEffectPublisher() {

    init {
        onIntent(HomeIntent.Load)
        observeWishlist()
        loadSurveyStatus()
        observeCartCount()
        observeReconnect()
        refreshCartCount()
    }

    private fun refreshCartCount() {
        viewModelScope.launch { refreshCart() }
    }


    private fun observeReconnect() {
        val online = observeConnectivity()
            .map { it == ConnectivityStatus.Available }
            .distinctUntilChanged()

        online
            .onEach { isOnline -> updateState { copy(isOffline = !isOnline) } }
            .launchIn(viewModelScope)

        online
            .drop(1) // ignore the initial value
            .onEach { isOnline -> if (isOnline) loadHomeFeed() }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Load, HomeIntent.Retry -> loadHomeFeed()
            HomeIntent.Refresh -> loadHomeFeed(isRefresh = true)
            HomeIntent.SearchClicked -> sendEffect(HomeEffect.NavigateToSearch)
            HomeIntent.CartClicked -> onCartClicked()
            HomeIntent.SurveyBannerClicked -> sendEffect(HomeEffect.NavigateToSurvey)
            HomeIntent.SurveyBannerNeverShowAgain -> dismissSurveyPermanently()
            HomeIntent.SignUpPromptConfirmed -> {
                updateState { copy(showSignUpPrompt = false) }
                sendEffect(HomeEffect.NavigateToRegister)
            }
            HomeIntent.SignUpPromptDismissed -> updateState { copy(showSignUpPrompt = false) }
            HomeIntent.AiLoginPromptConfirmed -> {
                updateState { copy(showAiLoginPrompt = false) }
                sendEffect(HomeEffect.NavigateToRegister)
            }
            HomeIntent.AiLoginPromptDismissed -> updateState { copy(showAiLoginPrompt = false) }
            HomeIntent.SeeAllBrandsClicked -> sendEffect(HomeEffect.NavigateToAllBrands)
            HomeIntent.ViewAllCategoriesClicked -> sendEffect(HomeEffect.NavigateToAllCategories)
            HomeIntent.ViewAllJustForYouClicked -> viewModelScope.launch {
                sendEffect(
                    NavigateToProducts(
                        sourceType = "collection",
                        sourceId = "just-for-you",
                        sourceName = getString(Res.string.home_source_just_for_you),
                    ),
                )
            }
            HomeIntent.ViewAllTrendingClicked -> viewModelScope.launch {
                sendEffect(
                    NavigateToProducts(
                        sourceType = "collection",
                        sourceId = "trending",
                        sourceName = getString(Res.string.home_source_trending_now),
                    ),
                )
            }
            HomeIntent.ViewAllYourTrovesClicked -> viewModelScope.launch {
                sendEffect(
                    NavigateToProducts(
                        sourceType = "collection",
                        sourceId = "your-troves",
                        sourceName = getString(Res.string.home_source_your_troves),
                    ),
                )
            }
            is HomeIntent.AdClicked -> {
                val targetType = intent.ad.targetType
                val targetId = intent.ad.targetId
                val targetName = intent.ad.targetName
                
                if (intent.ad.buttonText == "Copy code") {
                    viewModelScope.launch {
                        sendEffect(ShowToast(getString(Res.string.home_copied_to_clipboard, intent.ad.titleTop)))
                    }
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
            HomeIntent.AiClicked -> onAiClicked()
        }
    }

    private fun loadHomeFeed(isRefresh: Boolean = false) {
        viewModelScope.launch {
            updateState {
                copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    errorMessage = null,
                )
            }

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
                    isRefreshing = false,
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

    private var yourTrovesJob: Job? = null

    private fun loadSurveyStatus() {
        // Not deduped here: the source only emits on account switch, banner dismissal,
        // or a fresh survey save — each of which should re-resolve the section.
        observeSurveyDone()
            .onEach { done ->
                updateState { copy(isSurveyDone = done) }
                if (done) {
                    loadSurveyRecommendations()
                } else {
                    // Signed out, or a different account signed in: drop the previous
                    // user's recommendations instead of leaving them on screen.
                    yourTrovesJob?.cancel()
                    updateState { copy(yourTroves = emptyList(), isLoadingYourTroves = false) }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadSurveyRecommendations() {
        yourTrovesJob?.cancel()
        yourTrovesJob = viewModelScope.launch {
            updateState { copy(isLoadingYourTroves = true) }
            val products = getSurveyRecommendations()
                .getOrElse(emptyList())
                .mapNotNull { it.toProduct() }

            updateState {
                copy(
                    yourTroves = products,
                    isLoadingYourTroves = false,
                )
            }
        }
    }

    /** Drops recommendations whose Shopify id isn't numeric — [Product.id] is a Long, and
     *  collapsing them all to 0L would duplicate keys in the LazyRow. */
    private fun SurveyRecommendedItem.toProduct(): Product? {
        val numericId = id.toLongOrNull() ?: return null
        return Product(
            id = numericId,
            title = title,
            vendor = vendor,
            price = price,
            imageUrl = imageUrl ?: "",
            status = status,
        )
    }

    private fun dismissSurveyPermanently() {
        viewModelScope.launch {
            updateState { copy(isSurveyDone = true) }
            dismissSurveyBanner()
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

    private fun onAiClicked() {
        viewModelScope.launch {
            if (isLoggedIn()) {
                sendEffect(NavigateToAiChat)
            } else {
                updateState { copy(showAiLoginPrompt = true) }
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
                    sendEffect(HomeEffect.ShowToast(getString(Res.string.favorites_added)))

                ToggleFavoriteResult.Removed ->
                    sendEffect(HomeEffect.ShowToast(getString(Res.string.favorites_removed)))

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
                    sendEffect(HomeEffect.ShowToast(getString(Res.string.favorites_update_failed)))
                }
            }
        }
    }
}
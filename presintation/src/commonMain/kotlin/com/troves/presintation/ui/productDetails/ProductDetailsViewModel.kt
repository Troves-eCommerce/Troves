package com.troves.presintation.ui.productDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Product
import com.troves.domain.usecase.cart.AddToCartUseCase
import com.troves.domain.usecase.cart.CartOperationResult
import com.troves.domain.usecase.details.GetProductByIdUseCase
import com.troves.domain.usecase.wishlist.IsProductFavoritedUseCase
import com.troves.domain.usecase.shared.ObserveConnectivityUseCase
import com.troves.domain.usecase.wishlist.ToggleFavoriteResult
import com.troves.domain.usecase.wishlist.ToggleFavoriteUseCase
import com.troves.domain.utils.connectivity.ConnectivityStatus
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import com.troves.domain.utils.Result
import com.troves.domain.entity.Review
import com.troves.domain.usecase.review.GetProductReviewsUseCase
import com.troves.domain.usecase.review.GetReviewerIdentityUseCase
import com.troves.domain.usecase.review.SubmitReviewResult
import com.troves.domain.usecase.review.SubmitReviewUseCase
import com.troves.presintation.ui.aichat.nowEpochMillis
import com.troves.presintation.ui.productDetails.models.ReviewDraft
import com.troves.presintation.ui.productDetails.models.ReviewUi

import org.jetbrains.compose.resources.getString
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.product_details_coming_soon
import troves.designsystem.generated.resources.product_details_out_of_stock
import troves.designsystem.generated.resources.product_details_review_error
import troves.designsystem.generated.resources.product_details_review_rating_required
import troves.designsystem.generated.resources.product_details_review_submitted
import troves.designsystem.generated.resources.product_details_select_option
import troves.designsystem.generated.resources.product_details_unavailable_combination

class ProductDetailsViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val isProductFavorite: IsProductFavoritedUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getCartStreamUseCase: com.troves.domain.usecase.cart.GetCartStreamUseCase,
    private val getProductReviews: GetProductReviewsUseCase,
    private val submitReviewUseCase: SubmitReviewUseCase,
    private val getReviewerIdentity: GetReviewerIdentityUseCase,
    private val observeConnectivity: ObserveConnectivityUseCase,
) : ViewModel(),
    StateHolder<ProductDetailUiState> by DefaultStateHolder(ProductDetailUiState()),
    EffectPublisher<ProductDetailsEffect> by DefaultEffectPublisher() {

    private var favoriteJob: Job? = null
    private var cartJob: Job? = null
    private var bannerDismissJob: Job? = null
    private var currentProductId: String = ""

    init {
        val online = observeConnectivity()
            .map { it == ConnectivityStatus.Available }
            .distinctUntilChanged()

        online
            .onEach { isOnline -> updateState { copy(isOffline = !isOnline) } }
            .launchIn(viewModelScope)

        online
            .drop(1)
            .onEach { isOnline ->
                if (isOnline && currentState.product == null && currentProductId.isNotEmpty()) {
                    fetchProduct(currentProductId)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: ProductDetailsIntent) {
        when (intent) {
            ProductDetailsIntent.OnBackClick ->
                sendEffect(ProductDetailsEffect.NavigateBack)

            ProductDetailsIntent.OnSizeGuide ->
                sendEffect(ProductDetailsEffect.NavigateBack)

            ProductDetailsIntent.OnAddToCart -> addCurrentProductToCart()

            is ProductDetailsIntent.OnFavoriteClick -> onFavoriteClick()

            is ProductDetailsIntent.OnOptionSelected ->
                updateState {
                    copy(selectedOptions = selectedOptions + (intent.optionName to intent.value))
                }

            is ProductDetailsIntent.Retry -> fetchProduct(intent.productId)
            is ProductDetailsIntent.Load -> fetchProduct(intent.productId)

            ProductDetailsIntent.OnViewCartClick ->
                sendEffect(ProductDetailsEffect.NavigateToCart)

            ProductDetailsIntent.OnDismissCartConfirmation -> {
                bannerDismissJob?.cancel()
                updateState { copy(showCartConfirmation = false) }
            }

            ProductDetailsIntent.OnSeeAllReviews ->
                updateState { copy(showReviewsSheet = true) }

            ProductDetailsIntent.OnDismissReviewsSheet ->
                updateState { copy(showReviewsSheet = false, showReviewEditor = false) }

            ProductDetailsIntent.OnOpenReviewEditor -> openReviewEditor()

            ProductDetailsIntent.OnDismissReviewEditor ->
                updateState { copy(showReviewEditor = false) }

            is ProductDetailsIntent.OnReviewDraftChanged ->
                updateState { copy(reviewDraft = intent.draft) }

            ProductDetailsIntent.OnSubmitReview -> submitReview()
        }
    }

    private fun fetchProduct(productId: String) {
        currentProductId = productId
        viewModelScope.launch {
            when (val product = getProductByIdUseCase(productId)) {
                Result.Loading -> updateState { copy(isLoading = true) }

                is Result.Error -> updateState {
                    copy(
                        isLoading = false,
                        errorMessage = product.throwable.message ?: "Unknown error",
                    )
                }

                is Result.Success<Product> -> {
                    val value = product.value
                    updateState {
                        copy(
                            isLoading = false,
                            product = value,
                            images = value.images,
                            title = value.title,
                            priceFormatted = value.price,
                            errorMessage = null,
                            description = value.description,
                        )
                    }
                    observeFavoriteStatus(productId)
                    observeCartStatus(productId)
                    loadReviews(productId)
                }
            }
        }
    }

    private fun loadReviews(productId: String) {
        viewModelScope.launch {
            updateState { copy(reviewsLoading = true) }
            when (val result = getProductReviews(productId)) {
                is Result.Success ->
                    applyReviews(productId, result.value.reviews, result.value.currentUserId)

                // Even if the read fails (e.g. Firestore rules not set yet) still show seed reviews.
                is Result.Error ->
                    applyReviews(productId, realReviews = emptyList(), currentUserId = null)

                Result.Loading -> {}
            }
        }
    }

    private fun applyReviews(
        productId: String,
        realReviews: List<Review>,
        currentUserId: String?,
    ) {
        val now = nowEpochMillis()
        // Merge real reviews with DEV-only seed reviews (never duplicating a real author).
        val seeds = FakeReviews.forProduct(productId, now)
            .filter { seed -> realReviews.none { it.userId == seed.userId } }
        val uiReviews = (realReviews + seeds)
            .sortedByDescending { it.createdAt }
            .map { it.toUi(currentUserId, now) }
        val mine = uiReviews.firstOrNull { it.isMine }
        updateState {
            copy(
                reviewsLoading = false,
                reviews = uiReviews,
                myReview = mine,
                reviewCount = uiReviews.size,
                rating = if (uiReviews.isEmpty()) 0
                else uiReviews.map { it.rating }.average().toInt(),
            )
        }
    }

    private fun openReviewEditor() {
        viewModelScope.launch {
            val draft = currentState.myReview?.let {
                ReviewDraft(
                    firstName = it.firstName,
                    lastName = it.lastName,
                    rating = it.rating,
                    comment = it.comment,
                )
            } ?: run {
                val identity = getReviewerIdentity()
                ReviewDraft(firstName = identity.firstName, lastName = identity.lastName)
            }
            updateState {
                copy(
                    reviewDraft = draft,
                    showReviewsSheet = true,
                    showReviewEditor = true,
                )
            }
        }
    }

    private fun submitReview() {
        val draft = currentState.reviewDraft
        if (draft.rating <= 0) {
            viewModelScope.launch {
                sendEffect(ProductDetailsEffect.ShowToast(getString(Res.string.product_details_review_rating_required)))
            }
            return
        }
        val productId = currentProductId
        updateState { copy(isSubmittingReview = true) }
        viewModelScope.launch {
            val result = submitReviewUseCase(
                productId = productId,
                firstName = draft.firstName,
                lastName = draft.lastName,
                rating = draft.rating,
                comment = draft.comment,
                createdAt = nowEpochMillis(),
            )
            updateState { copy(isSubmittingReview = false) }
            when (result) {
                SubmitReviewResult.Success -> {
                    updateState { copy(showReviewEditor = false) }
                    sendEffect(ProductDetailsEffect.ShowToast(getString(Res.string.product_details_review_submitted)))
                    loadReviews(productId)
                }

                SubmitReviewResult.RequiresLogin ->
                    sendEffect(ProductDetailsEffect.ShowLoginRequiredDialog(forReview = true))

                is SubmitReviewResult.Error ->
                    sendEffect(ProductDetailsEffect.ShowToast(getString(Res.string.product_details_review_error)))
            }
        }
    }

    private fun Review.toUi(currentUserId: String?, now: Long) = ReviewUi(
        id = id,
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        rating = rating,
        comment = comment,
        date = relativeTime(now, createdAt),
        isMine = currentUserId != null && userId == currentUserId,
    )

    private fun relativeTime(now: Long, then: Long): String {
        if (then <= 0L) return ""
        val diff = (now - then).coerceAtLeast(0L)
        val minutes = diff / 60_000
        val hours = diff / 3_600_000
        val days = diff / 86_400_000
        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            else -> "${days / 7}w ago"
        }
    }

    private fun observeFavoriteStatus(productId: String) {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            isProductFavorite(productId).collect { favorited ->
                updateState { copy(isFavorite = favorited) }
            }
        }
    }

    private fun observeCartStatus(productId: String) {
        cartJob?.cancel()
        cartJob = viewModelScope.launch {
            getCartStreamUseCase().collect { cart ->
                val quantity = cart?.lines
                    ?.filter { it.productId.toString() == productId }
                    ?.sumOf { it.quantity } ?: 0
                updateState { copy(productCartQuantity = quantity) }
            }
        }
    }

    private fun onFavoriteClick() {
        val product = currentState.product ?: run {
            sendEffect(ProductDetailsEffect.ShowToast("Couldn't update favorites"))
            return
        }
        val wasFavorite = currentState.isFavorite

        updateState { copy(isFavorite = !wasFavorite) }

        viewModelScope.launch {
            when (toggleFavoriteUseCase(product)) {
                ToggleFavoriteResult.Added ->
                    sendEffect(ProductDetailsEffect.ShowToast("Added to favorites successfully"))

                ToggleFavoriteResult.Removed ->
                    sendEffect(ProductDetailsEffect.ShowToast("Removed from favorites successfully"))

                ToggleFavoriteResult.RequiresLogin -> {
                    updateState { copy(isFavorite = wasFavorite) }
                    sendEffect(ProductDetailsEffect.ShowLoginRequiredDialog())
                }

                is ToggleFavoriteResult.Error -> {
                    updateState { copy(isFavorite = wasFavorite) }
                    sendEffect(ProductDetailsEffect.ShowToast("Couldn't update favorites"))
                }
            }
        }
    }

    private fun addCurrentProductToCart() {
        val state = currentState
        state.product ?: return
        val variant = state.selectedVariant
        if (variant == null || !variant.available) {
            viewModelScope.launch {
                val message = when {
                    variant == null -> {
                        val missing = state.displayOptions.firstOrNull { state.selectedOptions[it.name].isNullOrEmpty() }
                        if (missing != null) getString(Res.string.product_details_select_option, missing.name)
                        else getString(Res.string.product_details_unavailable_combination)
                    }
                    !variant.available -> getString(Res.string.product_details_out_of_stock)
                    else -> ""
                }
                sendEffect(ProductDetailsEffect.ShowToast(message))
            }
            return
        }
        updateState { copy(isAddingToCart = true) }
        viewModelScope.launch {
            val result = addToCartUseCase(variantId = variant.variantId, quantity = 1)
            updateState { copy(isAddingToCart = false) }
            when (result) {
                CartOperationResult.Success -> showCartConfirmationBar()

                CartOperationResult.RequiresLogin ->
                    sendEffect(ProductDetailsEffect.ShowLoginRequiredDialog())

                is CartOperationResult.Error ->
                    sendEffect(ProductDetailsEffect.ShowToast("Couldn't add to cart"))
            }
        }
    }

    private fun showCartConfirmationBar() {
        updateState { copy(showCartConfirmation = true) }
        bannerDismissJob?.cancel()
        bannerDismissJob = viewModelScope.launch {
            kotlinx.coroutines.delay(4000)
            updateState { copy(showCartConfirmation = false) }
        }
    }
}

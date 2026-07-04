package com.troves.data.repository

import com.troves.data.mapper.toBrand
import com.troves.data.mapper.toCategory
import com.troves.data.mapper.toDomain
import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.service.StorefrontApiService
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.data.util.applyAppLocale
import com.troves.domain.entity.Ad
import com.troves.domain.entity.Address
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Cart
import com.troves.domain.entity.Category
import com.troves.domain.entity.DiscountCode
import com.troves.domain.entity.Order
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.utils.Result
import com.troves.domain.utils.getOrElse
import com.troves.domain.utils.getOrNull
import com.troves.domain.utils.getOrThrow
import com.troves.domain.utils.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.coroutines.IO

class TrovesRepositoryImpl(
    private val remoteDataSource: RemoteDatasource,
    private val dataSource: TrovesPreferences,
    private val storefront: StorefrontApiService,
    private val trovesApiService: TrovesApiService,
    private val authenticationRepository: AuthenticationRepository,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TrovesRepository {

    // ── Catalogue ───────────────────────────────────────────────────────────

    override suspend fun getAllProducts(): Result<List<Product>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getAllProducts().map { response ->
                response.products
                    ?.map { it.toDomain() }
                    .orEmpty()
            }
        }
    }

    override suspend fun getProductsByQuery(queryMap: Map<String, String>): Result<List<Product>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getProductsByQuery(queryMap = queryMap).map { response ->
                response.products?.map { it.toDomain() }.orEmpty()
            }
        }
    }

    override suspend fun searchProducts(params: ProductSearchParams): Result<List<Product>> {
        return try {
            withContext(coroutineDispatcher) {
                var products =
                    remoteDataSource.searchProducts(params = params).getOrElse { emptyList() }
                if (!params.query.isNullOrBlank()) {
                    products = products.filter {
                        it.title.contains(params.query ?: "", ignoreCase = true)
                    }
                }
                Result.Success(products)
            }
        } catch (e: IOException) {
            Result.Error(e)
        }
    }

    override suspend fun getProductsByVendor(vendorName: String): Result<List<Product>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getProductsByVendor(vendorName = vendorName)
        }
    }

    override suspend fun getProductsByCollection(collectionId: Long): Result<List<Product>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getProductsByCollection(collectionId = collectionId.toString())
        }
    }

    override suspend fun getProductById(productId: String): Result<Product> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getProductById(productId = productId)
        }
    }

    override suspend fun getBrands(): Result<List<Brand>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getAllBrands().map { collection ->
                collection.smartCollections
                    ?.filterNotNull()
                    ?.map { it.toBrand() }
                    .orEmpty()
            }
        }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getCategory().map { response ->
                response.customCollections
                    ?.map { it.toCategory() }
                    .orEmpty()
            }
        }
    }

    override suspend fun getAds(): Result<List<Ad>> = Result.Success(FAKE_ADS)

    override suspend fun getDiscountCodes(): Result<List<DiscountCode>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getDiscountCodes()
        }
    }

    // ── Settings ────────────────────────────────────────────────────────────

    override val selectedLanguage: Flow<String> = dataSource.selectedLanguage
    override val themeMode: Flow<String> = dataSource.themeMode
    override val selectedCurrency: Flow<String> = dataSource.selectedCurrency

    override suspend fun setSelectedLanguage(language: String) {
        dataSource.setSelectedLanguage(language)

        withContext(Dispatchers.Main) {
            applyAppLocale(language)
        }
    }

    override suspend fun setThemeMode(mode: String) =
        dataSource.setThemeMode(mode)

    override suspend fun setSelectedCurrency(currency: String) =
        dataSource.setSelectedCurrency(currency)

    // ── Cart (Shopify = source of truth) ─────────────────────────────────────

    private val _cart = MutableStateFlow<Cart?>(null)
    override val cart: Flow<Cart?> = _cart.asStateFlow()

    override suspend fun addToCart(variantId: String, quantity: Int): Cart {
        val existingCartId = resolveCartId()
        val updated = if (existingCartId == null) {
            val email = authenticationRepository.getCurrentUserEmail()
            val token = dataSource.shopifyCustomerAccessTokenOrNull.first()
            val created = storefront.createCart(variantId, quantity, email, token).getOrThrow()
            persistCartId(created.cartId)
            created
        } else {
            storefront.addLines(existingCartId, variantId, quantity).getOrThrow()
        }
        _cart.value = updated
        return updated
    }

    override suspend fun updateQuantity(lineId: String, quantity: Int): Cart {
        val cartId = requireCartId()
        val updated = storefront.updateLineQuantity(cartId, lineId, quantity).getOrThrow()
        _cart.value = updated
        return updated
    }

    override suspend fun removeFromCart(lineId: String): Cart {
        val cartId = requireCartId()
        val updated = storefront.removeLines(cartId, listOf(lineId)).getOrThrow()
        _cart.value = updated
        return updated
    }

    override suspend fun removeAllItems(): Cart {
        val cartId = requireCartId()
        val lineIds = _cart.value?.lines?.map { it.lineId }.orEmpty()
        if (lineIds.isEmpty()) {
            // Nothing to remove; reconcile to the authoritative empty cart.
            val current = storefront.getCart(cartId).getOrThrow()
            if (current != null) _cart.value = current
            return current ?: throw IllegalStateException("No active cart")
        }
        val updated = storefront.removeLines(cartId, lineIds).getOrThrow()
        _cart.value = updated
        return updated
    }

    override suspend fun applyDiscountCodes(codes: List<String>): Cart {
        val cartId = requireCartId()
        val updated = storefront.updateDiscountCodes(cartId, codes).getOrThrow()
        _cart.value = updated
        return updated
    }

    override suspend fun refreshCart() {
        val cartId = resolveCartId() ?: run { _cart.value = null; return }
        val cart = storefront.getCart(cartId).getOrThrow()
        if (cart == null) {
            // Cart expired or already checked out — drop the pointer and start fresh next time.
            clearCartPointer()
            _cart.value = null
        } else {
            _cart.value = cart
        }
    }

    override suspend fun clearCart() {
        clearCartPointer()
        _cart.value = null
    }

    override suspend fun clearLocal() {
        // Keep the Firestore pointer so the cart rehydrates on next login; drop the local copy only.
        _cart.value = null
        dataSource.clearCartId()
    }

    /** In-memory → DataStore → Firestore, populating faster caches as it goes. */
    private suspend fun resolveCartId(): String? {
        _cart.value?.cartId?.let { return it }
        dataSource.cartId.first()?.let { return it }
        val userId = authenticationRepository.getCurrentUserId() ?: return null
        val remote = remoteDataSource.getUserCartId(userId)
        if (remote != null) dataSource.setCartId(remote)
        return remote
    }

    private suspend fun requireCartId(): String =
        resolveCartId() ?: throw IllegalStateException("No active cart")

    private suspend fun persistCartId(cartId: String) {
        dataSource.setCartId(cartId)
        authenticationRepository.getCurrentUserId()?.let { userId ->
            runCatching { remoteDataSource.setUserCartId(userId, cartId) }
        }
    }

    private suspend fun clearCartPointer() {
        dataSource.clearCartId()
        authenticationRepository.getCurrentUserId()?.let { userId ->
            runCatching { remoteDataSource.clearUserCartId(userId) }
        }
    }

    // ── Checkout / orders ─────────────────────────────────────────────────────

    override suspend fun getDefaultAddress(): Address? {
        val token = dataSource.shopifyCustomerAccessTokenOrNull.first() ?: return null
        return storefront.getDefaultAddress(token).getOrNull()
    }

    override suspend fun getOrders(): List<Order> {
        val token = dataSource.shopifyCustomerAccessTokenOrNull.first() ?: return emptyList()
        return storefront.getOrders(token).getOrElse { emptyList() }
    }

    override suspend fun getOrderById(orderId: String): Order? =
        storefront.getOrderById(orderId).getOrNull()

    override suspend fun placeCodOrder(cart: Cart, address: Address): String {
        val email = authenticationRepository.getCurrentUserEmail()
        val lineItems = cart.lines.map { it.variantId to it.quantity }
        return trovesApiService.createOrder(email, address, lineItems).getOrThrow()
    }

    override suspend fun attachAddressToCart(cartId: String, address: Address) {
        val token = dataSource.shopifyCustomerAccessTokenOrNull.first()
        val email = authenticationRepository.getCurrentUserEmail()
        storefront.updateCartBuyerIdentity(cartId, token, email)
        storefront.updateCartDeliveryAddress(cartId, address)
    }

    private companion object {
        val FAKE_ADS = listOf(
            Ad(
                id = 1,
                titleTop = "30% DISCOUNT",
                titleBottom = "Today special",
                description = "Get discount for every order, only valid for today.",
            ),
            Ad(
                id = 2,
                titleTop = "NEW ARRIVALS",
                titleBottom = "Summer 2026",
                description = "Fresh styles just landed. Explore the latest collection.",
            ),
            Ad(
                id = 3,
                titleTop = "FREE SHIPPING",
                titleBottom = "Orders over \$50",
                description = "Shop more, save more with free delivery on big orders.",
            ),
        )
    }
}
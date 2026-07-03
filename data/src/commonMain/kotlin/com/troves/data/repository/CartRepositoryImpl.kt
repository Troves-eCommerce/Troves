package com.troves.data.repository

import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.service.StorefrontApiService
import com.troves.domain.entity.Cart
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.CartRepository
import com.troves.domain.utils.getOrThrow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first


class CartRepositoryImpl(
    private val storefront: StorefrontApiService,
    private val remoteDatasource: RemoteDatasource,
    private val preferences: TrovesPreferences,
    private val authenticationRepository: AuthenticationRepository,
) : CartRepository {

    private val _cart = MutableStateFlow<Cart?>(null)
    override val cart: Flow<Cart?> = _cart.asStateFlow()

    override suspend fun addToCart(variantId: String, quantity: Int): Cart {
        val existingCartId = resolveCartId()
        val updated = if (existingCartId == null) {
            val email = authenticationRepository.getCurrentUserEmail()
            val token = preferences.shopifyCustomerAccessTokenOrNull.first()
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
        preferences.clearCartId()
    }

    // ── cart identity ───────────────────────────────────────────────────────

    /** In-memory → DataStore → Firestore, populating faster caches as it goes. */
    private suspend fun resolveCartId(): String? {
        _cart.value?.cartId?.let { return it }
        preferences.cartId.first()?.let { return it }
        val userId = authenticationRepository.getCurrentUserId() ?: return null
        val remote = remoteDatasource.getUserCartId(userId)
        if (remote != null) preferences.setCartId(remote)
        return remote
    }

    private suspend fun requireCartId(): String =
        resolveCartId() ?: throw IllegalStateException("No active cart")

    private suspend fun persistCartId(cartId: String) {
        preferences.setCartId(cartId)
        authenticationRepository.getCurrentUserId()?.let { userId ->
            runCatching { remoteDatasource.setUserCartId(userId, cartId) }
        }
    }

    private suspend fun clearCartPointer() {
        preferences.clearCartId()
        authenticationRepository.getCurrentUserId()?.let { userId ->
            runCatching { remoteDatasource.clearUserCartId(userId) }
        }
    }
}

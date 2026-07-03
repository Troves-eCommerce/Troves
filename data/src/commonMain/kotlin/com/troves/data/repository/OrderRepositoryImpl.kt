package com.troves.data.repository

import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.service.StorefrontApiService
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.domain.entity.Address
import com.troves.domain.entity.Cart
import com.troves.domain.entity.Order
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.OrderRepository
import com.troves.domain.utils.getOrElse
import com.troves.domain.utils.getOrNull
import com.troves.domain.utils.getOrThrow
import kotlinx.coroutines.flow.first

class OrderRepositoryImpl(
    private val storefront: StorefrontApiService,
    private val trovesApiService: TrovesApiService,
    private val preferences: TrovesPreferences,
    private val authenticationRepository: AuthenticationRepository,
) : OrderRepository {

    override suspend fun getDefaultAddress(): Address? {
        val token = preferences.shopifyCustomerAccessTokenOrNull.first() ?: return null
        return storefront.getDefaultAddress(token).getOrNull()
    }

    override suspend fun getOrders(): List<Order> {
        val token = preferences.shopifyCustomerAccessTokenOrNull.first() ?: return emptyList()
        return storefront.getOrders(token).getOrElse { emptyList() }
    }

    override suspend fun getOrderById(orderId: String): Order? =
        storefront.getOrderById(orderId).getOrNull()

    override suspend fun placeCodOrder(cart: Cart, address: Address): String {
        val email = authenticationRepository.getCurrentUserEmail()
        val lineItems = cart.lines.map { it.variantId to it.quantity }
        return trovesApiService.createOrder(email, address, lineItems).getOrThrow()
    }
}

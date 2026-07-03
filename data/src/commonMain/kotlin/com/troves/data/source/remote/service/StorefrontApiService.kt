package com.troves.data.source.remote.service

import com.troves.domain.entity.Address
import com.troves.domain.entity.Cart
import com.troves.domain.entity.Order
import com.troves.domain.utils.Result

data class ShopifyAccessToken(
    val accessToken: String,
    val expiresAt: String,
)


interface StorefrontApiService {

    // region customer auth
    suspend fun createCustomer(email: String, password: String): Result<Unit>
    suspend fun createAccessToken(email: String, password: String): Result<ShopifyAccessToken>
    suspend fun deleteAccessToken(accessToken: String): Result<Unit>
    // endregion

    // region cart
    suspend fun createCart(
        variantId: String,
        quantity: Int,
        email: String?,
        customerAccessToken: String?,
    ): Result<Cart>

    suspend fun addLines(cartId: String, variantId: String, quantity: Int): Result<Cart>
    suspend fun updateLineQuantity(cartId: String, lineId: String, quantity: Int): Result<Cart>
    suspend fun removeLines(cartId: String, lineIds: List<String>): Result<Cart>
    suspend fun updateDiscountCodes(cartId: String, codes: List<String>): Result<Cart>

    suspend fun getCart(cartId: String): Result<Cart?>
    // endregion

    // region customer address & orders (require a Storefront customer token)
    suspend fun getDefaultAddress(customerAccessToken: String): Result<Address?>

    suspend fun getCustomerAddresses(customerAccessToken: String): Result<List<Address>>
    suspend fun createCustomerAddress(customerAccessToken: String, address: Address): Result<Address>
    suspend fun updateCustomerAddress(customerAccessToken: String, id: String, address: Address): Result<Address>
    suspend fun deleteCustomerAddress(customerAccessToken: String, id: String): Result<Unit>
    suspend fun setDefaultCustomerAddress(customerAccessToken: String, id: String): Result<Unit>

    suspend fun updateCartBuyerIdentity(cartId: String, customerAccessToken: String?, email: String?): Result<Unit>

    suspend fun updateCartDeliveryAddress(cartId: String, address: Address): Result<Unit>

    suspend fun getOrders(customerAccessToken: String): Result<List<Order>>
    suspend fun getOrderById(orderId: String): Result<Order?>
    // endregion
}

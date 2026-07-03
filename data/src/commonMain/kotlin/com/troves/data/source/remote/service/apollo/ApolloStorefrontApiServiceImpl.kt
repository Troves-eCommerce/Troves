package com.troves.data.source.remote.service.apollo

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.troves.data.source.remote.service.ShopifyAccessToken
import com.troves.data.source.remote.service.StorefrontApiService
import com.troves.data.source.remote.service.apollo.graphql.storefront.AddCartLinesMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.CreateCartMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.CreateCustomerMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.CustomerAccessTokenCreateMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.CustomerAccessTokenDeleteMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.CustomerAddressCreateMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.CustomerAddressDeleteMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.CustomerAddressUpdateMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.CustomerAddressesQuery
import com.troves.data.source.remote.service.apollo.graphql.storefront.CustomerDefaultAddressUpdateMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.GetCartQuery
import com.troves.data.source.remote.service.apollo.graphql.storefront.UpdateBuyerIdentityMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.UpdateDeliveryAddressMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.GetOrderByIdQuery
import com.troves.data.source.remote.service.apollo.graphql.storefront.GetOrdersQuery
import com.troves.data.source.remote.service.apollo.graphql.storefront.RemoveCartLinesMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.UpdateCartLinesMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.UpdateDiscountCodesMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.type.CartBuyerIdentityInput
import com.troves.data.source.remote.service.apollo.graphql.storefront.type.CartInput
import com.troves.data.source.remote.service.apollo.graphql.storefront.type.CartLineInput
import com.troves.data.source.remote.service.apollo.graphql.storefront.type.CartLineUpdateInput
import com.troves.data.source.remote.service.apollo.graphql.storefront.type.CustomerCreateInput
import com.troves.data.source.remote.service.apollo.mapper.toCartSelectableAddressInput
import com.troves.data.source.remote.service.apollo.mapper.toDomainCart
import com.troves.data.source.remote.service.apollo.mapper.toDomainAddress
import com.troves.data.source.remote.service.apollo.mapper.toDomainLineItem
import com.troves.data.source.remote.service.apollo.mapper.toDomainOrder
import com.troves.data.source.remote.service.apollo.mapper.toStorefrontMailingAddressInput
import com.troves.data.source.remote.service.apollo.util.runMutation
import com.troves.data.source.remote.service.apollo.util.runQuery
import com.troves.data.source.remote.service.apollo.util.toVariantGid
import com.troves.domain.entity.Address
import com.troves.domain.entity.Cart
import com.troves.domain.entity.Order
import com.troves.domain.utils.Result

class ApolloStorefrontApiServiceImpl(
    private val apolloClient: ApolloClient,
) : StorefrontApiService {

    // region customer auth
    override suspend fun createCustomer(email: String, password: String): Result<Unit> =
        apolloClient.runMutation(
            CreateCustomerMutation(input = CustomerCreateInput(email = email, password = password))
        ) { data ->
            data.customerCreate?.customerUserErrors?.firstOrNull()?.let { error(it.message) }
            Unit
        }

    override suspend fun createAccessToken(email: String, password: String): Result<ShopifyAccessToken> =
        apolloClient.runMutation(
            CustomerAccessTokenCreateMutation(email = email, password = password)
        ) { data ->
            val payload = data.customerAccessTokenCreate
            payload?.customerUserErrors?.firstOrNull()?.let { error(it.message) }
            val token = payload?.customerAccessToken
                ?: error("Shopify did not return a customer access token")
            ShopifyAccessToken(accessToken = token.accessToken, expiresAt = token.expiresAt.toString())
        }

    override suspend fun deleteAccessToken(accessToken: String): Result<Unit> =
        apolloClient.runMutation(
            CustomerAccessTokenDeleteMutation(customerAccessToken = accessToken)
        ) { data ->
            data.customerAccessTokenDelete?.userErrors?.firstOrNull()?.let { error(it.message) }
            Unit
        }
    // endregion

    // region cart
    override suspend fun createCart(
        variantId: String,
        quantity: Int,
        email: String?,
        customerAccessToken: String?,
    ): Result<Cart> {
        val buyerIdentity = if (email != null || customerAccessToken != null) {
            Optional.present(
                CartBuyerIdentityInput(
                    email = Optional.presentIfNotNull(email),
                    customerAccessToken = Optional.presentIfNotNull(customerAccessToken),
                )
            )
        } else {
            Optional.Absent
        }
        val input = CartInput(
            lines = Optional.present(
                listOf(CartLineInput(merchandiseId = variantId.toVariantGid(), quantity = Optional.present(quantity)))
            ),
            buyerIdentity = buyerIdentity,
        )
        return apolloClient.runMutation(CreateCartMutation(input = Optional.present(input))) { data ->
            val payload = data.cartCreate
            payload?.userErrors?.firstOrNull()?.let { error(it.message) }
            payload?.cart?.cartFields?.toDomainCart() ?: error("Cart creation returned no cart")
        }
    }

    override suspend fun addLines(cartId: String, variantId: String, quantity: Int): Result<Cart> =
        apolloClient.runMutation(
            AddCartLinesMutation(
                cartId = cartId,
                lines = listOf(CartLineInput(merchandiseId = variantId.toVariantGid(), quantity = Optional.present(quantity))),
            )
        ) { data ->
            val payload = data.cartLinesAdd
            payload?.userErrors?.firstOrNull()?.let { error(it.message) }
            payload?.cart?.cartFields?.toDomainCart() ?: error("Add to cart returned no cart")
        }

    override suspend fun updateLineQuantity(cartId: String, lineId: String, quantity: Int): Result<Cart> =
        apolloClient.runMutation(
            UpdateCartLinesMutation(
                cartId = cartId,
                lines = listOf(CartLineUpdateInput(id = lineId, quantity = Optional.present(quantity))),
            )
        ) { data ->
            val payload = data.cartLinesUpdate
            payload?.userErrors?.firstOrNull()?.let { error(it.message) }
            payload?.cart?.cartFields?.toDomainCart() ?: error("Update cart returned no cart")
        }

    override suspend fun removeLines(cartId: String, lineIds: List<String>): Result<Cart> =
        apolloClient.runMutation(
            RemoveCartLinesMutation(cartId = cartId, lineIds = lineIds)
        ) { data ->
            val payload = data.cartLinesRemove
            payload?.userErrors?.firstOrNull()?.let { error(it.message) }
            payload?.cart?.cartFields?.toDomainCart() ?: error("Remove from cart returned no cart")
        }

    override suspend fun updateDiscountCodes(cartId: String, codes: List<String>): Result<Cart> =
        apolloClient.runMutation(
            UpdateDiscountCodesMutation(cartId = cartId, discountCodes = codes)
        ) { data ->
            val payload = data.cartDiscountCodesUpdate
            payload?.userErrors?.firstOrNull()?.let { error(it.message) }
            payload?.cart?.cartFields?.toDomainCart() ?: error("Apply discount returned no cart")
        }

    override suspend fun getCart(cartId: String): Result<Cart?> =
        apolloClient.runQuery(GetCartQuery(cartId = cartId)) { data ->
            data.cart?.cartFields?.toDomainCart()
        }
    // endregion

    // region customer address & orders
    override suspend fun getDefaultAddress(customerAccessToken: String): Result<Address?> =
        getCustomerAddresses(customerAccessToken).let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.value.firstOrNull { it.isDefault })
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        }

    override suspend fun getCustomerAddresses(customerAccessToken: String): Result<List<Address>> =
        apolloClient.runQuery(
            CustomerAddressesQuery(customerAccessToken = customerAccessToken, first = 30)
        ) { data ->
            val defaultId = data.customer?.defaultAddress?.id
            data.customer?.addresses?.nodes.orEmpty().map { node ->
                node.addressFields.toDomainAddress(isDefault = node.addressFields.id == defaultId)
            }
        }

    override suspend fun createCustomerAddress(customerAccessToken: String, address: Address): Result<Address> =
        apolloClient.runMutation(
            CustomerAddressCreateMutation(
                customerAccessToken = customerAccessToken,
                address = address.toStorefrontMailingAddressInput(),
            )
        ) { data ->
            val payload = data.customerAddressCreate
            payload?.customerUserErrors?.firstOrNull()?.let { error(it.message) }
            payload?.customerAddress?.addressFields?.toDomainAddress()
                ?: error("Address creation returned no address")
        }

    override suspend fun updateCustomerAddress(customerAccessToken: String, id: String, address: Address): Result<Address> =
        apolloClient.runMutation(
            CustomerAddressUpdateMutation(
                customerAccessToken = customerAccessToken,
                id = id,
                address = address.toStorefrontMailingAddressInput(),
            )
        ) { data ->
            val payload = data.customerAddressUpdate
            payload?.customerUserErrors?.firstOrNull()?.let { error(it.message) }
            payload?.customerAddress?.addressFields?.toDomainAddress()
                ?: error("Address update returned no address")
        }

    override suspend fun deleteCustomerAddress(customerAccessToken: String, id: String): Result<Unit> =
        apolloClient.runMutation(
            CustomerAddressDeleteMutation(customerAccessToken = customerAccessToken, id = id)
        ) { data ->
            data.customerAddressDelete?.customerUserErrors?.firstOrNull()?.let { error(it.message) }
            Unit
        }

    override suspend fun setDefaultCustomerAddress(customerAccessToken: String, id: String): Result<Unit> =
        apolloClient.runMutation(
            CustomerDefaultAddressUpdateMutation(customerAccessToken = customerAccessToken, addressId = id)
        ) { data ->
            data.customerDefaultAddressUpdate?.customerUserErrors?.firstOrNull()?.let { error(it.message) }
            Unit
        }

    override suspend fun updateCartBuyerIdentity(cartId: String, customerAccessToken: String?, email: String?): Result<Unit> =
        apolloClient.runMutation(
            UpdateBuyerIdentityMutation(
                cartId = cartId,
                buyerIdentity = CartBuyerIdentityInput(
                    email = Optional.presentIfNotNull(email),
                    customerAccessToken = Optional.presentIfNotNull(customerAccessToken),
                ),
            )
        ) { data ->
            data.cartBuyerIdentityUpdate?.userErrors?.firstOrNull()?.let { error(it.message) }
            Unit
        }

    override suspend fun updateCartDeliveryAddress(cartId: String, address: Address): Result<Unit> =
        apolloClient.runMutation(
            UpdateDeliveryAddressMutation(
                cartId = cartId,
                addresses = listOf(address.toCartSelectableAddressInput()),
            )
        ) { data ->
            data.cartDeliveryAddressesAdd?.userErrors?.firstOrNull()?.let { error(it.message) }
            Unit
        }

    override suspend fun getOrders(customerAccessToken: String): Result<List<Order>> =
        apolloClient.runQuery(
            GetOrdersQuery(customerAccessToken = customerAccessToken)
        ) { data ->
            data.customer?.orders?.nodes?.map { it.orderCoreFields.toDomainOrder() }.orEmpty()
        }

    override suspend fun getOrderById(orderId: String): Result<Order?> =
        apolloClient.runQuery(GetOrderByIdQuery(id = orderId)) { data ->
            val order = data.node?.onOrder ?: return@runQuery null
            order.orderCoreFields.toDomainOrder(
                lineItems = order.lineItems.nodes.map { it.orderLineItemFields.toDomainLineItem() }
            )
        }
    // endregion
}

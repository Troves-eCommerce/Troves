package com.troves.data.source.remote.service.apollo.mapper

import com.troves.data.source.remote.service.apollo.graphql.storefront.fragment.AddressFields
import com.troves.data.source.remote.service.apollo.graphql.storefront.fragment.OrderCoreFields
import com.troves.data.source.remote.service.apollo.graphql.storefront.fragment.OrderLineItemFields
import com.troves.data.source.remote.service.apollo.graphql.storefront.GetOrdersQuery
import com.troves.domain.entity.Address
import com.troves.domain.entity.CartMoney
import com.troves.domain.entity.Order
import com.troves.domain.entity.OrderLineItem
import com.troves.domain.entity.OrderSummary

internal fun AddressFields.toDomainAddress(isDefault: Boolean = false): Address = Address(
    id = id,
    address1 = address1,
    address2 = address2,
    city = city,
    province = province,
    provinceCode = provinceCode,
    country = country,
    countryCode = countryCodeV2?.rawValue,
    zip = zip,
    phone = phone,
    firstName = firstName,
    lastName = lastName,
    company = company,
    isDefault = isDefault,
)

internal fun OrderCoreFields.toDomainOrder(
    lineItems: List<OrderLineItem> = emptyList(),
): Order = Order(
    id = id,
    number = orderNumber,
    name = name,
    processedAt = processedAt.toString(),
    financialStatus = financialStatus?.rawValue,
    fulfillmentStatus = fulfillmentStatus.rawValue,
    subtotal = CartMoney(
        amount = currentSubtotalPrice.moneyFields.amount.toString(),
        currencyCode = currentSubtotalPrice.moneyFields.currencyCode.rawValue,
    ),
    total = CartMoney(
        amount = currentTotalPrice.moneyFields.amount.toString(),
        currencyCode = currentTotalPrice.moneyFields.currencyCode.rawValue,
    ),
    shippingAddress = shippingAddress?.addressFields?.toDomainAddress(),
    statusUrl = statusUrl.toString(),
    shipping = CartMoney(
        amount = currentTotalShippingPrice.moneyFields.amount.toString(),
        currencyCode = currentTotalShippingPrice.moneyFields.currencyCode.rawValue,
    ),
    tax = CartMoney(
        amount = currentTotalTax.moneyFields.amount.toString(),
        currencyCode = currentTotalTax.moneyFields.currencyCode.rawValue,
    ),
    lineItems = lineItems,
)

internal fun GetOrdersQuery.Node.toDomainOrderSummary(): OrderSummary = OrderSummary(
    id = id,
    number = orderNumber,
    processedAt = processedAt.toString(),
    financialStatus = financialStatus?.rawValue,
    fulfillmentStatus = fulfillmentStatus.rawValue,
    total = CartMoney(
        amount = currentTotalPrice.amount.toString(),
        currencyCode = currentTotalPrice.currencyCode.rawValue,
    ),
    itemCount = lineItems.nodes.sumOf { it.quantity },
    thumbnailUrl = lineItems.nodes.firstOrNull()?.variant?.image?.url?.toString(),
)

internal fun OrderLineItemFields.toDomainLineItem(): OrderLineItem = OrderLineItem(
    title = title,
    variantTitle = variant?.title,
    imageUrl = variant?.image?.url?.toString(),
    quantity = quantity,
    price = CartMoney(
        amount = originalTotalPrice.moneyFields.amount.toString(),
        currencyCode = originalTotalPrice.moneyFields.currencyCode.rawValue,
    ),
)

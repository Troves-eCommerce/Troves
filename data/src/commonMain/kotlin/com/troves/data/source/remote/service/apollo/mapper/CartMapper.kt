package com.troves.data.source.remote.service.apollo.mapper

import com.troves.data.source.remote.service.apollo.graphql.storefront.fragment.CartFields
import com.troves.data.source.remote.service.apollo.util.gidToLong
import com.troves.domain.entity.AppliedDiscount
import com.troves.domain.entity.Cart
import com.troves.domain.entity.CartLine
import com.troves.domain.entity.CartMoney

internal fun CartFields.toDomainCart(): Cart = Cart(
    cartId = id,
    checkoutUrl = checkoutUrl.toString(),
    email = buyerIdentity.email,
    totalQuantity = totalQuantity,
    subtotal = CartMoney(
        amount = cost.subtotalAmount.moneyFields.amount.toString(),
        currencyCode = cost.subtotalAmount.moneyFields.currencyCode.rawValue,
    ),
    total = CartMoney(
        amount = cost.totalAmount.moneyFields.amount.toString(),
        currencyCode = cost.totalAmount.moneyFields.currencyCode.rawValue,
    ),
    discountCodes = discountCodes.map { AppliedDiscount(code = it.code, applicable = it.applicable) },
    lines = lines.nodes.mapNotNull { node ->
        val variant = node.merchandise.onProductVariant ?: return@mapNotNull null
        CartLine(
            lineId = node.id,
            variantId = variant.id,
            productId = variant.product.id.gidToLong() ?: 0L,
            productTitle = variant.product.title,
            variantTitle = variant.title,
            imageUrl = variant.image?.url?.toString() ?: "",
            unitPrice = node.cost.amountPerQuantity.moneyFields.amount.toString(),
            currencyCode = node.cost.amountPerQuantity.moneyFields.currencyCode.rawValue,
            quantity = node.quantity,
            maxQuantity = variant.quantityAvailable?.takeIf { variant.availableForSale },
        )
    },
)

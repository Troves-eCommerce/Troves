package com.troves.domain.entity


data class Cart(
    val cartId: String,
    val checkoutUrl: String,
    val email: String?,
    val totalQuantity: Int,
    val subtotal: CartMoney,
    val total: CartMoney,
    val discountCodes: List<AppliedDiscount>,
    val lines: List<CartLine>,
)

data class CartMoney(
    val amount: String,
    val currencyCode: String,
)

data class AppliedDiscount(
    val code: String,
    val applicable: Boolean,
)

/**
 * A single line in the cart. Keyed by [lineId] (the Shopify cart-line GID) so that two variants of
 * the same product are distinct lines. [variantId] is the merchandise GID used by cart mutations.
 */
data class CartLine(
    val lineId: String,
    val variantId: String,
    val productId: Long,
    val productTitle: String,
    val variantTitle: String,
    val imageUrl: String,
    val unitPrice: String,
    val currencyCode: String,
    val quantity: Int,
    /** Remaining purchasable stock for this variant, or null when Shopify does not track it. */
    val maxQuantity: Int?,
)

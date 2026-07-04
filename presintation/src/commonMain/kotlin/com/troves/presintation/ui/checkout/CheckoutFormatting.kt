package com.troves.presintation.ui.checkout

import com.troves.domain.entity.CartMoney
import kotlin.math.roundToLong



internal fun format(money: CartMoney?): String {
    if (money == null) return "$0.00"
    return if (money.currencyCode == "USD") "$${money.amount}" else "${money.amount} ${money.currencyCode}"
}

internal fun formatMoney(amount: Double?, currencyCode: String?): String {
    if (amount == null) return if (currencyCode == "USD" || currencyCode == null) "$0.00" else "0.00 $currencyCode"
    val cents = (amount * 100).roundToLong()
    val whole = cents / 100
    val frac = (cents % 100).let { if (it < 0) -it else it }
    val fracStr = if (frac < 10) "0$frac" else "$frac"
    val body = "$whole.$fracStr"
    return if (currencyCode == "USD" || currencyCode == null) "$$body" else "$body $currencyCode"
}

internal fun lineTotal(unitPrice: String, quantity: Int, currencyCode: String): String {
    val unit = unitPrice.toDoubleOrNull() ?: return formatMoney(null, currencyCode)
    return formatMoney(unit * quantity, currencyCode)
}

internal fun savings(subtotal: CartMoney?, total: CartMoney?): String? =
    savings(subtotal?.amount, total?.amount, subtotal?.currencyCode)

internal fun savings(subtotalAmount: String?, totalAmount: String?, currencyCode: String?): String? {
    val s = subtotalAmount?.toDoubleOrNull() ?: return null
    val t = totalAmount?.toDoubleOrNull() ?: return null
    return savings(s, t, currencyCode)
}

internal fun savings(subtotalAmount: Double?, totalAmount: Double?, currencyCode: String?): String? {
    if (subtotalAmount == null || totalAmount == null) return null
    val diff = subtotalAmount - totalAmount
    if (diff <= 0.009) return null
    return "- " + formatMoney(diff, currencyCode)
}

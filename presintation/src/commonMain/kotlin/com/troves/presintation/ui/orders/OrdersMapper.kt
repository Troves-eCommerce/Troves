package com.troves.presintation.ui.orders

import com.troves.domain.entity.CartMoney
import com.troves.domain.entity.OrderSummary

fun OrderSummary.toUi() = OrderUi(
    id = id,
    name = "#$number",
    date = processedAt.take(10),
    status = listOfNotNull(financialStatus, fulfillmentStatus)
        .filter { it.isNotBlank() }
        .joinToString(" · ") { it.lowercase().replaceFirstChar(Char::uppercase) }
        .ifBlank { "Processing" },
    totalFormatted = format(total),
    itemCount = itemCount,
    imageUrl = thumbnailUrl,
)

private fun format(money: CartMoney): String =
    if (money.currencyCode == "USD") "$${money.amount}" else "${money.amount} ${money.currencyCode}"

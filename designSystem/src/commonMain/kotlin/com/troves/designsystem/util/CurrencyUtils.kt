package com.troves.designsystem.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.troves.domain.entity.ExchangeRate

data class CurrencyState(
    val selectedCurrency: String = "EGP",
    val exchangeRate: ExchangeRate? = null
)

val LocalCurrency = compositionLocalOf { CurrencyState() }

@Composable
@ReadOnlyComposable
fun formatPrice(price: String): String {
    val cleanPrice = price.replace(Regex("[^0-9.-]"), "")
    val isNegative = price.contains("-")
    val numericPrice = cleanPrice.toDoubleOrNull() ?: 0.0
    val formatted = formatPrice(if (isNegative && numericPrice > 0) -numericPrice else numericPrice)
    return formatted
}

@Composable
@ReadOnlyComposable
fun formatPrice(price: Double): String {
    val currencyState = LocalCurrency.current
    val rates = currencyState.exchangeRate?.rates
    val targetCurrency = currencyState.selectedCurrency
    
    val isNegative = price < 0
    val absPrice = if (isNegative) -price else price

    val convertedPrice = if (rates != null && targetCurrency != "EGP") {
        val rate = rates[targetCurrency] ?: 1.0
        absPrice * rate
    } else {
        absPrice
    }

    val symbol = when (targetCurrency) {
        "EGP" -> "EGP"
        "GBP" -> "£"
        "JPY" -> "¥"
        "EUR" -> "€"
        "USD" -> "$"
        else -> targetCurrency
    }

    val rounded = ((convertedPrice + 0.005) * 100).toLong() / 100.0
    val parts = rounded.toString().split(".")
    val decimals = if (parts.size > 1) parts[1].padEnd(2, '0').take(2) else "00"
    
    val result = if (targetCurrency == "EGP") {
        "${parts[0]}.$decimals $symbol"
    } else {
        "$symbol${parts[0]}.$decimals"
    }

    return if (isNegative) "- $result" else result
}

package com.troves.presintation.ui.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.ExchangeRate
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.presintation.generated.resources.Res as ResP
import troves.presintation.generated.resources.*
import troves.designsystem.generated.resources.ic_arrow
import troves.designsystem.generated.resources.ic_flag_europ
import troves.designsystem.generated.resources.ic_flag_saudi
import troves.designsystem.generated.resources.ic_flag_us
import troves.designsystem.generated.resources.ic_live_rate
import troves.designsystem.generated.resources.ic_flag_egypt
import com.troves.designsystem.util.autoMirror

data class CurrencyInfo(
    val code: String,
    val name: String,
    val symbol: String,
    val flag: DrawableResource? = null
)

@Composable
fun LiveRatesRow(
    exchangeRate: ExchangeRate?,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val egpName = stringResource(ResP.string.currency_egp)
    val usdName = stringResource(ResP.string.currency_usd)
    val eurName = stringResource(ResP.string.currency_eur)
    val sarName = stringResource(ResP.string.currency_sar)

    val currencies = remember(egpName, usdName, eurName, sarName) {
        listOf(
            CurrencyInfo("EGP", egpName, "EGP", Res.drawable.ic_flag_egypt),
            CurrencyInfo("USD", usdName, "$ USD" , Res.drawable.ic_flag_us),
            CurrencyInfo("EUR", eurName, "€ EUR", Res.drawable.ic_flag_europ),
            CurrencyInfo("SAR", sarName, "SAR", Res.drawable.ic_flag_saudi),
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_live_rate),
            contentDescription = null,
            tint = Theme.colors.secondaryFont,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(ResP.string.profile_currency_exchange_rates),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.SemiBold
                )
            )

            val currentCurrency = currencies.find { it.code == selectedCurrency } ?: currencies.first()
            var rateText = stringResource(ResP.string.profile_currency_desc)

            if (exchangeRate != null) {
                if (selectedCurrency == exchangeRate.base) {
                    rateText = stringResource(ResP.string.profile_base_currency)
                } else {
                    val rate = exchangeRate.rates[selectedCurrency]
                    if (rate != null) {
                        rateText = stringResource(
                            ResP.string.profile_exchange_rate_format,
                            exchangeRate.base,
                            rate.roundTo(2).toString(),
                            currentCurrency.code
                        )
                    }
                }
            }

            Text(
                text = rateText,
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
            )
        }

        Icon(
            painter = painterResource(Res.drawable.ic_arrow),
            contentDescription = null,
            tint = Theme.colors.hint,
            modifier = Modifier
                .size(16.dp)
                .autoMirror()
        )
    }

    if (showDialog) {
        CurrencySelectionDialog(
            currencies = currencies,
            selectedCurrencyCode = selectedCurrency,
            exchangeRate = exchangeRate,
            onCurrencySelected = {
                onCurrencySelected(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun CurrencySelectionDialog(
    currencies: List<CurrencyInfo>,
    selectedCurrencyCode: String,
    exchangeRate: ExchangeRate?,
    onCurrencySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempSelectedCode by remember { mutableStateOf(selectedCurrencyCode) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Theme.colors.surface)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(ResP.string.profile_select_currency),
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(ResP.string.profile_select_currency_subtitle),
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont)
                )
                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(currencies) { currency ->
                        CurrencyItem(
                            currency = currency,
                            isSelected = tempSelectedCode == currency.code,
                            rate = exchangeRate?.rates?.get(currency.code),
                            baseCurrency = exchangeRate?.base,
                            onClick = { tempSelectedCode = currency.code }
                        )
                        if (currencies.last() != currency) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 4.dp),
                                color = Theme.colors.backGround,
                                thickness = 0.5.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onCurrencySelected(tempSelectedCode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Theme.colors.primary
                    )
                ) {
                    Text(
                        text = stringResource(ResP.string.profile_save_currency),
                        style = Theme.typography.body.large.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CurrencyItem(
    currency: CurrencyInfo,
    isSelected: Boolean,
    rate: Double? = null,
    baseCurrency: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currency.flag != null) {
            Image(
                painter = painterResource(currency.flag),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.backGround),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currency.code.take(2),
                    style = Theme.typography.body.small.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(currency.code)
                    }
                    append(" - ${currency.name}")
                },
                style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont)
            )
            if (rate != null && baseCurrency != null && currency.code != baseCurrency) {
                Text(
                    text = stringResource(
                        ResP.string.profile_exchange_rate_format,
                        baseCurrency,
                        rate.toString(),
                        currency.code
                    ),
                    style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
                )
            }
        }

        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Theme.colors.tint,
                unselectedColor = Theme.colors.hint
            )
        )
    }
}

fun Double.roundTo(decimals: Int = 2): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}
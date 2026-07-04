package com.troves.presintation.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_live_rate


import com.troves.domain.entity.ExchangeRate

@Composable
fun LiveRatesRow(
    exchangeRate: ExchangeRate?,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_live_rate),
                contentDescription = null,
                tint = Theme.colors.primary
            )
            Text(
                text = "Live Rates",
                style = Theme.typography.body.large.copy(color = Theme.colors.primaryFont)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Theme.colors.backGround)
                .padding(4.dp)
        ) {
            val currencies = listOf("GBP" to "£ GBP", "JPY" to "¥ JPY", "EUR" to "€ EUR")
            currencies.forEach { (code, label) ->
                val isSelected = selectedCurrency == code
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Theme.colors.surface else Color.Transparent)
                        .clickable { onCurrencySelected(code) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = if (isSelected) {
                            Theme.typography.body.medium.copy(color = Theme.colors.primary)
                        } else {
                            Theme.typography.hint.medium.copy(color = Theme.colors.secondaryFont)
                        }
                    )
                }
            }
        }

        if (exchangeRate != null) {
            val rate = exchangeRate.rates[selectedCurrency]
            if (rate != null) {
                Text(
                    text = "1 ${exchangeRate.base} = $rate $selectedCurrency",
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
package com.troves.designsystem.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme


@Composable
fun OrderSummaryInfoCard(
    subtotalLabel: String,
    subtotalFormatted: String,
    totalLabel: String,
    totalFormatted: String,
    modifier: Modifier = Modifier,
    discountLabel: String? = null,
    discountValueFormatted: String? = null,
    shippingLabel: String? = null,
    shippingFormatted: String? = null,
    taxLabel: String? = null,
    taxFormatted: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
          .shadow(elevation = 0.2.dp, shape = Theme.shapes.medium, clip = false)
            .clip(Theme.shapes.medium)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.onPrimary, Theme.shapes.medium)
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        SummaryRow(
            label = subtotalLabel,
            value = subtotalFormatted,
            labelColor = Theme.colors.primaryFont,
            valueColor = Theme.colors.primaryFont,
        )

        if (discountLabel != null && discountValueFormatted != null) {
            SummaryRow(
                label = discountLabel,
                value = discountValueFormatted,
                labelColor = Theme.colors.success,
                valueColor = Theme.colors.success,
            )
        }

        if (shippingLabel != null && shippingFormatted != null) {
            SummaryRow(
                label = shippingLabel,
                value = shippingFormatted,
                labelColor = Theme.colors.primaryFont,
                valueColor = Theme.colors.primaryFont,
            )
        }

        if (taxLabel != null && taxFormatted != null) {
            SummaryRow(
                label = taxLabel,
                value = taxFormatted,
                labelColor = Theme.colors.primaryFont,
                valueColor = Theme.colors.primaryFont,
            )
        }


        SummaryRow(
            label = totalLabel,
            value = totalFormatted,
            labelColor = Theme.colors.primaryFont,
            valueColor = Theme.colors.primaryFont,
            textStyle = Theme.typography.body.large,
            fontWeight = FontWeight.Bold,
        )
    }
}


@Composable
private fun SummaryRow(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    textStyle: TextStyle = Theme.typography.body.medium,
    fontWeight: FontWeight = FontWeight.Medium,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        BasicText(
            text = label,
            style = textStyle.copy(color = labelColor, fontWeight = fontWeight),
        )
        BasicText(
            text = value,
            style = textStyle.copy(color = valueColor, fontWeight = fontWeight),
        )
    }
}

@Preview
@Composable
private fun OrderSummaryInfoCardPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.backGround)
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        ) {
            OrderSummaryInfoCard(
                subtotalLabel = "Subtotal (3 items)",
                subtotalFormatted = "\$246.00",
                totalLabel = "Total",
                totalFormatted = "\$221.40",
                discountLabel = "Discount (WELCOME10)",
                discountValueFormatted = "- \$24.60",
            )
            OrderSummaryInfoCard(
                subtotalLabel = "Subtotal (2 items)",
                subtotalFormatted = "\$180.00",
                totalLabel = "Total",
                totalFormatted = "\$180.00",
            )
        }
    }
}

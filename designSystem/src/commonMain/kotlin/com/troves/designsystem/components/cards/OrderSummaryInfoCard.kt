package com.troves.designsystem.components.cards

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme


@Composable
fun OrderSummaryInfoCard(
    itemCount: Int,
    subtotalFormatted: String,
    totalFormatted: String,
    modifier: Modifier = Modifier,
    discountCode: String? = null,
    discountValueFormatted: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.medium)
            .background(Theme.colors.surface)
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        SummaryRow(
            label = "Subtotal ($itemCount items)",
            value = subtotalFormatted,
            labelColor = Theme.colors.primaryFont,
            valueColor = Theme.colors.primaryFont,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Theme.spacing.extraSmall)
                .height(1.dp)
                .background(Theme.colors.primary),
        )

        if (discountCode != null && discountValueFormatted != null) {
            SummaryRow(
                label = "Discount ($discountCode)",
                value = discountValueFormatted,
                labelColor = Theme.colors.success,
                valueColor = Theme.colors.success,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Theme.spacing.extraSmall)
                .height(1.dp)
                .background(Theme.colors.primary),
        )

        SummaryRow(
            label = "Total",
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
                itemCount = 3,
                subtotalFormatted = "\$246.00",
                totalFormatted = "\$221.40",
                discountCode = "WELCOME10",
                discountValueFormatted = "- \$24.60",
            )
            OrderSummaryInfoCard(
                itemCount = 2,
                subtotalFormatted = "\$180.00",
                totalFormatted = "\$180.00",
            )
        }
    }
}

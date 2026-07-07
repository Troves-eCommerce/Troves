package com.troves.designsystem.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.img_placeholder


@Composable
fun OrderSummaryItemCard(
    imagePainter: Painter,
    name: String,
    specs: String,
    quantity: Int,
    priceFormatted: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
           .shadow(elevation =0.4.dp, shape = Theme.shapes.medium, clip = false)
            .clip(Theme.shapes.medium)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.onPrimaryVariant, Theme.shapes.medium)
            .padding(Theme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = imagePainter,
            contentDescription = name,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(110.dp)
                .fillMaxHeight()
                .clip(Theme.shapes.medium),
        )

        Column(
            modifier = Modifier.weight(1f),
        ) {
            BasicText(
                text = name,
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.SemiBold,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            BasicText(
                text = specs,
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
            )
            BasicText(
                text = "Qty: $quantity",
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
            )
        }

        BasicText(
            text = priceFormatted,
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .align(Alignment.Bottom)
                .padding(bottom = Theme.spacing.extraSmall),
        )
    }
}

@Preview
@Composable
private fun OrderSummaryItemCardPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        ) {
            OrderSummaryItemCard(
                imagePainter = painterResource(Res.drawable.img_placeholder),
                name = "Soft Knit Sweater",
                specs = "Cream / M",
                quantity = 2,
                priceFormatted = "\$98.00",
            )
        }
    }
}

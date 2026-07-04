package com.troves.presintation.ui.orderresult

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.cards.SectionCard
import com.troves.designsystem.theme.Theme
import com.troves.presintation.navigation.AppRoute


@Composable
fun OrderResultScreen(
    args: AppRoute.OrderResult,
    onGoHome: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        containerColor = Theme.colors.backGround,
        bottomBar = {
            PrimaryButton(
                caption = "Go to Home",
                onClick = onGoHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(Theme.spacing.medium),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.large),
        ) {
            StatusHeader(args)

            SectionCard(title = "Order Summary") {
                if (args.itemImageUrls.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                            args.itemImageUrls.take(4).forEach { url ->
                                androidx.compose.foundation.Image(
                                    painter = rememberAsyncImagePainter(url),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(48.dp).clip(Theme.shapes.medium),
                                )
                            }
                        }
                        BasicText(
                            text = "${args.itemCount} items",
                            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                        )
                    }
                    Divider()
                }
                SummaryRow("Subtotal", args.subtotalFormatted, Theme.colors.primaryFont)
                if (args.discountLabel != null && args.discountValueFormatted != null) {
                    SummaryRow(args.discountLabel, args.discountValueFormatted, Theme.colors.success)
                }
                Divider()
                SummaryRow(
                    label = "Total",
                    value = args.totalFormatted,
                    color = Theme.colors.primaryFont,
                    style = Theme.typography.body.large,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (args.recipientName.isNotBlank() || args.addressLines.isNotEmpty()) {
                SectionCard(title = "Delivery") {
                    if (args.paymentLabel.isNotBlank()) {
                        BasicText(
                            text = args.paymentLabel,
                            style = Theme.typography.body.medium.copy(
                                color = Theme.colors.primaryFont,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                    if (args.recipientName.isNotBlank()) {
                        BasicText(
                            text = args.recipientName,
                            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                        )
                    }
                    args.addressLines.forEach { line ->
                        BasicText(
                            text = line,
                            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                        )
                    }
                    if (args.phone.isNotBlank()) {
                        BasicText(
                            text = args.phone,
                            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusHeader(args: AppRoute.OrderResult) {
    val accent = if (args.success) Theme.colors.success else Theme.colors.error
    val glyph = if (args.success) "✓" else "!" // check / exclamation
    val title = if (args.success) "Order Placed!" else "Payment Failed"
    val message = when {
        !args.success -> args.errorMessage ?: "Something went wrong. Please try again."
        args.orderName != null -> "Your order ${args.orderName} has been placed successfully."
        else -> "Your order has been placed successfully."
    }
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = Theme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        Box(
            modifier = Modifier.size(72.dp).clip(CircleShape).background(accent),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = glyph,
                style = Theme.typography.display.copy(color = Theme.colors.onSuccess),
            )
        }
        BasicText(
            text = title,
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
        BasicText(
            text = message,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Theme.spacing.extraSmall)
            .height(1.dp)
            .background(Theme.colors.surfaceVariant),
    )
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    color: Color,
    style: TextStyle = Theme.typography.body.medium,
    fontWeight: FontWeight = FontWeight.Medium,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        BasicText(text = label, style = style.copy(color = color, fontWeight = fontWeight))
        BasicText(text = value, style = style.copy(color = color, fontWeight = fontWeight))
    }
}

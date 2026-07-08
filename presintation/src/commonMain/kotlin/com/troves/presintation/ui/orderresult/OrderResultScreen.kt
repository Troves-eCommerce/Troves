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
import com.troves.presintation.ui.orderresult.components.ConfettiCelebration
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.Res as ResP
import troves.presintation.generated.resources.checkout_items_count
import troves.presintation.generated.resources.checkout_order_summary
import troves.presintation.generated.resources.checkout_total
import troves.presintation.generated.resources.order_details_subtotal
import troves.presintation.generated.resources.order_result_delivery
import troves.presintation.generated.resources.order_result_error_generic
import troves.presintation.generated.resources.order_result_failed_title
import troves.presintation.generated.resources.order_result_go_home
import troves.presintation.generated.resources.order_result_success_generic
import troves.presintation.generated.resources.order_result_success_named
import troves.presintation.generated.resources.order_result_success_title


@Composable
fun OrderResultScreen(
    args: AppRoute.OrderResult,
    onGoHome: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(Theme.colors.backGround)) {
    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        containerColor = Color.Transparent,
        bottomBar = {
            PrimaryButton(
                caption = stringResource(ResP.string.order_result_go_home),
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

            SectionCard(title = stringResource(ResP.string.checkout_order_summary)) {
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
                            text = stringResource(ResP.string.checkout_items_count, args.itemCount),
                            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                        )
                    }
                    Divider()
                }
                SummaryRow(stringResource(ResP.string.order_details_subtotal), args.subtotalFormatted, Theme.colors.primaryFont)
                if (args.discountLabel != null && args.discountValueFormatted != null) {
                    SummaryRow(args.discountLabel, args.discountValueFormatted, Theme.colors.success)
                }
                Divider()
                SummaryRow(
                    label = stringResource(ResP.string.checkout_total),
                    value = args.totalFormatted,
                    color = Theme.colors.primaryFont,
                    style = Theme.typography.body.large,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (args.recipientName.isNotBlank() || args.addressLines.isNotEmpty()) {
                SectionCard(title = stringResource(ResP.string.order_result_delivery)) {
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

        if (args.success) {
            ConfettiCelebration(
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
            )
        }
    }
}

@Composable
private fun StatusHeader(args: AppRoute.OrderResult) {
    val accent = if (args.success) Theme.colors.success else Theme.colors.error
    val glyph = if (args.success) "✓" else "!" // check / exclamation
    val title = if (args.success) {
        stringResource(ResP.string.order_result_success_title)
    } else {
        stringResource(ResP.string.order_result_failed_title)
    }
    val message = when {
        !args.success -> args.errorMessage ?: stringResource(ResP.string.order_result_error_generic)
        args.orderName != null -> stringResource(ResP.string.order_result_success_named, args.orderName)
        else -> stringResource(ResP.string.order_result_success_generic)
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

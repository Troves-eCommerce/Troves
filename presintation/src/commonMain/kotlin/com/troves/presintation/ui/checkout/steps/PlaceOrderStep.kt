package com.troves.presintation.ui.checkout.steps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.cards.SectionCard
import com.troves.designsystem.components.stepper.HorizontalStepper
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_order
import troves.designsystem.generated.resources.ic_payment_method
import troves.designsystem.generated.resources.product_card

/**
 * Body of the "Confirm & Place Order" checkout step: read-only recap of the chosen
 * payment method, shipping address, and order summary (item thumbnails + totals),
 * each with a link back to the relevant step. No tax/shipping rows (not modeled).
 * No top app bar in this pass.
 */
@Composable
fun PlaceOrderStepContent(
    currentStep: Int,
    paymentIcon: Painter,
    paymentTitle: String,
    paymentDescription: String,
    addressTitle: String,
    recipientName: String,
    addressLines: List<String>,
    phone: String,
    itemImages: List<Painter>,
    itemCount: Int,
    subtotalFormatted: String,
    totalFormatted: String,
    onChangePayment: () -> Unit,
    onChangeAddress: () -> Unit,
    onEditCart: () -> Unit,
    modifier: Modifier = Modifier,
    totalSteps: Int = 4,
    paymentSubDescription: String? = null,
    discountCode: String? = null,
    discountValueFormatted: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        HorizontalStepper(currentStep = currentStep, totalSteps = totalSteps)

        BasicText(
            text = "Confirm & Place Order",
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
        BasicText(
            text = "Review your details and place your order.",
            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
        )

        SectionCard(title = "Payment Method", actionText = "Change", onActionClick = onChangePayment) {
            Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(Theme.shapes.medium)
                        .background(Theme.colors.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = paymentIcon,
                        contentDescription = null,
                        tint = Theme.colors.primaryFont,
                        modifier = Modifier.size(Theme.size.iconMedium),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)) {
                    BasicText(
                        text = paymentTitle,
                        style = Theme.typography.body.large.copy(
                            color = Theme.colors.primaryFont,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    BasicText(
                        text = paymentDescription,
                        style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                    )
                    if (paymentSubDescription != null) {
                        BasicText(
                            text = paymentSubDescription,
                            style = Theme.typography.body.medium.copy(
                                color = Theme.colors.success,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                }
            }
        }

        SectionCard(title = "Shipping Address", actionText = "Change", onActionClick = onChangeAddress) {
            BasicText(
                text = addressTitle,
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            BasicText(
                text = recipientName,
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
            )
            addressLines.forEach { line ->
                BasicText(
                    text = line,
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                )
            }
            BasicText(
                text = phone,
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
            )
        }

        SectionCard(title = "Order Summary", actionText = "Edit Cart", onActionClick = onEditCart) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                    itemImages.forEach { image ->
                        Image(
                            painter = image,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(Theme.shapes.medium),
                        )
                    }
                }
                BasicText(
                    text = "$itemCount items",
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                )
            }

            Divider()

            SummaryRow(
                label = "Subtotal ($itemCount items)",
                value = subtotalFormatted,
                color = Theme.colors.primaryFont,
            )
            if (discountCode != null && discountValueFormatted != null) {
                SummaryRow(
                    label = "Discount ($discountCode)",
                    value = discountValueFormatted,
                    color = Theme.colors.success,
                )
            }

            Divider()

            SummaryRow(
                label = "Total",
                value = totalFormatted,
                color = Theme.colors.primaryFont,
                style = Theme.typography.body.large,
                fontWeight = FontWeight.Bold,
            )
        }

        OrderConfirmationNote()
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
    color: androidx.compose.ui.graphics.Color,
    style: androidx.compose.ui.text.TextStyle = Theme.typography.body.medium,
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

@Composable
private fun OrderConfirmationNote() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.medium)
            .background(Theme.colors.surfaceVariant)
            .padding(Theme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Placeholder glyph until a dedicated mail icon asset exists.
        Icon(
            painter = painterResource(Res.drawable.ic_order),
            contentDescription = null,
            tint = Theme.colors.primaryFont,
            modifier = Modifier.size(Theme.size.iconMedium),
        )
        Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)) {
            BasicText(
                text = "Order Confirmation",
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            BasicText(
                text = "You will receive an email confirmation once your order is placed.",
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
            )
        }
    }
}

@Preview
@Composable
private fun PlaceOrderStepPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        val image = painterResource(Res.drawable.product_card)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colors.backGround),
        ) {
            PlaceOrderStepContent(
                currentStep = 4,
                paymentIcon = painterResource(Res.drawable.ic_payment_method),
                paymentTitle = "Cash on Delivery (COD)",
                paymentDescription = "Pay with cash when your order is delivered.",
                paymentSubDescription = "Cash limit: up to \$500.00",
                addressTitle = "Home",
                recipientName = "Sophia Johnson",
                addressLines = listOf("123 Maple Street, Apartment 4B", "San Francisco, CA 94107", "United States"),
                phone = "+1 415 555 0123",
                itemImages = listOf(image, image, image),
                itemCount = 3,
                subtotalFormatted = "\$246.00",
                totalFormatted = "\$221.40",
                discountCode = "WELCOME10",
                discountValueFormatted = "- \$24.60",
                onChangePayment = {},
                onChangeAddress = {},
                onEditCart = {},
                modifier = Modifier.weight(1f),
            )
            // Temporary CTA — moves to a shared sticky bottom bar in a later phase.
            PrimaryButton(
                caption = "Place Order",
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing.medium),
            )
        }
    }
}

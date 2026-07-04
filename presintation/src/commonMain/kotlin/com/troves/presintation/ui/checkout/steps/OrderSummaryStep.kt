package com.troves.presintation.ui.checkout.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.cards.OrderSummaryInfoCard
import com.troves.designsystem.components.cards.OrderSummaryItemCard
import com.troves.designsystem.components.stepper.HorizontalStepper
import com.troves.designsystem.components.textfield.TextField
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.product_card

/** UI model for a single order line in the Order Summary step. */
data class OrderSummaryItemUi(
    val imagePainter: Painter,
    val name: String,
    val specs: String,
    val quantity: Int,
    val priceFormatted: String,
)

/**
 * Body of the "Order Summary" (Review) checkout step: a coupon-code entry,
 * the list of order line items, and the totals card. No top app bar in this pass.
 */
@Composable
fun OrderSummaryStepContent(
    couponInput: String,
    onCouponChange: (String) -> Unit,
    onApplyCoupon: () -> Unit,
    items: List<OrderSummaryItemUi>,
    itemCount: Int,
    subtotalFormatted: String,
    totalFormatted: String,
    currentStep: Int,
    modifier: Modifier = Modifier,
    totalSteps: Int = 4,
    discountCode: String? = null,
    discountValueFormatted: String? = null,
    isApplyingCoupon: Boolean = false,
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
            text = "Order Summary",
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
        BasicText(
            text = "Review your items and apply a coupon.",
            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)
        ) {
            BasicText(
            text = "Coupon Code",
            style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont),
        )
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = Theme.spacing.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                TextField(
                    text = couponInput,
                    onTextChange = onCouponChange,
                    hint = "Enter coupon code",
                    singleLine = true,
                    modifier = Modifier.fillMaxHeight().width(280.dp).padding(end = 8.dp)
                )
                PrimaryButton(
                    caption = "Apply",
                    onClick = onApplyCoupon,
                    isLoading = isApplyingCoupon,
                    isDisabled = couponInput.isBlank(),
                    modifier = Modifier.width(80.dp).height(48.dp)
                )
            }
        }



        items.forEach { item ->
            OrderSummaryItemCard(
                imagePainter = item.imagePainter,
                name = item.name,
                specs = item.specs,
                quantity = item.quantity,
                priceFormatted = item.priceFormatted,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Theme.colors.primary),
            )
        }

        OrderSummaryInfoCard(
            itemCount = itemCount,
            subtotalFormatted = subtotalFormatted,
            totalFormatted = totalFormatted,
            discountCode = discountCode,
            discountValueFormatted = discountValueFormatted,
        )
    }
}

@Preview
@Composable
private fun OrderSummaryStepPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        var coupon by remember { mutableStateOf("WELCOME10") }
        val image = painterResource(Res.drawable.product_card)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colors.backGround),
        ) {
            OrderSummaryStepContent(
                couponInput = coupon,
                onCouponChange = { coupon = it },
                onApplyCoupon = {},
                items = listOf(
                    OrderSummaryItemUi(image, "Soft Knit Sweater", "Cream / M", 2, "\$98.00"),
                    OrderSummaryItemUi(image, "Linen Trousers", "Sand / 32", 1, "\$50.00"),
                ),
                itemCount = 3,
                subtotalFormatted = "\$246.00",
                totalFormatted = "\$221.40",
                currentStep = 1,
                discountCode = "WELCOME10",
                discountValueFormatted = "- \$24.60",
                modifier = Modifier.weight(1f),
            )
            PrimaryButton(
                caption = "Continue",
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing.medium),
            )
        }
    }
}

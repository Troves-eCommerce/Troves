package com.troves.presintation.ui.checkout.steps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.cards.OrderSummaryInfoCard
import com.troves.designsystem.components.cards.OrderSummaryItemCard
import com.troves.designsystem.components.stepper.HorizontalStepper
import com.troves.designsystem.components.textfield.CustomTextField
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.formatPrice
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.img_placeholder
import troves.presintation.generated.resources.apply
import troves.presintation.generated.resources.checkout_coupon_applied
import troves.presintation.generated.resources.checkout_coupon_hint
import troves.presintation.generated.resources.checkout_coupon_label
import troves.presintation.generated.resources.checkout_discount_code
import troves.presintation.generated.resources.checkout_order_summary
import troves.presintation.generated.resources.checkout_review_subtitle
import troves.presintation.generated.resources.checkout_subtotal_items
import troves.presintation.generated.resources.checkout_total
import troves.presintation.generated.resources.Res as StringRes

data class OrderSummaryItemUi(
    val imagePainter: Painter,
    val name: String,
    val specs: String,
    val quantity: Int,
    val priceFormatted: String,
)


@Composable
fun OrderSummaryStepContent(
    couponInput: String,
    onCouponChange: (String) -> Unit,
    onApplyCoupon: () -> Unit,
    onRemoveCoupon: () -> Unit,
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
            text = stringResource(StringRes.string.checkout_order_summary),
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
        BasicText(
            text = stringResource(StringRes.string.checkout_review_subtitle),
            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)
        ) {
            BasicText(
            text = stringResource(StringRes.string.checkout_coupon_label),
            style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont),
        )
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = Theme.spacing.small),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                verticalAlignment = Alignment.Bottom,
            ) {
                CustomTextField(
                    text = couponInput,
                    onTextChange = onCouponChange,
                    hint = stringResource(StringRes.string.checkout_coupon_hint),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    caption = stringResource(StringRes.string.apply),
                    onClick = onApplyCoupon,
                    isLoading = isApplyingCoupon,
                    isDisabled = couponInput.isBlank(),
                    modifier = Modifier.widthIn(min = 88.dp).height(48.dp)
                )
            }

            if (discountCode != null) {
                AppliedCouponChip(
                    code = discountCode,
                    onRemove = onRemoveCoupon,
                    enabled = !isApplyingCoupon,
                )
                BasicText(
                    text = stringResource(StringRes.string.checkout_coupon_applied),
                    style = Theme.typography.body.small.copy(color = Theme.colors.success),
                )
            }
        }



        items.forEach { item ->
            OrderSummaryItemCard(
                imagePainter = item.imagePainter,
                name = item.name,
                specs = item.specs,
                quantity = item.quantity,
                priceFormatted = formatPrice(item.priceFormatted),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Theme.colors.primary),
            )
        }

        OrderSummaryInfoCard(
            subtotalLabel = stringResource(StringRes.string.checkout_subtotal_items, itemCount),
            subtotalFormatted = formatPrice(subtotalFormatted),
            totalLabel = stringResource(StringRes.string.checkout_total),
            totalFormatted = formatPrice(totalFormatted),
            discountLabel = discountCode?.let { stringResource(StringRes.string.checkout_discount_code, it) },
            discountValueFormatted = discountValueFormatted?.let { formatPrice(it) },
        )
    }
}

@Composable
private fun AppliedCouponChip(
    code: String,
    onRemove: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val crossColor = Theme.colors.onSecondary
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.spacing.small))
            .background(Theme.colors.secondary)
            .padding(horizontal = Theme.spacing.small, vertical = Theme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        BasicText(
            text = code,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.onSecondary,
                fontWeight = FontWeight.Medium,
            ),
        )
        Canvas(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(50))
                .clickable(enabled = enabled, onClick = onRemove)
                .padding(3.dp),
        ) {
            val stroke = 1.6.dp.toPx()
            drawLine(
                color = crossColor,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = crossColor,
                start = Offset(size.width, 0f),
                end = Offset(0f, size.height),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Preview
@Composable
private fun OrderSummaryStepPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        var coupon by remember { mutableStateOf("WELCOME10") }
        var appliedCode by remember { mutableStateOf<String?>("WELCOME10") }
        val image = painterResource(Res.drawable.img_placeholder)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colors.backGround),
        ) {
            OrderSummaryStepContent(
                couponInput = coupon,
                onCouponChange = { coupon = it },
                onApplyCoupon = { appliedCode = coupon },
                onRemoveCoupon = { appliedCode = null },
                items = listOf(
                    OrderSummaryItemUi(image, "Soft Knit Sweater", "Cream / M", 2, "\$98.00"),
                    OrderSummaryItemUi(image, "Linen Trousers", "Sand / 32", 1, "\$50.00"),
                ),
                itemCount = 3,
                subtotalFormatted = "\$246.00",
                totalFormatted = "\$221.40",
                currentStep = 1,
                discountCode = appliedCode,
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

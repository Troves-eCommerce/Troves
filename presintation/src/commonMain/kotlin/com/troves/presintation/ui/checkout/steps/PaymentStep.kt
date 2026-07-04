package com.troves.presintation.ui.checkout.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.cards.PaymentMethodCard
import com.troves.designsystem.components.stepper.HorizontalStepper
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_payment_method


enum class PaymentOption { CashOnDelivery, Online }


@Composable
fun PaymentStepContent(
    selected: PaymentOption?,
    onSelect: (PaymentOption) -> Unit,
    currentStep: Int,
    modifier: Modifier = Modifier,
    totalSteps: Int = 4,
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
            text = "Select Payment Method",
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
        BasicText(
            text = "Choose how you'd like to pay for your order.",
            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
        )

        PaymentMethodCard(
            title = "Cash on Delivery (COD)",
            description = "Pay with cash when your order is delivered.",
            icon = painterResource(Res.drawable.ic_payment_method),
            selected = selected == PaymentOption.CashOnDelivery,
            onClick = { onSelect(PaymentOption.CashOnDelivery) },
            label = "COD",
            subDescription = "Cash limit: up to \$500.00",
        )
        PaymentMethodCard(
            title = "Online Payment",
            description = "Pay securely using your card.",
            icon = painterResource(Res.drawable.ic_payment_method),
            selected = selected == PaymentOption.Online,
            onClick = { onSelect(PaymentOption.Online) },
            trailingContent = {
                Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                    listOf("VISA", "MC", "AMEX").forEach {
                        BasicText(
                            text = it,
                            style = Theme.typography.body.small.copy(
                                color = Theme.colors.secondaryFont,
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                }
            },
        )

        Spacer(modifier = Modifier.weight(1f))

        SecureCheckoutNote()
    }
}

@Composable
private fun SecureCheckoutNote() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.medium)
            .background(Theme.colors.surface)
            .padding(Theme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_payment_method),
            contentDescription = null,
            tint = Theme.colors.primaryFont,
            modifier = Modifier.size(Theme.size.iconMedium),
        )
        Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall), modifier = Modifier.background(Theme.colors.surface)) {
            BasicText(
                text = "Secure Checkout",
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            BasicText(
                text = "Your payment information is encrypted and safe with us.",
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
            )
        }
    }
}

@Preview
@Composable
private fun PaymentStepPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        var selected by remember { mutableStateOf<PaymentOption?>(PaymentOption.CashOnDelivery) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colors.backGround),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                PaymentStepContent(
                    selected = selected,
                    onSelect = { selected = it },
                    currentStep = 3,
                )
            }
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

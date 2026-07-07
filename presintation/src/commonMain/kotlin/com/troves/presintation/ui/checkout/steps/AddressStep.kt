package com.troves.presintation.ui.checkout.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.cards.AddressCard
import com.troves.designsystem.components.stepper.HorizontalStepper
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_home
import troves.designsystem.generated.resources.ic_location
import troves.presintation.generated.resources.Res as StringRes
import troves.presintation.generated.resources.checkout_add_new_address
import troves.presintation.generated.resources.checkout_address_default
import troves.presintation.generated.resources.checkout_address_step_subtitle
import troves.presintation.generated.resources.checkout_address_step_title
import troves.presintation.generated.resources.checkout_no_saved_addresses

data class AddressUi(
    val id: String,
    val title: String,
    val recipientName: String,
    val addressLines: List<String>,
    val phone: String,
    val iconPainter: Painter,
    val isDefault: Boolean = false,
)


@Composable
fun AddressStepContent(
    addresses: List<AddressUi>,
    selectedAddressId: String?,
    onSelectAddress: (String) -> Unit,
    onEditAddress: (String) -> Unit,
    onAddAddressClick: () -> Unit,
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
            text = stringResource(StringRes.string.checkout_address_step_title),
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
        BasicText(
            text = stringResource(StringRes.string.checkout_address_step_subtitle),
            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
        )

        if (addresses.isEmpty()) {
            BasicText(
                text = stringResource(StringRes.string.checkout_no_saved_addresses),
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                modifier = Modifier.padding(vertical = Theme.spacing.small),
            )
        } else {
            addresses.forEach { address ->
                AddressCard(
                    title = address.title,
                    recipientName = address.recipientName,
                    addressLines = address.addressLines,
                    phone = address.phone,
                    icon = address.iconPainter,
                    selected = address.id == selectedAddressId,
                    onClick = { onSelectAddress(address.id) },
                    label = if (address.isDefault) stringResource(StringRes.string.checkout_address_default) else null,
                    onEditClick = { onEditAddress(address.id) },
                )
            }
        }

        AddAddressButton(onClick = onAddAddressClick)
    }
}

@Composable
private fun AddAddressButton(onClick: () -> Unit) {
    val borderColor = Theme.colors.secondary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f)),
                    ),
                )
            }
            .clickable(onClick = onClick)
            .padding(Theme.spacing.medium),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                text = "+",
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )
            BasicText(
                text = stringResource(StringRes.string.checkout_add_new_address),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}

@Preview
@Composable
private fun AddressStepPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        var selectedId by remember { mutableStateOf("home") }
        val homeIcon = painterResource(Res.drawable.ic_home)
        val workIcon = painterResource(Res.drawable.ic_location)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colors.backGround),
        ) {
            AddressStepContent(
                addresses = listOf(
                    AddressUi(
                        id = "home",
                        title = "Home",
                        recipientName = "Sophia Johnson",
                        addressLines = listOf("123 Maple Street, Apartment 4B", "San Francisco, CA 94107", "United States"),
                        phone = "+1 415 555 0123",
                        iconPainter = homeIcon,
                        isDefault = true,
                    ),
                    AddressUi(
                        id = "work",
                        title = "Work",
                        recipientName = "Sophia Johnson",
                        addressLines = listOf("456 Business Park, Suite 200", "San Francisco, CA 94108", "United States"),
                        phone = "+1 415 555 0456",
                        iconPainter = workIcon,
                    ),
                ),
                selectedAddressId = selectedId,
                onSelectAddress = { selectedId = it },
                onEditAddress = {},
                onAddAddressClick = {},
                currentStep = 2,
                modifier = Modifier.weight(1f),
            )
            // Temporary CTA — moves to a shared sticky bottom bar in a later phase.
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

package com.troves.presintation.ui.productDetails.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.troves.designsystem.components.button.PrimaryButton

import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.product_details_add_to_cart

@Composable
fun AddToCartButton(
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    PrimaryButton(
        modifier = modifier,
        caption = stringResource(Res.string.product_details_add_to_cart),
        onClick = onAddToCart,
        isDisabled = !enabled
    )
}

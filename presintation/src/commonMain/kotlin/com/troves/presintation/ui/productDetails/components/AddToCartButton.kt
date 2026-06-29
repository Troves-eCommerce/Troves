package com.troves.presintation.ui.productDetails.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.troves.designsystem.components.button.PrimaryButton

@Composable
fun AddToCartButton(
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    PrimaryButton(
        modifier = modifier,
        caption = "Add To Cart",
        onClick = onAddToCart,
        isDisabled = !enabled
    )
}

package com.troves.designsystem.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme

@Composable
fun SecondaryButton(
    caption: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconPainter: Painter? = null,
    iconPosition: ButtonIconPosition = ButtonIconPosition.Start,
    isDisabled: Boolean = false,
    isLoading: Boolean = false,
) {
    val containerColor = Theme.colors.backGround
    val contentColor = Theme.colors.primary
    val border = Theme.colors.primary
    BaseButton(
        caption = caption,
        modifier = modifier,
        iconPainter = iconPainter,
        iconPosition = iconPosition,
        containerColor = containerColor,
        contentColor = contentColor,
        borderColor = border,
        hasBorder = true,
        isDisabled = isDisabled,
        isLoading = isLoading,
        onClick = onClick,
        loadingView = {
            LottieButtonLoader(
                tintColor = contentColor,
            )
        }
    )
}

@Preview
@Composable
private fun PreviewSecondaryButton() {
    SpTheme(
        isDarkTheme = false,
        locale = Locale("ar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colors.backGround),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            SecondaryButton(
                caption = "Button",
                onClick = {},
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            SecondaryButton(
                caption = "Button",
                onClick = {},
                modifier = Modifier.fillMaxWidth(0.5f),
                isDisabled = true
            )
            SecondaryButton(
                caption = "Button",
                onClick = {},
                modifier = Modifier.fillMaxWidth(0.5f),
                isLoading = true
            )
        }
    }
}

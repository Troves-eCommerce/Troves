package com.troves.designsystem.components.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter

import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.noRippleClickable

enum class ButtonIconPosition { Start, End }

@Composable
internal fun BaseButton(
    caption: String?,
    modifier: Modifier = Modifier,
    iconPainter: Painter? = null,
    iconPosition: ButtonIconPosition = ButtonIconPosition.Start,
    containerColor: Color = Theme.colors.primary,
    contentColor: Color = Theme.colors.onPrimary,
    onClick: () -> Unit,
    isDisabled: Boolean = false,
    borderColor: Color = Color.Transparent,
    hasBorder: Boolean = false,
    isLoading: Boolean = false,
    loadingView: (@Composable () -> Unit)? = null,
) {
    val backGroundColor = if (isDisabled) Theme.colors.disable else containerColor
    val borderColor = if (!hasBorder || isDisabled) Color.Transparent else borderColor
    Row(
        modifier = modifier
            .height(
                58.dp
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(14.dp),
                color = borderColor
            )
            .clip(RoundedCornerShape(14.dp))
            .background(backGroundColor)
            .noRippleClickable(onClick = { if (!isDisabled && !isLoading) onClick() }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            loadingView?.invoke()
        } else {
            @Composable
            fun IconBlock() {
                if (iconPainter != null) {
                    Image(
                        painter = iconPainter,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(
                            if (isDisabled) Theme.colors.onDisable else contentColor
                        )
                    )
                }
            }
            @Composable
            fun CaptionBlock() {
                if (caption != null) {
                    BasicText(
                        text = caption,
                        style = Theme.typography.body.large.copy(
                            color = if (isDisabled) Theme.colors.onDisable else contentColor
                        ),
                    )
                }
            }
            @Composable
            fun Gap() {
                if (caption != null && iconPainter != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            when (iconPosition) {
                ButtonIconPosition.Start -> {
                    IconBlock(); Gap(); CaptionBlock()
                }
                ButtonIconPosition.End -> {
                    CaptionBlock(); Gap(); IconBlock()
                }
            }
        }
    }
}


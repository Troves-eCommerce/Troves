package com.example.designsystem.components.textfield

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.VisualTransformation

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.SpTheme
import com.example.designsystem.theme.Theme

@Composable
fun TextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
    title: String? = null,
    tipText: String? = null,
    titleColor: Color = Theme.colors.primaryFont,
    textColor: Color = Theme.colors.primaryFont,
    borderColor: Color = Theme.colors.hint,
    containerColor: Color = Theme.colors.backGround,
    onFocusBorderColor: Color = Theme.colors.primary,
    errorBorderColor: Color = Theme.colors.error,
    errorMessage: String? = null,
    errorIcon: Painter? = null,
    isError: Boolean = false,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    enabled: Boolean = true,
    leadingIconColor: Color = if (!enabled) Theme.colors.onDisable else Theme.colors.hint,
    trailingIconColor: Color = if (!enabled) Theme.colors.onDisable else Theme.colors.hint,
    onClickLeadingIcon: (() -> Unit)? = null,
    onClickTrailingIcon: (() -> Unit)? = null,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    /**
     * Height of the input box. Defaults to the standard single-line height; pass a
     * larger value for a multi-line area (e.g. a message body). When taller than the
     * default, callers usually pair it with [fieldVerticalAlignment] = `Alignment.Top`
     * so the text starts at the top instead of being vertically centred.
     */
    fieldHeight: Dp = Theme.size.componentsNormalHeight,
    fieldVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    shape: Shape = Theme.shapes.medium,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(Theme.colors.primary),
) {
    var isFocused: Boolean by remember { mutableStateOf(false) }
    val currentBorderColor = when {
        isError -> errorBorderColor
        isFocused -> onFocusBorderColor
        else -> borderColor
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            title?.let {
                BasicText(
                    text = it,
                    style = Theme.typography.body.medium.copy(
                        color = titleColor
                    )
                )
            }
            tipText?.let {
                BasicText(
                    text = it,
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.hint
                    )
                )
            }
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(fieldHeight)
                .clip(shape)
                .background(containerColor)
                .border(
                    width = 1.dp,
                    shape = shape,
                    color = currentBorderColor
                )
                .padding(Theme.spacing.small),
            verticalAlignment = fieldVerticalAlignment,
        )
        {
            leadingIcon?.let {
                Image(
                    painter = it,
                    colorFilter = ColorFilter.tint(
                        color = leadingIconColor
                    ),
                    contentDescription = "leading icon",
                    modifier = Modifier
                        .padding(end = Theme.spacing.small)
                        .size(Theme.size.iconMedium)
                        .clickable(
                            enabled = onClickLeadingIcon != null,
                            onClick = {
                                onClickLeadingIcon?.invoke()
                            }
                        ),
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                if (text.isBlank()) {
                    hint?.let {
                        BasicText(
                            text = it,
                            style = Theme.typography.body.medium.copy(
                                color = Theme.colors.hint
                            )
                        )
                    }
                }
                    BasicTextField(
                        value = text,
                        onValueChange = onTextChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                            },
                        textStyle = Theme.typography.body.medium.copy(
                            color = if (!enabled) Theme.colors.onDisable else textColor
                        ),
                        enabled = enabled,
                        readOnly = readOnly,
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        singleLine = singleLine,
                        maxLines = maxLines,
                        minLines = minLines,
                        visualTransformation = visualTransformation,
                        onTextLayout = onTextLayout,
                        interactionSource = interactionSource,
                        cursorBrush = cursorBrush,
                    )

            }
            trailingIcon?.let {
                Image(
                    painter = it,
                    colorFilter = ColorFilter.tint(
                        color = trailingIconColor
                    ),
                    contentDescription = "trailing icon",
                    modifier = Modifier
                        .padding(start = Theme.spacing.small)
                        .size(Theme.size.iconMedium)
                        .clickable(
                            enabled = onClickTrailingIcon != null,
                            onClick = {
                                onClickTrailingIcon?.invoke()
                            }
                        ),
                )
            }
        }
        if (isError) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                errorIcon?.let {
                    Image(
                        painter = it,
                        colorFilter = ColorFilter.tint(
                            color = Theme.colors.error
                        ),
                        contentDescription = "error icon",
                        modifier = Modifier
                            .padding(start = Theme.spacing.small)
                            .size(Theme.size.iconSmall)
                    )
                }
                errorMessage?.let {
                    BasicText(
                        text = it,
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.error
                        )
                    )
                }
            }
        }
    }
}

// ── Empty / hint only ────────────────────────────────────────────────────────

@Composable
private fun TextFieldPreviewEmpty() {
    SpTheme {
        TextField(
            text = "",
            onTextChange = {},
            hint = "Enter your name",
            onClickLeadingIcon = null,
        )
    }
}

// ── With text entered ─────────────────────────────────────────────────────────

@Composable
private fun TextFieldPreviewWithText() {
    SpTheme {
        TextField(
            text = "John Doe",
            onTextChange = {},
            hint = "Enter your name",
            onClickLeadingIcon = null,
        )
    }
}

// ── Title + tip text ──────────────────────────────────────────────────────────

@Composable
private fun TextFieldPreviewTitleTip() {
    SpTheme {
        TextField(
            text = "",
            onTextChange = {},
            hint = "Enter your name",
            title = "Full Name",
            tipText = "Required",
            onClickLeadingIcon = null,
        )
    }
}

// ── Leading icon ──────────────────────────────────────────────────────────────

@Composable
private fun TextFieldPreviewLeadingIcon() {
    SpTheme {
        TextField(
            text = "",
            onTextChange = {},
            hint = "Search…",
            title = "Search",
            leadingIcon = ColorPainter(Color.Black),
            onClickLeadingIcon = null,
        )
    }
}

// ── Trailing icon ─────────────────────────────────────────────────────────────

@Composable
private fun TextFieldPreviewTrailingIcon() {
    SpTheme {
        TextField(
            text = "john@example.com",
            onTextChange = {},
            hint = "Enter email",
            title = "Email",
            trailingIcon = ColorPainter(Color.Black),
            onClickLeadingIcon = null,
            onClickTrailingIcon = {},
        )
    }
}

// ── Leading + trailing icons ──────────────────────────────────────────────────

@Composable
private fun TextFieldPreviewBothIcons() {
    SpTheme {
        TextField(
            text = "my_password",
            onTextChange = {},
            hint = "Password",
            title = "Password",
            leadingIcon = ColorPainter(Color.Black),
            trailingIcon = ColorPainter(Color.Black),
            onClickLeadingIcon = null,
            onClickTrailingIcon = {},
        )
    }
}

// ── Error state ───────────────────────────────────────────────────────────────

@Composable
private fun TextFieldPreviewError() {
    SpTheme {
        TextField(
            text = "bad-input",
            onTextChange = {},
            hint = "Enter email",
            title = "Email",
            isError = true,
            errorMessage = "Invalid email address",
            errorIcon = ColorPainter(Color.Black),
            onClickLeadingIcon = null,
        )
    }
}

// ── Error state – no icon ─────────────────────────────────────────────────────

@Composable
private fun TextFieldPreviewErrorNoIcon() {
    SpTheme {
        TextField(
            text = "",
            onTextChange = {},
            hint = "Enter email",
            title = "Email",
            isError = true,
            errorMessage = "This field is required",
            onClickLeadingIcon = null,
        )
    }
}

// ── Disabled state ────────────────────────────────────────────────────────────

@Composable
private fun TextFieldPreviewDisabled() {
    SpTheme {
        TextField(
            text = "Cannot edit this",
            onTextChange = {},
            hint = "Enter text",
            title = "Disabled field",
            enabled = false,
            leadingIcon = ColorPainter(Color.Black),
            trailingIcon = ColorPainter(Color.Black),
            onClickLeadingIcon = null,
        )
    }
}

// ── Read-only state ───────────────────────────────────────────────────────────

@Composable
private fun TextFieldPreviewReadOnly() {
    SpTheme {
        TextField(
            text = "Read-only value",
            onTextChange = {},
            hint = "Enter text",
            title = "Read-only field",
            readOnly = true,
            onClickLeadingIcon = null,
        )
    }
}

// ── Single-line ───────────────────────────────────────────────────────────────

@Composable
private fun TextFieldPreviewSingleLine() {
    SpTheme {
        TextField(
            text = "Single line content that keeps going and going",
            onTextChange = {},
            hint = "Enter text",
            title = "Single line",
            singleLine = true,
            onClickLeadingIcon = null,
        )
    }
}

// ── Multi-line ────────────────────────────────────────────────────────────────

@Composable
private fun TextFieldPreviewMultiLine() {
    SpTheme {
        TextField(
            text = "Line one\nLine two\nLine three",
            onTextChange = {},
            hint = "Enter text",
            title = "Multi-line",
            minLines = 3,
            maxLines = 5,
            onClickLeadingIcon = null,
        )
    }
}

// ── All features combined ─────────────────────────────────────────────────────

@Composable
private fun TextFieldPreviewAllFeatures() {
    SpTheme {
        TextField(
            text = "hello@example.com",
            onTextChange = {},
            hint = "Enter email",
            title = "Email Address",
            tipText = "Required",
            leadingIcon = ColorPainter(Color.Black),
            trailingIcon = ColorPainter(Color.Black),
            onClickLeadingIcon = null,
            onClickTrailingIcon = {},
            isError = false,
            singleLine = true,
        )
    }
}

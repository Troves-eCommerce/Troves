package com.example.designsystem.components.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.Theme

/**
 * Internal preview-only showcase. Every text field holds its own mutable state
 * so you can type freely and toggle error/disabled/readonly via chip buttons
 * when running the preview on a device.
 */
@Composable
internal fun TextFieldShowcaseScreen(modifier: Modifier = Modifier) {

    // ── Shared toggle states ──────────────────────────────────────────────────
    var isError    by remember { mutableStateOf(false) }
    var isDisabled by remember { mutableStateOf(false) }
    var isReadOnly by remember { mutableStateOf(false) }

    // ── Individual field texts ────────────────────────────────────────────────
    var textEmpty      by remember { mutableStateOf("") }
    var textWithValue  by remember { mutableStateOf("John Doe") }
    var textTitleTip   by remember { mutableStateOf("") }
    var textLeading    by remember { mutableStateOf("") }
    var textTrailing   by remember { mutableStateOf("john@example.com") }
    var textBothIcons  by remember { mutableStateOf("my_password") }
    var textError      by remember { mutableStateOf("bad-input") }
    var textErrorNoIco by remember { mutableStateOf("") }
    var textDisabled   by remember { mutableStateOf("Cannot edit this") }
    var textReadOnly   by remember { mutableStateOf("Read-only value") }
    var textSingle     by remember { mutableStateOf("Single line content that keeps going and going") }
    var textMulti      by remember { mutableStateOf("Line one\nLine two\nLine three") }
    var textAll        by remember { mutableStateOf("hello@example.com") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {

        // Global toggles
        ShowcaseSectionHeader("Global Toggles")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StateChip(label = "Error",     active = isError,    onClick = { isError    = !isError })
            StateChip(label = "Disabled",  active = isDisabled, onClick = { isDisabled = !isDisabled })
            StateChip(label = "Read-only", active = isReadOnly, onClick = { isReadOnly = !isReadOnly })
        }

        ShowcaseDivider()

        // 1. Empty / hint only
        ShowcaseSectionHeader("1. Empty – hint only")
        TextField(
            text = textEmpty, onTextChange = { textEmpty = it },
            hint = "Enter your name",
            isError = isError, enabled = !isDisabled, readOnly = isReadOnly,
            errorMessage = if (isError) "Required" else null,
            onClickLeadingIcon = null,
        )

        ShowcaseDivider()

        // 2. With text
        ShowcaseSectionHeader("2. With text")
        TextField(
            text = textWithValue, onTextChange = { textWithValue = it },
            hint = "Enter your name",
            isError = isError, enabled = !isDisabled, readOnly = isReadOnly,
            errorMessage = if (isError) "Invalid value" else null,
            onClickLeadingIcon = null,
        )

        ShowcaseDivider()

        // 3. Title + tip
        ShowcaseSectionHeader("3. Title & tip text")
        TextField(
            text = textTitleTip, onTextChange = { textTitleTip = it },
            hint = "Enter your name", title = "Full Name", tipText = "Required",
            isError = isError, enabled = !isDisabled, readOnly = isReadOnly,
            errorMessage = if (isError) "Required" else null,
            onClickLeadingIcon = null,
        )

        ShowcaseDivider()

        // 4. Leading icon
        ShowcaseSectionHeader("4. Leading icon")
        TextField(
            text = textLeading, onTextChange = { textLeading = it },
            hint = "Search…", title = "Search",
            leadingIcon = ColorPainter(Color.Black),
            isError = isError, enabled = !isDisabled, readOnly = isReadOnly,
            errorMessage = if (isError) "No results" else null,
            onClickLeadingIcon = null,
        )

        ShowcaseDivider()

        // 5. Trailing icon
        ShowcaseSectionHeader("5. Trailing icon")
        TextField(
            text = textTrailing, onTextChange = { textTrailing = it },
            hint = "Enter email", title = "Email",
            trailingIcon = ColorPainter(Color.Black),
            isError = isError, enabled = !isDisabled, readOnly = isReadOnly,
            errorMessage = if (isError) "Invalid email" else null,
            onClickLeadingIcon = null,
            onClickTrailingIcon = { textTrailing = "" },
        )

        ShowcaseDivider()

        // 6. Leading + trailing icons
        ShowcaseSectionHeader("6. Leading + trailing icons")
        TextField(
            text = textBothIcons, onTextChange = { textBothIcons = it },
            hint = "Password", title = "Password",
            leadingIcon  = ColorPainter(Color.Black),
            trailingIcon = ColorPainter(Color.Black),
            isError = isError, enabled = !isDisabled, readOnly = isReadOnly,
            errorMessage = if (isError) "Too short" else null,
            onClickLeadingIcon = null,
            onClickTrailingIcon = { textBothIcons = "" },
        )

        ShowcaseDivider()

        // 7. Error state (independently always shows error)
        ShowcaseSectionHeader("7. Error state (static)")
        TextField(
            text = textError, onTextChange = { textError = it },
            hint = "Enter email", title = "Email",
            isError = true,
            errorMessage = "Invalid email address",
            errorIcon    = ColorPainter(Color.Black),
            enabled = !isDisabled, readOnly = isReadOnly,
            onClickLeadingIcon = null,
        )

        ShowcaseDivider()

        // 8. Error – no icon
        ShowcaseSectionHeader("8. Error – no icon (static)")
        TextField(
            text = textErrorNoIco, onTextChange = { textErrorNoIco = it },
            hint = "Enter email", title = "Email",
            isError = true,
            errorMessage = "This field is required",
            enabled = !isDisabled, readOnly = isReadOnly,
            onClickLeadingIcon = null,
        )

        ShowcaseDivider()

        // 9. Disabled (static)
        ShowcaseSectionHeader("9. Disabled (static)")
        TextField(
            text = textDisabled, onTextChange = { textDisabled = it },
            hint = "Enter text", title = "Disabled field",
            leadingIcon  = ColorPainter(Color.Black),
            trailingIcon = ColorPainter(Color.Black),
            enabled = false,
            onClickLeadingIcon = null,
        )

        ShowcaseDivider()

        // 10. Read-only (static)
        ShowcaseSectionHeader("10. Read-only (static)")
        TextField(
            text = textReadOnly, onTextChange = { textReadOnly = it },
            hint = "Enter text", title = "Read-only field",
            readOnly = true,
            onClickLeadingIcon = null,
        )

        ShowcaseDivider()

        // 11. Single line
        ShowcaseSectionHeader("11. Single line")
        TextField(
            text = textSingle, onTextChange = { textSingle = it },
            hint = "Enter text", title = "Single line",
            singleLine = true,
            isError = isError, enabled = !isDisabled, readOnly = isReadOnly,
            errorMessage = if (isError) "Required" else null,
            onClickLeadingIcon = null,
        )

        ShowcaseDivider()

        // 12. Multi-line
        ShowcaseSectionHeader("12. Multi-line")
        TextField(
            text = textMulti, onTextChange = { textMulti = it },
            hint = "Enter text", title = "Multi-line",
            minLines = 3, maxLines = 5,
            isError = isError, enabled = !isDisabled, readOnly = isReadOnly,
            errorMessage = if (isError) "Required" else null,
            onClickLeadingIcon = null,
        )

        ShowcaseDivider()

        // 13. All features combined
        ShowcaseSectionHeader("13. All features combined")
        TextField(
            text = textAll, onTextChange = { textAll = it },
            hint = "Enter email", title = "Email Address", tipText = "Required",
            leadingIcon  = ColorPainter(Color.Black),
            trailingIcon = ColorPainter(Color.Black),
            isError = isError, enabled = !isDisabled, readOnly = isReadOnly,
            errorMessage = if (isError) "Invalid email" else null,
            errorIcon    = if (isError) ColorPainter(Color.Black) else null,
            singleLine = true,
            onClickLeadingIcon = null,
            onClickTrailingIcon = { textAll = "" },
        )

        Spacer(Modifier.height(8.dp))
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

@Composable
private fun ShowcaseSectionHeader(title: String) {
    BasicText(
        text     = title,
        style    = Theme.typography.body.large.copy(color = Theme.colors.primary),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun StateChip(label: String, active: Boolean, onClick: () -> Unit) {
    val bg     = if (active) Theme.colors.primary else Theme.colors.backGround
    val border = if (active) Theme.colors.primary else Theme.colors.hint
    val text   = if (active) Theme.colors.onPrimary else Theme.colors.hint
    Box(
        modifier = Modifier
            .clip(Theme.shapes.small)
            .background(bg)
            .border(1.dp, border, Theme.shapes.small)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(text = label, style = Theme.typography.body.small.copy(color = text))
    }
}

@Composable
private fun ShowcaseDivider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Theme.colors.hint.copy(alpha = 0.3f)),
    )
}

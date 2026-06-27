
package com.example.designsystem.components.button

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
 * Internal preview-only showcase. Shows every button variant with live-togglable
 * Disabled / Loading states — tap the pill chips to flip them while the preview
 * is running on a device.
 */
@Composable
internal fun ButtonShowcaseScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {

        // ── Primary Button ────────────────────────────────────────────────────
        ShowcaseSectionHeader("Primary Button")
        PrimaryButtonStates()

        ShowcaseDivider()

        // ── Secondary Button ──────────────────────────────────────────────────
        ShowcaseSectionHeader("Secondary Button")
        SecondaryButtonStates()

        ShowcaseDivider()

        // ── Icon Button ───────────────────────────────────────────────────────
        ShowcaseSectionHeader("Icon Button")
        IconButtonStates()

        Spacer(Modifier.height(8.dp))
    }
}

// ─── Per-component state blocks ───────────────────────────────────────────────

@Composable
private fun PrimaryButtonStates() {
    var isDisabled by remember { mutableStateOf(false) }
    var isLoading  by remember { mutableStateOf(false) }

    StateToggleRow(
        isDisabled = isDisabled, onToggleDisabled = { isDisabled = !isDisabled },
        isLoading  = isLoading,  onToggleLoading  = { isLoading  = !isLoading  },
    )

    Spacer(Modifier.height(8.dp))

    // Normal (always visible regardless of toggles, for comparison)
    ButtonStateRow("Normal") {
        PrimaryButton(
            caption  = "Button",
            onClick  = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
    ButtonStateRow("Interactive") {
        PrimaryButton(
            caption    = "Button",
            onClick    = {},
            modifier   = Modifier.fillMaxWidth(),
            isDisabled = isDisabled,
            isLoading  = isLoading,
        )
    }
    ButtonStateRow("With Icon") {
        PrimaryButton(
            caption      = "Button",
            onClick      = {},
            modifier     = Modifier.fillMaxWidth(),
            iconPainter  = ColorPainter(Color.Black),
            isDisabled   = isDisabled,
            isLoading    = isLoading,
        )
    }
    // Static states always shown
    ButtonStateRow("Disabled") {
        PrimaryButton(
            caption    = "Button",
            onClick    = {},
            modifier   = Modifier.fillMaxWidth(),
            isDisabled = true,
        )
    }
    ButtonStateRow("Loading") {
        PrimaryButton(
            caption   = "Button",
            onClick   = {},
            modifier  = Modifier.fillMaxWidth(),
            isLoading = true,
        )
    }
}

@Composable
private fun SecondaryButtonStates() {
    var isDisabled by remember { mutableStateOf(false) }
    var isLoading  by remember { mutableStateOf(false) }

    StateToggleRow(
        isDisabled = isDisabled, onToggleDisabled = { isDisabled = !isDisabled },
        isLoading  = isLoading,  onToggleLoading  = { isLoading  = !isLoading  },
    )

    Spacer(Modifier.height(8.dp))

    ButtonStateRow("Normal") {
        SecondaryButton(
            caption  = "Button",
            onClick  = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
    ButtonStateRow("Interactive") {
        SecondaryButton(
            caption    = "Button",
            onClick    = {},
            modifier   = Modifier.fillMaxWidth(),
            isDisabled = isDisabled,
            isLoading  = isLoading,
        )
    }
    ButtonStateRow("With Icon") {
        SecondaryButton(
            caption     = "Button",
            onClick     = {},
            modifier    = Modifier.fillMaxWidth(),
            iconPainter = ColorPainter(Color.Black),
            isDisabled  = isDisabled,
            isLoading   = isLoading,
        )
    }
    ButtonStateRow("Disabled") {
        SecondaryButton(
            caption    = "Button",
            onClick    = {},
            modifier   = Modifier.fillMaxWidth(),
            isDisabled = true,
        )
    }
    ButtonStateRow("Loading") {
        SecondaryButton(
            caption   = "Button",
            onClick   = {},
            modifier  = Modifier.fillMaxWidth(),
            isLoading = true,
        )
    }
}

@Composable
private fun IconButtonStates() {
    var isDisabled by remember { mutableStateOf(false) }

    // single chip toggle for disabled
    StateChip(
        label    = "Disabled",
        active   = isDisabled,
        onClick  = { isDisabled = !isDisabled },
    )

    Spacer(Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LabeledIcon("Normal") {
            IconButton(onClick = {}, icon = ColorPainter(Color.Black))
        }
        LabeledIcon("Interactive") {
            IconButton(
                onClick    = {},
                icon       = ColorPainter(Color.Black),
                isDisabled = isDisabled,
            )
        }
        LabeledIcon("Disabled") {
            IconButton(
                onClick    = {},
                icon       = ColorPainter(Color.Black),
                isDisabled = true,
            )
        }
        LabeledIcon("Alt Color") {
            IconButton(
                onClick         = {},
                icon            = ColorPainter(Color.Black),
                containerColor  = Theme.colors.secondary,
            )
        }
    }
}

// ─── Toggle row ───────────────────────────────────────────────────────────────

@Composable
private fun StateToggleRow(
    isDisabled: Boolean, onToggleDisabled: () -> Unit,
    isLoading:  Boolean, onToggleLoading:  () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StateChip(label = "Disabled", active = isDisabled, onClick = onToggleDisabled)
        StateChip(label = "Loading",  active = isLoading,  onClick = onToggleLoading)
    }
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
        BasicText(
            text  = label,
            style = Theme.typography.body.small.copy(color = text),
        )
    }
}

// ─── Layout helpers ───────────────────────────────────────────────────────────

@Composable
private fun ButtonStateRow(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BasicText(
            text     = label,
            style    = Theme.typography.body.small.copy(color = Theme.colors.hint),
            modifier = Modifier.fillMaxWidth(0.22f),
        )
        Column(modifier = Modifier.fillMaxWidth()) { content() }
    }
}

@Composable
private fun LabeledIcon(label: String, content: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        content()
        Spacer(Modifier.height(4.dp))
        BasicText(text = label, style = Theme.typography.body.small.copy(color = Theme.colors.hint))
    }
}

@Composable
private fun ShowcaseSectionHeader(title: String) {
    BasicText(
        text     = title,
        style    = Theme.typography.body.large.copy(color = Theme.colors.primary),
        modifier = Modifier.fillMaxWidth(),
    )
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

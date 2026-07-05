package com.troves.presintation.ui.aichat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowUp
import com.composables.icons.lucide.Image
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mic
import com.troves.designsystem.components.textfield.TextField
import com.troves.designsystem.theme.Theme

@Composable
fun ChatInputBar(
    value: String,
    hint: String,
    canSend: Boolean,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttachImage: () -> Unit,
    onMic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextField(
            text = value,
            onTextChange = onValueChange,
            modifier = Modifier.weight(1f),
            hint = hint,
            enabled = enabled,
            singleLine = true,
            containerColor = Theme.colors.surface,
            borderColor = Color.Transparent,
            onFocusBorderColor = Color.Transparent,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
            fieldHeight = 52.dp,
            shape = RoundedCornerShape(28.dp),
            leadingIcon = rememberVectorPainter(Lucide.Image),
            leadingIconColor = Theme.colors.secondaryFont,
            onClickLeadingIcon = onAttachImage,
            trailingIcon = rememberVectorPainter(Lucide.Mic),
            trailingIconColor = Theme.colors.secondaryFont,
            onClickTrailingIcon = onMic,
        )

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (canSend) Theme.colors.primary else Theme.colors.surface)
                .clickable(enabled = canSend, onClick = onSend),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = rememberVectorPainter(Lucide.ArrowUp),
                contentDescription = "Send",
                tint = Theme.colors.onPrimary,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

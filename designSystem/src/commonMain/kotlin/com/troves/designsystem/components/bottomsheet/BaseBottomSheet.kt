package com.troves.designsystem.components.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    closeIcon: Painter? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = Theme.colors.surface,
        contentColor = Theme.colors.primaryFont,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Theme.colors.hint) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            if (title != null) {
                BottomSheetHeader(
                    title = title,
                    closeIcon = closeIcon,
                    onClose = onDismissRequest,
                )
            }

            content()

            if (footer != null) {
                Box(modifier = Modifier.padding(Theme.spacing.medium)) {
                    footer()
                }
            }
        }
    }
}

@Composable
private fun BottomSheetHeader(
    title: String,
    closeIcon: Painter?,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = Theme.spacing.medium,
                end = Theme.spacing.medium,
                top = Theme.spacing.small,
                bottom = Theme.spacing.medium,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        BasicText(
            text = title,
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
        if (closeIcon != null) {
            Box(
                modifier = Modifier
                    .clip(Theme.shapes.medium)
                    .noRippleClickable(onClick = onClose)
                    .size(Theme.size.iconMedium),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = closeIcon,
                    contentDescription = null,
                    tint = Theme.colors.secondaryFont,
                    modifier = Modifier.size(Theme.size.iconMedium),
                )
            }
        }
    }
}

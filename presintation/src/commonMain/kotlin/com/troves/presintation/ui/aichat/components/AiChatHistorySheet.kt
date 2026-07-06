package com.troves.presintation.ui.aichat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Trash2
import com.troves.designsystem.components.dialog.TrovesDialog
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.aichat.ConversationSummaryUi
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.ai_history_cancel
import troves.presintation.generated.resources.ai_history_delete
import troves.presintation.generated.resources.ai_history_delete_confirm
import troves.presintation.generated.resources.ai_history_delete_message
import troves.presintation.generated.resources.ai_history_delete_title
import troves.presintation.generated.resources.ai_history_empty
import troves.presintation.generated.resources.ai_history_empty_subtitle
import troves.presintation.generated.resources.ai_history_error
import troves.presintation.generated.resources.ai_history_guest
import troves.presintation.generated.resources.ai_history_new_chat
import troves.presintation.generated.resources.ai_history_title
import troves.presintation.generated.resources.ai_retry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatHistorySheet(
    loading: Boolean,
    requiresLogin: Boolean,
    error: String?,
    conversations: List<ConversationSummaryUi>,
    onSelect: (String) -> Unit,
    onDelete: (String) -> Unit,
    onNewChat: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var pendingDelete by remember { mutableStateOf<ConversationSummaryUi?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Theme.colors.backGround,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.ai_history_title),
                    style = Theme.typography.title,
                    color = Theme.colors.primaryFont,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onNewChat) {
                    Icon(
                        painter = rememberVectorPainter(Lucide.Plus),
                        contentDescription = null,
                        tint = Theme.colors.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = stringResource(Res.string.ai_history_new_chat),
                        style = Theme.typography.hint.medium,
                        color = Theme.colors.primary,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    loading -> CircularProgressIndicator(color = Theme.colors.primary)

                    error != null -> HistoryMessage(
                        title = error,
                        subtitle = null,
                        action = stringResource(Res.string.ai_retry) to onRetry,
                    )

                    requiresLogin -> HistoryMessage(
                        title = stringResource(Res.string.ai_history_guest),
                        subtitle = null,
                        action = null,
                    )

                    conversations.isEmpty() -> HistoryMessage(
                        title = stringResource(Res.string.ai_history_empty),
                        subtitle = stringResource(Res.string.ai_history_empty_subtitle),
                        action = null,
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(conversations, key = { it.id }) { item ->
                            ConversationRow(
                                item = item,
                                onSelect = { onSelect(item.id) },
                                onDelete = { pendingDelete = item },
                            )
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { target ->
        TrovesDialog(
            title = stringResource(Res.string.ai_history_delete_title),
            message = stringResource(Res.string.ai_history_delete_message),
            confirmText = stringResource(Res.string.ai_history_delete_confirm),
            dismissText = stringResource(Res.string.ai_history_cancel),
            confirmColor = Theme.colors.error,
            onConfirm = {
                onDelete(target.id)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null },
        )
    }
}

@Composable
private fun ConversationRow(
    item: ConversationSummaryUi,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (item.isActive) Theme.colors.surface else Theme.colors.backGround)
            .clickable(onClick = onSelect)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Theme.colors.surface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = rememberVectorPainter(Lucide.MessageCircle),
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(18.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = Theme.typography.hint.medium,
                color = Theme.colors.primaryFont,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (item.timeLabel.isNotBlank()) {
                Text(
                    text = item.timeLabel,
                    style = Theme.typography.hint.small,
                    color = Theme.colors.secondaryFont,
                    maxLines = 1,
                )
            }
        }

        Icon(
            painter = rememberVectorPainter(Lucide.Trash2),
            contentDescription = stringResource(Res.string.ai_history_delete),
            tint = Theme.colors.secondaryFont,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = onDelete)
                .padding(6.dp)
                .size(18.dp),
        )
    }
}

@Composable
private fun HistoryMessage(
    title: String,
    subtitle: String?,
    action: Pair<String, () -> Unit>?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            painter = rememberVectorPainter(Lucide.MessageCircle),
            contentDescription = null,
            tint = Theme.colors.secondaryFont,
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = title,
            style = Theme.typography.hint.medium,
            color = Theme.colors.primaryFont,
        )
        subtitle?.let {
            Text(
                text = it,
                style = Theme.typography.hint.small,
                color = Theme.colors.secondaryFont,
            )
        }
        action?.let { (label, onClick) ->
            TextButton(onClick = onClick) {
                Text(
                    text = label,
                    style = Theme.typography.hint.medium,
                    color = Theme.colors.primary,
                )
            }
        }
    }
}

package com.troves.presintation.ui.aichat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme

@Composable
fun RateLimitBanner(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = message,
        style = Theme.typography.hint.medium,
        color = Theme.colors.onWarning,
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.warning)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

@Composable
fun ErrorRetryBar(
    message: String,
    retryLabel: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.error.copy(alpha = 0.12f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = message,
            style = Theme.typography.hint.medium,
            color = Theme.colors.error,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = retryLabel,
            style = Theme.typography.body.medium,
            color = Theme.colors.primary,
            modifier = Modifier.clickable(onClick = onRetry),
        )
    }
}

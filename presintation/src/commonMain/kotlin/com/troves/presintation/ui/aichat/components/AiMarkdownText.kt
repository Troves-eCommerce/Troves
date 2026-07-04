package com.troves.presintation.ui.aichat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme


@Composable
fun AiMarkdownText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val lines = text.split("\n")
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (raw in lines) {
            val line = raw.trimEnd()
            if (line.isBlank()) continue
            val trimmed = line.trimStart()
            val isBullet = trimmed.startsWith("• ") || trimmed.startsWith("- ") || trimmed.startsWith("* ")
            if (isBullet) {
                val content = trimmed.removeRange(0, 2)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "•",
                        style = Theme.typography.body.medium,
                        color = Theme.colors.primaryFont,
                    )
                    Text(
                        text = parseBold(content),
                        style = Theme.typography.body.medium,
                        color = Theme.colors.primaryFont,
                        modifier = Modifier.padding(start = 0.dp),
                    )
                }
            } else {
                Text(
                    text = parseBold(line),
                    style = Theme.typography.body.medium,
                    color = Theme.colors.primaryFont,
                )
            }
        }
    }
}

private fun parseBold(input: String): AnnotatedString = buildAnnotatedString {
    var i = 0
    while (i < input.length) {
        val start = input.indexOf("**", i)
        if (start < 0) {
            append(input.substring(i))
            break
        }
        append(input.substring(i, start))
        val end = input.indexOf("**", start + 2)
        if (end < 0) {
            append(input.substring(start))
            break
        }
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        append(input.substring(start + 2, end))
        pop()
        i = end + 2
    }
}

package com.aiagent.assistant.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiagent.assistant.presentation.theme.IconTone

/** সাম্প্রতিক চ্যাটের একটি সারি: আইকন, শিরোনাম, শেষ বার্তার ঝলক, সময় */
@Composable
fun RecentChatRow(
    title: String,
    preview: String,
    time: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(12.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTile(
                icon = Icons.Rounded.ChatBubble,
                tone = IconTone.PURPLE,
                size = 44.dp,
                iconSize = 22.dp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface,
                    maxLines = 1
                )
                Text(
                    text = preview,
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = cs.onSurfaceVariant
            )
        }
    }
}

package com.aiagent.assistant.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.aiagent.assistant.presentation.theme.AgentDesign
import com.aiagent.assistant.presentation.theme.AgentExtraShapes

/** Home-এর "Ask anything..." লম্বা বার: বাঁয়ে attach, ডানে mic */
@Composable
fun AskBar(
    placeholder: String,
    onClick: () -> Unit,
    onAttachClick: () -> Unit,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val c = AgentDesign.colors
    val haptics = rememberAgentHaptics()
    val shape = AgentExtraShapes.pill
    val fill = Brush.horizontalGradient(listOf(c.glassHighlight, cs.secondary.copy(alpha = 0.28f)))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(shape)
            .background(fill)
            .border(1.dp, cs.secondary.copy(alpha = 0.45f), shape)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(cs.surfaceVariant.copy(alpha = 0.8f))
                .clickable(role = Role.Button) {
                    haptics.tick()
                    onAttachClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.AttachFile, contentDescription = "Attach", tint = cs.onSurface)
        }
        Text(
            text = placeholder,
            style = MaterialTheme.typography.bodyLarge,
            color = cs.onSurfaceVariant,
            modifier = Modifier
                .weight(1f)
                .clip(shape)
                .clickable {
                    haptics.tick()
                    onClick()
                }
                .padding(vertical = 8.dp)
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button) {
                    haptics.confirm()
                    onMicClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Mic, contentDescription = "Voice", tint = cs.onSurface)
        }
    }
}

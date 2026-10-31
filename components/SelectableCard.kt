package com.aiagent.assistant.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiagent.assistant.presentation.theme.AgentDesign

/** একটা বিকল্প বেছে নেওয়ার কার্ড (যেমন AI Model)। বাছা হলে border animate হয়ে রঙিন হয় ও ✓ আসে। */
@Composable
fun SelectableCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tag: String? = null,
    leading: @Composable () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val colors = AgentDesign.colors
    val haptics = rememberAgentHaptics()
    val shape = MaterialTheme.shapes.medium
    val borderColor by animateColorAsState(
        targetValue = if (selected) cs.primary else colors.glassBorder,
        label = "selectableBorder"
    )
    val fillColor by animateColorAsState(
        targetValue = if (selected) cs.primary.copy(alpha = 0.12f) else colors.glassFill,
        label = "selectableFill"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(fillColor)
            .border(if (selected) 1.5.dp else 1.dp, borderColor, shape)
            .clickable(role = Role.RadioButton) {
                haptics.tick()
                onClick()
            }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        leading()
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface
                )
                if (tag != null) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelMedium,
                        color = cs.secondary
                    )
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .then(
                    if (selected) {
                        Modifier.background(colors.brand)
                    } else {
                        Modifier.border(1.5.dp, cs.outline.copy(alpha = 0.7f), CircleShape)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

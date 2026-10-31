package com.aiagent.assistant.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aiagent.assistant.presentation.theme.IconTone

/** রঙিন গ্রেডিয়েন্ট বাক্সের ভেতর সাদা আইকন (Home tile, Settings row, Model card সব জায়গায়) */
@Composable
fun IconTile(
    icon: ImageVector,
    tone: IconTone,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp,
    shape: Shape = RoundedCornerShape(14.dp)
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(tone.brush),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
    }
}

/** Home screen-এর feature কার্ড: মাঝখানে আইকন, নিচে নাম ও ছোট বিবরণ */
@Composable
fun FeatureTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tone: IconTone,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        label = "featureTileScale"
    )
    val haptics = rememberAgentHaptics()

    Column(
        modifier = modifier
            .heightIn(min = 124.dp)
            .scale(scale)
            .glassSurface(shape = MaterialTheme.shapes.medium, tint = tone.top)
            .clickable(
                interactionSource = interaction,
                indication = LocalIndication.current,
                role = Role.Button
            ) {
                haptics.tick()
                onClick()
            }
            .padding(horizontal = 8.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
    ) {
        IconTile(icon = icon, tone = tone)
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = cs.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = cs.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
    }
}

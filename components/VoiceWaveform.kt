package com.aiagent.assistant.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.aiagent.assistant.presentation.theme.AgentDesign

/** মাইকের আওয়াজের মাত্রা দিয়ে আঁকা live waveform (প্রতিটি দণ্ড একটি সাম্প্রতিক মাত্রা) */
@Composable
fun VoiceWaveform(
    levels: List<Float>,
    active: Boolean,
    modifier: Modifier = Modifier.fillMaxWidth().height(56.dp)
) {
    val brush = AgentDesign.colors.brand
    Canvas(modifier = modifier) {
        val count = if (levels.isEmpty()) 1 else levels.size
        val slot = size.width / count
        val barWidth = slot * 0.55f
        levels.forEachIndexed { index, level ->
            val fraction = 0.10f + 0.90f * level.coerceIn(0f, 1f)
            val barHeight = size.height * fraction
            drawRoundRect(
                brush = brush,
                topLeft = Offset(index * slot + (slot - barWidth) / 2f, (size.height - barHeight) / 2f),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f),
                alpha = if (active) 1f else 0.35f
            )
        }
    }
}

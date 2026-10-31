package com.aiagent.assistant.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp

/** লোড হওয়ার সময় ঝিলিক-দেওয়া ফাঁকা বাক্স (chat history, image list ইত্যাদির জন্য) */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1200, easing = LinearEasing)),
        label = "shimmerProgress"
    )
    val base = MaterialTheme.colorScheme.surfaceVariant
    val shine = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f).compositeOver(base)

    Box(
        modifier = modifier
            .clip(shape)
            .drawBehind {
                val w = size.width
                val brush = Brush.linearGradient(
                    colors = listOf(base, shine, base),
                    start = Offset(w * progress, 0f),
                    end = Offset(w * progress + w * 0.6f, size.height)
                )
                drawRect(brush = brush)
            }
    )
}

/** Assistant উত্তর লিখছে বোঝাতে তিনটি লাফানো বিন্দু */
@Composable
fun TypingDots(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val transition = rememberInfiniteTransition(label = "typing")
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(3) { index ->
            val alpha by transition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 600, delayMillis = index * 150),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "typingDot$index"
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = alpha))
            )
        }
    }
}

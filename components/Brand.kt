package com.aiagent.assistant.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiagent.assistant.presentation.theme.AgentDesign

/** অ্যাপের লোগো: গোল কোণের বাক্সে brand gradient ও একটি অক্ষর (App name-এর প্রথম অক্ষর) */
@Composable
fun BrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    letter: String = "M"
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f))
            .background(AgentDesign.colors.brand),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = (size.value * 0.52f).sp
        )
    }
}

@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(AgentDesign.colors.brand),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Person,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}

@Composable
fun OnlineBadge(
    modifier: Modifier = Modifier,
    text: String = "Online"
) {
    val green = AgentDesign.colors.success
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(green)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = green,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** ছোট রোবট মাসকট (Canvas দিয়ে আঁকা)। Promo banner ও খালি screen-এ ব্যবহার হবে। */
@Composable
fun RobotMascot(modifier: Modifier = Modifier.size(72.dp)) {
    val headBrush = Brush.linearGradient(listOf(Color(0xFFB9C4FF), Color(0xFF7C8CFF)))
    val eyeColor = Color(0xFF3DDCFF)
    val earColor = Color(0xFF7C8CFF)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // antenna
        drawLine(
            color = Color(0xFFB9C4FF),
            start = Offset(w * 0.5f, h * 0.18f),
            end = Offset(w * 0.5f, h * 0.07f),
            strokeWidth = w * 0.03f
        )
        drawCircle(color = eyeColor, radius = w * 0.045f, center = Offset(w * 0.5f, h * 0.06f))

        // ears
        drawRoundRect(
            color = earColor,
            topLeft = Offset(w * 0.03f, h * 0.42f),
            size = Size(w * 0.1f, h * 0.24f),
            cornerRadius = CornerRadius(w * 0.05f)
        )
        drawRoundRect(
            color = earColor,
            topLeft = Offset(w * 0.87f, h * 0.42f),
            size = Size(w * 0.1f, h * 0.24f),
            cornerRadius = CornerRadius(w * 0.05f)
        )

        // body hint
        drawRoundRect(
            brush = headBrush,
            topLeft = Offset(w * 0.28f, h * 0.80f),
            size = Size(w * 0.44f, h * 0.18f),
            cornerRadius = CornerRadius(w * 0.1f)
        )

        // head
        drawRoundRect(
            brush = headBrush,
            topLeft = Offset(w * 0.10f, h * 0.18f),
            size = Size(w * 0.80f, h * 0.62f),
            cornerRadius = CornerRadius(w * 0.26f)
        )

        // face plate
        drawRoundRect(
            color = Color(0xFF0B1030),
            topLeft = Offset(w * 0.18f, h * 0.28f),
            size = Size(w * 0.64f, h * 0.42f),
            cornerRadius = CornerRadius(w * 0.18f)
        )

        // eyes
        drawCircle(color = eyeColor, radius = w * 0.075f, center = Offset(w * 0.37f, h * 0.49f))
        drawCircle(color = eyeColor, radius = w * 0.075f, center = Offset(w * 0.63f, h * 0.49f))
    }
}

/** App name-এর প্রথম অক্ষর (লোগোর জন্য) */
fun String.brandLetter(): String = trim().firstOrNull()?.uppercase() ?: "M"

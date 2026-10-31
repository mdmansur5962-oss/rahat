package com.aiagent.assistant.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AgentShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

/** Material-এর বাইরের বাড়তি shape (pill, chat bubble) */
object AgentExtraShapes {
    val pill = RoundedCornerShape(50)

    /** User-এর chat bubble: নিচের ডান কোণ ছোট */
    val bubbleUser = RoundedCornerShape(
        topStart = 22.dp, topEnd = 22.dp, bottomStart = 22.dp, bottomEnd = 6.dp
    )

    /** Assistant-এর chat bubble: নিচের বাম কোণ ছোট */
    val bubbleAssistant = RoundedCornerShape(
        topStart = 22.dp, topEnd = 22.dp, bottomStart = 6.dp, bottomEnd = 22.dp
    )
}

package com.aiagent.assistant.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/** Step 5-এর Settings থেকে haptic চালু/বন্ধ করা যাবে */
val LocalHapticsEnabled = staticCompositionLocalOf { true }

class AgentHaptics(
    private val feedback: HapticFeedback,
    private val enabled: Boolean
) {
    /** হালকা ট্যাপ: switch, tab বদলানো */
    fun tick() {
        if (enabled) feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    /** জোরালো: বাটন চাপা, কাজ শেষ */
    fun confirm() {
        if (enabled) feedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }
}

@Composable
fun rememberAgentHaptics(): AgentHaptics {
    val feedback = LocalHapticFeedback.current
    val enabled = LocalHapticsEnabled.current
    return remember(feedback, enabled) { AgentHaptics(feedback, enabled) }
}

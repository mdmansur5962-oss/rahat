package com.aiagent.assistant.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.domain.settings.ActionSpeed
import com.aiagent.assistant.domain.settings.AutonomyMode
import com.aiagent.assistant.presentation.components.AgentSlider
import com.aiagent.assistant.presentation.components.AgentSwitchRow
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.IconTile
import com.aiagent.assistant.presentation.components.SectionHeader
import com.aiagent.assistant.presentation.components.SegmentedPicker
import com.aiagent.assistant.presentation.components.SelectableCard
import com.aiagent.assistant.presentation.theme.IconTone
import kotlin.math.roundToInt

@Composable
fun AgentBehaviorScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val speeds = listOf(ActionSpeed.CAREFUL, ActionSpeed.NORMAL, ActionSpeed.FAST)

    SettingsPage(
        title = "Agent Behavior · এজেন্টের আচরণ",
        subtitle = "কাজ করার ধরন",
        onBack = onBack
    ) {
        SectionHeader(title = "Autonomy · স্বাধীনতা")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SelectableCard(
                title = "Confirm the plan · plan দেখিয়ে শুরু",
                subtitle = "Plan দেখাবে, আপনি OK বা \"শুরু করো\" বললে পুরোটা নিজে করবে",
                selected = settings.autonomyMode == AutonomyMode.CONFIRM_PLAN,
                onClick = { viewModel.update { it.copy(autonomyMode = AutonomyMode.CONFIRM_PLAN) } },
                tag = "(Recommended)",
                leading = { IconTile(icon = Icons.Rounded.PlayArrow, tone = IconTone.GREEN, size = 44.dp) }
            )
            SelectableCard(
                title = "Step by step · ধাপে ধাপে",
                subtitle = "প্রতিটি ধাপের আগে জিজ্ঞেস করবে",
                selected = settings.autonomyMode == AutonomyMode.STEP_BY_STEP,
                onClick = { viewModel.update { it.copy(autonomyMode = AutonomyMode.STEP_BY_STEP) } },
                leading = { IconTile(icon = Icons.Rounded.SkipNext, tone = IconTone.ORANGE, size = 44.dp) }
            )
        }

        SectionHeader(title = "Action speed · কাজের গতি")
        GlassCard {
            SegmentedPicker(
                options = listOf("Careful · সাবধানে", "Normal · স্বাভাবিক", "Fast · দ্রুত"),
                selectedIndex = speeds.indexOf(settings.actionSpeed).coerceAtLeast(0),
                onSelect = { index -> viewModel.update { it.copy(actionSpeed = speeds[index]) } }
            )
            Text(
                text = "প্রতিটি ট্যাপ/টাইপের মাঝে অপেক্ষা: ${settings.actionSpeed.delayMs} ms। " +
                    "ধীর ফোনে বা ভারী অ্যাপে \"Careful\" ভালো কাজ করে।",
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AgentSlider(
                    title = "Retries per step · ব্যর্থ হলে আবার চেষ্টা",
                    value = settings.maxRetries.toFloat(),
                    valueText = { "${it.roundToInt()}" },
                    valueRange = 0f..5f,
                    steps = 4,
                    onCommit = { v -> viewModel.update { it.copy(maxRetries = v.roundToInt()) } }
                )
                AgentSwitchRow(
                    title = "Stop on error · ভুল হলে থামা",
                    subtitle = "চেষ্টা শেষেও ব্যর্থ হলে নিরাপদে থেমে আপনাকে জানাবে",
                    checked = settings.stopOnError,
                    onCheckedChange = { on -> viewModel.update { it.copy(stopOnError = on) } }
                )
                AgentSwitchRow(
                    title = "Show progress bubble · অগ্রগতি বাবল",
                    subtitle = "কাজ চলার সময় ভাসমান বাবলে ধাপ দেখাবে",
                    checked = settings.showProgressBubble,
                    onCheckedChange = { on -> viewModel.update { it.copy(showProgressBubble = on) } }
                )
            }
        }

        SectionHeader(title = "Always on · সবসময় চালু")
        GlassCard(tint = cs.primary) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Rounded.Lock, contentDescription = null, tint = cs.primary)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Sensitive actions always ask first",
                        style = MaterialTheme.typography.titleMedium,
                        color = cs.onSurface
                    )
                    Text(
                        text = "কল, SMS পাঠানো, delete-এর আগে সবসময় আপনার confirmation চাইবে। এটা বন্ধ করা যায় না।",
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant
                    )
                }
            }
        }
        GlassCard(tint = cs.primary) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Rounded.Shield, contentDescription = null, tint = cs.primary)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Never inside banking, OTP or password screens",
                        style = MaterialTheme.typography.titleMedium,
                        color = cs.onSurface
                    )
                    Text(
                        text = "ব্যাংকিং অ্যাপ, OTP ও password screen-এ এজেন্ট কখনো কাজ করবে না। এটাও বন্ধ করা যায় না।",
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant
                    )
                }
            }
        }
    }
}

package com.aiagent.assistant.presentation.skills

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material.icons.rounded.StickyNote2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.GradientButton
import com.aiagent.assistant.presentation.components.IconTile
import com.aiagent.assistant.presentation.components.StatusPill
import com.aiagent.assistant.presentation.settings.SettingsPage
import com.aiagent.assistant.presentation.theme.AgentDesign
import com.aiagent.assistant.presentation.theme.IconTone

internal fun skillVisual(id: String): Pair<ImageVector, IconTone> = when (id) {
    "chrome" -> Icons.Rounded.Public to IconTone.BLUE
    "youtube" -> Icons.Rounded.PlayCircle to IconTone.RED
    "facebook" -> Icons.Rounded.Groups to IconTone.BLUE
    "capcut" -> Icons.Rounded.ContentCut to IconTone.SLATE
    "gallery" -> Icons.Rounded.PhotoLibrary to IconTone.PINK
    "whatsapp" -> Icons.Rounded.Chat to IconTone.GREEN
    "instagram" -> Icons.Rounded.PhotoCamera to IconTone.PINK
    "tiktok" -> Icons.Rounded.MusicNote to IconTone.TEAL
    "gmail" -> Icons.Rounded.Email to IconTone.RED
    "maps" -> Icons.Rounded.Map to IconTone.GREEN
    "play_store" -> Icons.Rounded.ShoppingBag to IconTone.SKY
    "settings" -> Icons.Rounded.Settings to IconTone.SLATE
    "camera" -> Icons.Rounded.CameraAlt to IconTone.PURPLE
    "phone" -> Icons.Rounded.Call to IconTone.GREEN
    "messages" -> Icons.Rounded.Sms to IconTone.BLUE
    "calendar" -> Icons.Rounded.CalendarMonth to IconTone.ORANGE
    "clock" -> Icons.Rounded.Alarm to IconTone.ORANGE
    "notes" -> Icons.Rounded.StickyNote2 to IconTone.ORANGE
    else -> Icons.Rounded.Extension to IconTone.PURPLE
}

/** Universal App Knowledge Engine: assistant কোন অ্যাপ সম্পর্কে কী জানে তার তালিকা */
@Composable
fun SkillsScreen(
    onBack: () -> Unit,
    onOpenSkill: (String) -> Unit,
    onNewSkill: () -> Unit,
    viewModel: SkillsViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val rows by viewModel.rows.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }

    SettingsPage(
        title = "App Skills · অ্যাপ স্কিল",
        subtitle = "assistant কোন অ্যাপ কীভাবে চালায়",
        onBack = onBack
    ) {
        GlassCard(highlighted = true) {
            Text(
                text = "প্রতিটি skill-এ আছে অ্যাপের নাম (বাংলাসহ), কীভাবে কাজ করতে হয় তার টিপস, বোতামের সম্ভাব্য লেখা, আর " +
                    "deep link দিয়ে করা নির্ভরযোগ্য কাজ (যেমন YouTube-এ খোঁজা, Maps-এ রাস্তা, alarm সেট করা)। " +
                    "Task Planner (পরের ধাপ) এগুলো ব্যবহার করে।",
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurface
            )
        }

        rows.forEach { row ->
            val (icon, tone) = skillVisual(row.skill.id)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                onClick = { onOpenSkill(row.skill.id) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconTile(icon = icon, tone = tone, size = 44.dp, iconSize = 22.dp)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = row.skill.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = cs.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = row.skill.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = cs.onSurfaceVariant,
                            maxLines = 1
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            StatusPill(
                                text = if (row.skill.builtIn) "Built-in" else "Custom",
                                color = if (row.skill.builtIn) cs.secondary else cs.tertiary
                            )
                            StatusPill(
                                text = if (row.installedPackage != null) "Installed" else "Not installed",
                                color = if (row.installedPackage != null) AgentDesign.colors.success else cs.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = cs.onSurfaceVariant
                    )
                }
            }
        }

        GradientButton(
            text = "Create custom skill · নিজের skill বানান",
            icon = Icons.Rounded.Add,
            onClick = onNewSkill,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

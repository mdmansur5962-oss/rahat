package com.aiagent.assistant.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.presentation.components.AgentSlider
import com.aiagent.assistant.presentation.components.AgentSwitchRow
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.SectionHeader
import com.aiagent.assistant.presentation.components.SegmentedPicker
import com.aiagent.assistant.presentation.components.SettingsGroup
import com.aiagent.assistant.presentation.components.SettingsRow
import com.aiagent.assistant.presentation.theme.AccentPalette
import com.aiagent.assistant.presentation.theme.IconTone
import com.aiagent.assistant.presentation.theme.ThemeMode
import com.aiagent.assistant.presentation.theme.ThemeViewModel
import kotlin.math.roundToInt

@Composable
fun AppearanceScreen(
    onBack: () -> Unit,
    onOpenDesignSystem: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
    val accent by themeViewModel.accent.collectAsStateWithLifecycle()
    val modes = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)

    SettingsPage(
        title = "Appearance · চেহারা",
        subtitle = "থিম, রঙ, লেখার আকার",
        onBack = onBack
    ) {
        GlassCard {
            Text("Theme · থিম", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
            Box(modifier = Modifier.padding(top = 8.dp)) {
                SegmentedPicker(
                    options = listOf("System · সিস্টেম", "Light · লাইট", "Dark · ডার্ক"),
                    selectedIndex = modes.indexOf(themeMode).coerceAtLeast(0),
                    onSelect = { themeViewModel.setThemeMode(modes[it]) }
                )
            }
            Text(
                text = "Accent color · রঙ",
                style = MaterialTheme.typography.titleMedium,
                color = cs.onSurface,
                modifier = Modifier.padding(top = 16.dp)
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AccentPalette.entries.forEach { palette ->
                    val selected = palette == accent
                    val ring = if (selected) {
                        Modifier.border(3.dp, cs.onBackground, CircleShape)
                    } else {
                        Modifier
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(palette.primary, palette.secondary)))
                            .then(ring)
                            .clickable { themeViewModel.setAccent(palette) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selected) {
                            Icon(Icons.Rounded.Check, contentDescription = palette.nameEn, tint = Color.White)
                        }
                    }
                }
            }
            Text(
                text = "${accent.nameEn} · ${accent.nameBn}",
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        GlassCard {
            AgentSlider(
                title = "Font size · লেখার আকার",
                value = settings.fontScale,
                valueText = { "${(it * 100).roundToInt()}%" },
                valueRange = 0.85f..1.30f,
                steps = 8,
                onCommit = { scale -> settingsViewModel.update { it.copy(fontScale = scale) } }
            )
            Text(
                text = "নমুনা: আমি আপনার AI সহকারী। / Sample: I am your AI assistant.",
                style = MaterialTheme.typography.bodyLarge,
                color = cs.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        GlassCard {
            AgentSwitchRow(
                title = "Haptic feedback · কম্পন",
                subtitle = "বাটন চাপলে হালকা কম্পন",
                checked = settings.hapticsEnabled,
                onCheckedChange = { on -> settingsViewModel.update { it.copy(hapticsEnabled = on) } }
            )
        }

        SectionHeader(title = "Preview · পূর্বদর্শন")
        SettingsGroup {
            SettingsRow(
                icon = Icons.Rounded.Palette,
                tone = IconTone.BLUE,
                title = "Design System",
                subtitle = "সব component একসাথে দেখুন",
                onClick = onOpenDesignSystem,
                showDivider = false
            )
        }
    }
}

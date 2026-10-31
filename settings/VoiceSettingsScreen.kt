package com.aiagent.assistant.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.domain.settings.VoiceLanguage
import com.aiagent.assistant.domain.settings.VoiceStyle
import com.aiagent.assistant.service.voice.SpeakResult
import com.aiagent.assistant.service.voice.SpeechOutput
import com.aiagent.assistant.presentation.components.AgentSlider
import com.aiagent.assistant.presentation.components.AgentSwitchRow
import com.aiagent.assistant.presentation.components.GlassButton
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.IconTile
import com.aiagent.assistant.presentation.components.SectionHeader
import com.aiagent.assistant.presentation.components.SegmentedPicker
import com.aiagent.assistant.presentation.components.SelectableCard
import com.aiagent.assistant.presentation.theme.IconTone
import java.util.Locale
import kotlin.math.roundToInt

private data class VoiceStyleUi(
    val style: VoiceStyle,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tone: IconTone
)

private val voiceStyles = listOf(
    VoiceStyleUi(VoiceStyle.FEMALE, "Female · নারী কণ্ঠ", "Clear and friendly", Icons.Rounded.RecordVoiceOver, IconTone.PINK),
    VoiceStyleUi(VoiceStyle.MALE, "Male · পুরুষ কণ্ঠ", "Calm and steady", Icons.Rounded.Face, IconTone.BLUE),
    VoiceStyleUi(VoiceStyle.CHILD, "Child · শিশু কণ্ঠ", "High and playful", Icons.Rounded.ChildCare, IconTone.ORANGE),
    VoiceStyleUi(VoiceStyle.ROBOT, "Robot · রোবট কণ্ঠ", "Low and mechanical", Icons.Rounded.SmartToy, IconTone.SLATE),
    VoiceStyleUi(VoiceStyle.SOFT, "Soft · নরম কণ্ঠ", "Slow and gentle", Icons.Rounded.Spa, IconTone.TEAL),
    VoiceStyleUi(VoiceStyle.DEEP, "Deep · গম্ভীর কণ্ঠ", "Very low and strong", Icons.Rounded.GraphicEq, IconTone.PURPLE),
    VoiceStyleUi(VoiceStyle.CUSTOM, "Custom · নিজের মতো", "Set speed and pitch yourself", Icons.Rounded.Tune, IconTone.GREEN)
)

@Composable
fun VoiceSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val preview = remember { SpeechOutput(context) }
    DisposableEffect(preview) {
        onDispose { preview.shutdown() }
    }

    fun runPreview(text: String, locale: Locale) {
        val result = preview.speak(
            text, locale, settings.voiceStyle, settings.voiceSpeed, settings.voicePitch
        )
        val message = when (result) {
            SpeakResult.OK -> null
            SpeakResult.NOT_READY -> "Voice engine is not ready yet · ভয়েস ইঞ্জিন এখনো প্রস্তুত নয়, একটু পরে চেষ্টা করুন"
            SpeakResult.LANGUAGE_MISSING -> "This language voice is not installed · এই ভাষার voice ফোনে নেই (Settings > Text-to-speech থেকে নামান)"
        }
        if (message != null) Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    SettingsPage(
        title = "Voice · ভয়েস",
        subtitle = "কণ্ঠের ধরন, গতি ও সুর",
        onBack = onBack
    ) {
        SectionHeader(title = "Listening language · শোনার ভাষা")
        GlassCard {
            val languages = listOf(VoiceLanguage.AUTO, VoiceLanguage.ENGLISH, VoiceLanguage.BANGLA)
            SegmentedPicker(
                options = listOf("Auto · স্বয়ংক্রিয়", "English", "বাংলা"),
                selectedIndex = languages.indexOf(settings.voiceLanguage).coerceAtLeast(0),
                onSelect = { index -> viewModel.update { it.copy(voiceLanguage = languages[index]) } }
            )
            Text(
                text = "Auto মোডে ফোন বাংলা ও ইংরেজি চেনার চেষ্টা করে, তবে সব ফোনে নিখুঁত হয় না। ভুল হলে এখানে বা Live Voice স্ক্রিনে ভাষা নিজে বেছে নিন।",
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        SectionHeader(title = "Voice style · কণ্ঠের ধরন")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            voiceStyles.forEach { ui ->
                SelectableCard(
                    title = ui.title,
                    subtitle = ui.subtitle,
                    selected = settings.voiceStyle == ui.style,
                    onClick = {
                        viewModel.update { s ->
                            if (ui.style == VoiceStyle.CUSTOM) {
                                s.copy(voiceStyle = VoiceStyle.CUSTOM)
                            } else {
                                s.copy(
                                    voiceStyle = ui.style,
                                    voicePitch = ui.style.pitch,
                                    voiceSpeed = ui.style.speed
                                )
                            }
                        }
                    },
                    leading = { IconTile(icon = ui.icon, tone = ui.tone, size = 44.dp) }
                )
            }
        }

        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AgentSlider(
                    title = "Speed · গতি",
                    value = settings.voiceSpeed,
                    valueText = { "${(it * 100).roundToInt()}%" },
                    valueRange = 0.5f..2.0f,
                    onCommit = { v ->
                        viewModel.update { it.copy(voiceSpeed = v, voiceStyle = VoiceStyle.CUSTOM) }
                    }
                )
                AgentSlider(
                    title = "Pitch · সুর",
                    value = settings.voicePitch,
                    valueText = { "${(it * 100).roundToInt()}%" },
                    valueRange = 0.5f..2.0f,
                    onCommit = { v ->
                        viewModel.update { it.copy(voicePitch = v, voiceStyle = VoiceStyle.CUSTOM) }
                    }
                )
            }
        }

        GlassCard {
            Text(
                text = "Preview · শুনে দেখুন",
                style = MaterialTheme.typography.titleMedium,
                color = cs.onSurface
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassButton(
                    text = "English",
                    onClick = {
                        runPreview("Hello! I am ${settings.assistantName}, your assistant.", Locale.US)
                    },
                    modifier = Modifier.weight(1f)
                )
                GlassButton(
                    text = "বাংলা",
                    onClick = {
                        runPreview("হ্যালো! আমি ${settings.assistantName}, আপনার সহকারী।", Locale.forLanguageTag("bn"))
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AgentSwitchRow(
                    title = "Speak replies aloud · উত্তর জোরে পড়া",
                    subtitle = "চ্যাটের উত্তর voice-এ শোনাবে",
                    checked = settings.speakReplies,
                    onCheckedChange = { on -> viewModel.update { it.copy(speakReplies = on) } }
                )
                AgentSwitchRow(
                    title = "Continuous listening · টানা শোনা",
                    subtitle = "একটা উত্তরের পর নিজে থেকে আবার শুনবে (Step 9-এ কাজ শুরু)",
                    checked = settings.continuousListening,
                    onCheckedChange = { on -> viewModel.update { it.copy(continuousListening = on) } }
                )
            }
        }

        Text(
            text = "ফোনে আলাদা পুরুষ/নারী voice ইনস্টল থাকলে (Google Text-to-speech) সেটাই ব্যবহার হয়। না থাকলে সুর ও গতি বদলে " +
                "কাছাকাছি কণ্ঠ বানানো হয়। আরও voice নামাতে ফোনের Settings → Text-to-speech দেখুন।",
            style = MaterialTheme.typography.bodySmall,
            color = cs.onSurfaceVariant
        )
    }
}

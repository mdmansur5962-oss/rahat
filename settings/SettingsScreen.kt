package com.aiagent.assistant.presentation.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.R
import com.aiagent.assistant.domain.settings.AppLanguage
import com.aiagent.assistant.presentation.components.Avatar
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.GradientBackground
import com.aiagent.assistant.presentation.components.ScreenHeader
import com.aiagent.assistant.presentation.components.SettingsGroup
import com.aiagent.assistant.presentation.components.SettingsRow
import com.aiagent.assistant.presentation.navigation.Routes
import com.aiagent.assistant.presentation.navigation.Screen
import com.aiagent.assistant.presentation.theme.IconTone

/** MYRA design-এর Settings (Main) পেজ */
@Composable
fun SettingsScreen(
    onNavigate: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val aiSummary by viewModel.aiSummary.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val languageLabel = when (settings.language) {
        AppLanguage.SYSTEM -> "System default · সিস্টেম অনুযায়ী"
        AppLanguage.ENGLISH -> "English"
        AppLanguage.BANGLA -> "বাংলা"
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenHeader(title = stringResource(R.string.nav_settings))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                onClick = { onNavigate(Screen.Profile.route) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Avatar(size = 56.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName.ifBlank { "Set your name · নাম দিন" },
                            style = MaterialTheme.typography.titleMedium,
                            color = cs.onSurface
                        )
                        Text(
                            text = "Local profile · শুধু এই ফোনে",
                            style = MaterialTheme.typography.bodySmall,
                            color = cs.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = cs.onSurfaceVariant
                    )
                }
            }

            SettingsGroup {
                SettingsRow(
                    icon = Icons.Rounded.AutoAwesome,
                    tone = IconTone.PINK,
                    title = "Assistant · সহকারী",
                    subtitle = "${settings.appName} · ${settings.assistantName} · ${settings.wakeWord}",
                    onClick = { onNavigate(Routes.SETTINGS_ASSISTANT) }
                )
                SettingsRow(
                    icon = Icons.Rounded.Psychology,
                    tone = IconTone.PURPLE,
                    title = "AI Model · এআই মডেল",
                    subtitle = aiSummary ?: "Add a provider · provider যোগ করুন",
                    onClick = { onNavigate(Routes.SETTINGS_AI) }
                )
                SettingsRow(
                    icon = Icons.Rounded.RecordVoiceOver,
                    tone = IconTone.TEAL,
                    title = "Voice · ভয়েস",
                    subtitle = "Voice style, speed, pitch",
                    onClick = { onNavigate(Routes.SETTINGS_VOICE) }
                )
                SettingsRow(
                    icon = Icons.Rounded.SmartToy,
                    tone = IconTone.GREEN,
                    title = "Agent Behavior · এজেন্টের আচরণ",
                    subtitle = "Autonomy, speed, retries",
                    onClick = { onNavigate(Routes.SETTINGS_AGENT) }
                )
                SettingsRow(
                    icon = Icons.Rounded.Bolt,
                    tone = IconTone.TEAL,
                    title = "Background Agent · ব্যাকগ্রাউন্ড এজেন্ট",
                    subtitle = "Bubble, notification, wake word",
                    onClick = { onNavigate(Routes.SETTINGS_BACKGROUND) }
                )
                SettingsRow(
                    icon = Icons.Rounded.Science,
                    tone = IconTone.SKY,
                    title = "Screen Lab · স্ক্রিন ল্যাব",
                    subtitle = "Test screen reading, actions, OCR",
                    onClick = { onNavigate(Routes.SETTINGS_LAB) }
                )
                SettingsRow(
                    icon = Icons.Rounded.Extension,
                    tone = IconTone.ORANGE,
                    title = "App Skills · অ্যাপ স্কিল",
                    subtitle = "Chrome, YouTube, WhatsApp... and your own",
                    onClick = { onNavigate(Routes.SETTINGS_SKILLS) },
                    showDivider = false
                )
            }

            SettingsGroup {
                SettingsRow(
                    icon = Icons.Rounded.Palette,
                    tone = IconTone.BLUE,
                    title = "Appearance · চেহারা",
                    subtitle = "Dark mode, theme, font size",
                    onClick = { onNavigate(Routes.SETTINGS_APPEARANCE) }
                )
                SettingsRow(
                    icon = Icons.Rounded.Language,
                    tone = IconTone.GREEN,
                    title = "Language · ভাষা",
                    subtitle = languageLabel,
                    onClick = { onNavigate(Routes.SETTINGS_LANGUAGE) }
                )
                SettingsRow(
                    icon = Icons.Rounded.Notifications,
                    tone = IconTone.ORANGE,
                    title = "Notifications · নোটিফিকেশন",
                    subtitle = "Opens Android notification settings",
                    onClick = {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // কিছু করার নেই
                        }
                    }
                )
                SettingsRow(
                    icon = Icons.Rounded.Lock,
                    tone = IconTone.RED,
                    title = "Privacy & Security · গোপনীয়তা",
                    subtitle = "Data, permissions, safety",
                    onClick = { onNavigate(Routes.SETTINGS_PRIVACY) },
                    showDivider = false
                )
            }

            SettingsGroup {
                SettingsRow(
                    icon = Icons.Rounded.Info,
                    tone = IconTone.SLATE,
                    title = "About · সম্পর্কে",
                    subtitle = "App version, help & support",
                    onClick = { onNavigate(Routes.SETTINGS_ABOUT) },
                    showDivider = false
                )
            }
        }
    }
}

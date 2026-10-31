package com.aiagent.assistant.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.presentation.components.AgentSwitchRow
import com.aiagent.assistant.presentation.components.AgentTextField
import com.aiagent.assistant.presentation.components.BrandLogo
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.GradientButton
import com.aiagent.assistant.presentation.components.SectionHeader
import com.aiagent.assistant.presentation.components.brandLetter

@Composable
fun AssistantSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var appName by remember(settings.appName) { mutableStateOf(settings.appName) }
    var assistantName by remember(settings.assistantName) { mutableStateOf(settings.assistantName) }
    var wakeWord by remember(settings.wakeWord) { mutableStateOf(settings.wakeWord) }

    val valid = appName.isNotBlank() && assistantName.isNotBlank() && wakeWord.trim().length >= 2
    val changed = appName.trim() != settings.appName ||
        assistantName.trim() != settings.assistantName ||
        wakeWord.trim() != settings.wakeWord

    SettingsPage(
        title = "Assistant · সহকারী",
        subtitle = "নাম ও wake word",
        onBack = onBack
    ) {
        GlassCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BrandLogo(size = 56.dp, letter = appName.brandLetter())
                AgentTextField(
                    value = appName,
                    onValueChange = { appName = it.take(20) },
                    label = "App name · অ্যাপের নাম",
                    isError = appName.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                AgentTextField(
                    value = assistantName,
                    onValueChange = { assistantName = it.take(20) },
                    label = "Assistant name · সহকারীর নাম",
                    isError = assistantName.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                AgentTextField(
                    value = wakeWord,
                    onValueChange = { wakeWord = it.take(30) },
                    label = "Wake word · জাগানোর শব্দ",
                    isError = wakeWord.trim().length < 2,
                    supportingText = "যেমন: Hey MYRA",
                    modifier = Modifier.fillMaxWidth()
                )
                GradientButton(
                    text = "Save · সংরক্ষণ করুন",
                    icon = Icons.Rounded.AutoAwesome,
                    enabled = valid && changed,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.update {
                            it.copy(
                                appName = appName.trim(),
                                assistantName = assistantName.trim(),
                                wakeWord = wakeWord.trim()
                            )
                        }
                        Toast.makeText(context, "Saved · সংরক্ষিত", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        SectionHeader(title = "Wake word · জাগানোর শব্দ")
        GlassCard {
            AgentSwitchRow(
                title = "Listen for wake word · wake word শোনা",
                subtitle = "Live Voice স্ক্রিন খোলা থাকলে এই শব্দ শুনে জেগে ওঠে। অ্যাপ বন্ধ বা ব্যাকগ্রাউন্ডে থাকলে শোনা Step 10-এ আসবে।",
                checked = settings.wakeWordEnabled,
                onCheckedChange = { enabled -> viewModel.update { it.copy(wakeWordEnabled = enabled) } }
            )
        }

        GlassCard(tint = cs.secondary) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Good to know · জেনে রাখুন",
                    style = MaterialTheme.typography.titleMedium,
                    color = cs.onSurface
                )
                Text(
                    text = "App name বদলালে অ্যাপের ভেতরের সব জায়গায় (Home, drawer, About) সাথে সাথে বদলে যায়। " +
                        "ফোনের হোম স্ক্রিনের আইকনের নিচের নাম Android-এ চলতি অবস্থায় বদলানো যায় না।",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant
                )
                Text(
                    text = "Changing the app name updates it everywhere inside the app. " +
                        "The name under the home-screen icon is fixed by Android and cannot be changed on the fly.",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant
                )
            }
        }
    }
}

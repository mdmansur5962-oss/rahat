package com.aiagent.assistant.presentation.settings

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.BuildConfig
import com.aiagent.assistant.presentation.components.BrandLogo
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.OnlineBadge
import com.aiagent.assistant.presentation.components.brandLetter

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    SettingsPage(
        title = "About · সম্পর্কে",
        onBack = onBack
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BrandLogo(size = 88.dp, letter = settings.appName.brandLetter())
            Text(
                text = settings.appName,
                style = MaterialTheme.typography.headlineMedium,
                color = cs.onBackground
            )
            Text(
                text = "AI Assistant · এআই সহকারী",
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onSurfaceVariant
            )
            OnlineBadge()
        }

        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoLine("Version · সংস্করণ", "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                InfoLine("Device · ডিভাইস", Build.MODEL)
                InfoLine("Android", "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                InfoLine("Built with", "Kotlin · Jetpack Compose · Material 3")
            }
        }

        GlassCard(tint = cs.primary) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "What this assistant never does · যা এটি কখনো করবে না",
                    style = MaterialTheme.typography.titleMedium,
                    color = cs.onSurface
                )
                listOf(
                    "ব্যাংকিং অ্যাপের ভেতরে কাজ করা / Work inside banking apps",
                    "OTP পড়া, রাখা বা শেয়ার করা / Read, store or share OTP",
                    "Password, PIN, card-এর তথ্য পড়া / Read passwords, PINs or card details",
                    "নিজে থেকে payment confirm করা / Confirm payments by itself"
                ).forEach { line ->
                    Text(
                        text = "• $line",
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant
                    )
                }
            }
        }

        Text(
            text = "Help & support: আপনার ডেভেলপারের সাথে যোগাযোগ করুন · Contact your developer",
            style = MaterialTheme.typography.bodySmall,
            color = cs.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    val cs = MaterialTheme.colorScheme
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = cs.onSurface)
    }
}

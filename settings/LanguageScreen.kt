package com.aiagent.assistant.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.domain.settings.AppLanguage
import com.aiagent.assistant.presentation.components.IconTile
import com.aiagent.assistant.presentation.components.SelectableCard
import com.aiagent.assistant.presentation.theme.IconTone

@Composable
fun LanguageScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val options = listOf(
        Triple(AppLanguage.SYSTEM, "System default · সিস্টেম অনুযায়ী", "ফোনের ভাষা অনুসরণ করবে"),
        Triple(AppLanguage.ENGLISH, "English", "Use English in the app"),
        Triple(AppLanguage.BANGLA, "বাংলা (Bangla)", "অ্যাপ বাংলায় দেখাবে")
    )

    SettingsPage(
        title = "Language · ভাষা",
        subtitle = "অ্যাপের ভাষা",
        onBack = onBack
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { (language, title, subtitle) ->
                SelectableCard(
                    title = title,
                    subtitle = subtitle,
                    selected = settings.language == language,
                    onClick = { viewModel.update { it.copy(language = language) } },
                    leading = {
                        IconTile(icon = Icons.Rounded.Language, tone = IconTone.GREEN, size = 44.dp)
                    }
                )
            }
        }
        Text(
            text = "Home, নিচের বার ও drawer-এ ভাষা সাথে সাথে বদলায়। Settings-এর পেজগুলো সবসময় দুই ভাষায় লেখা থাকে। " +
                "AI-এর সাথে কথা বলার ভাষা আলাদা: আপনি যে ভাষায় লিখবেন বা বলবেন, assistant সেই ভাষায় উত্তর দেবে।",
            style = MaterialTheme.typography.bodySmall,
            color = cs.onSurfaceVariant
        )
    }
}

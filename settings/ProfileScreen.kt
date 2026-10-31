package com.aiagent.assistant.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.presentation.components.AgentChip
import com.aiagent.assistant.presentation.components.AgentDialog
import com.aiagent.assistant.presentation.components.AgentTextField
import com.aiagent.assistant.presentation.components.Avatar
import com.aiagent.assistant.presentation.components.GradientBackground
import com.aiagent.assistant.presentation.components.ScreenHeader
import com.aiagent.assistant.presentation.components.SettingsGroup
import com.aiagent.assistant.presentation.components.SettingsRow
import com.aiagent.assistant.presentation.components.StatRow
import com.aiagent.assistant.presentation.navigation.Routes
import com.aiagent.assistant.presentation.theme.IconTone
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.dp

/** Profile ট্যাব। অ্যাকাউন্ট/লগইন নেই, তাই নাম শুধু এই ফোনে থাকে; সংখ্যাগুলো Step 6 থেকে সত্যি হবে। */
@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val userName by viewModel.userName.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf("") }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenHeader(title = "Profile · প্রোফাইল")

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Avatar(size = 96.dp)
                Text(
                    text = userName.ifBlank { "Your name · আপনার নাম" },
                    style = MaterialTheme.typography.headlineSmall,
                    color = cs.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Local profile · শুধু এই ফোনে সংরক্ষিত",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant
                )
                AgentChip(
                    text = "Edit name · নাম বদলান",
                    icon = Icons.Rounded.Edit,
                    onClick = {
                        draft = userName
                        showDialog = true
                    }
                )
            }

            StatRow(
                stats = listOf(
                    "0" to "Conversations",
                    "0" to "Images Created",
                    "0" to "AI Agents"
                )
            )

            SettingsGroup {
                SettingsRow(
                    icon = Icons.Rounded.Person,
                    tone = IconTone.BLUE,
                    title = "Edit Profile · প্রোফাইল",
                    subtitle = "Name",
                    onClick = {
                        draft = userName
                        showDialog = true
                    }
                )
                SettingsRow(
                    icon = Icons.Rounded.Settings,
                    tone = IconTone.PURPLE,
                    title = "Settings · সেটিংস",
                    subtitle = "Assistant, voice, appearance",
                    onClick = { onNavigate(Routes.SETTINGS) }
                )
                SettingsRow(
                    icon = Icons.Rounded.Security,
                    tone = IconTone.RED,
                    title = "Permission Center · পারমিশন সেন্টার",
                    subtitle = "Permissions",
                    onClick = { onNavigate(Routes.PERMISSIONS) }
                )
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

    if (showDialog) {
        AgentDialog(
            title = "What should I call you? · আপনাকে কী বলে ডাকব?",
            onDismiss = { showDialog = false },
            icon = Icons.Rounded.Person,
            confirmText = "Save · সংরক্ষণ",
            onConfirm = {
                viewModel.setUserName(draft)
                showDialog = false
            },
            dismissText = "Cancel · বাতিল"
        ) {
            AgentTextField(
                value = draft,
                onValueChange = { draft = it.take(30) },
                label = "Your name · আপনার নাম",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

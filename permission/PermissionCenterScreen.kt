package com.aiagent.assistant.presentation.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.presentation.theme.agentBackgroundBrush

@Composable
fun PermissionCenterScreen(
    onBack: () -> Unit,
    viewModel: PermissionViewModel = hiltViewModel()
) {
    PermissionEffectsHandler(viewModel)
    val state by viewModel.state.collectAsStateWithLifecycle()

    val special = state.items.filter { it.def.highlighted }
    val otherRequired = state.items.filter { it.def.required && !it.def.highlighted }
    val optional = state.items.filter { !it.def.required }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(agentBackgroundBrush()),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                }
                Column {
                    Text(
                        text = "Permission Center",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "পারমিশন সেন্টার",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        item { ProgressCard(state) }
        item {
            GuidedButtons(
                state = state,
                onStartRequired = { viewModel.onEvent(PermissionEvent.StartGuided(false)) },
                onStartEverything = { viewModel.onEvent(PermissionEvent.StartGuided(true)) },
                onStop = { viewModel.onEvent(PermissionEvent.StopGuided) }
            )
        }
        item { SafetyCard() }
        item { RestrictedSettingsCard(onOpenAppInfo = { viewModel.onEvent(PermissionEvent.OpenAppDetails) }) }

        item {
            SectionTitle(
                en = "Special permissions (full agent mode)",
                bn = "বিশেষ permission (full agent mode-এর জন্য)"
            )
        }
        items(special, key = { it.def.id.name }) { item ->
            PermissionCard(
                item = item,
                onEnable = { viewModel.onEvent(PermissionEvent.Enable(item.def.id)) },
                onOpenSettings = { viewModel.onEvent(PermissionEvent.OpenSettings(item.def.id)) }
            )
        }

        item { SectionTitle(en = "Other required", bn = "অন্যান্য প্রয়োজনীয়") }
        items(otherRequired, key = { it.def.id.name }) { item ->
            PermissionCard(
                item = item,
                onEnable = { viewModel.onEvent(PermissionEvent.Enable(item.def.id)) },
                onOpenSettings = { viewModel.onEvent(PermissionEvent.OpenSettings(item.def.id)) }
            )
        }

        item {
            SectionTitle(
                en = "Optional (enable when you need the feature)",
                bn = "ঐচ্ছিক (দরকার হলে চালু করুন)"
            )
        }
        items(optional, key = { it.def.id.name }) { item ->
            PermissionCard(
                item = item,
                onEnable = { viewModel.onEvent(PermissionEvent.Enable(item.def.id)) },
                onOpenSettings = { viewModel.onEvent(PermissionEvent.OpenSettings(item.def.id)) }
            )
        }

        item { SectionTitle(en = "Custom permissions", bn = "নিজের যোগ করা permission") }
        item {
            CustomPermissionsSection(
                items = state.customItems,
                onAdd = { viewModel.onEvent(PermissionEvent.AddCustom(it)) },
                onEnable = { viewModel.onEvent(PermissionEvent.EnableCustom(it)) },
                onRemove = { viewModel.onEvent(PermissionEvent.RemoveCustom(it)) }
            )
        }
    }
}

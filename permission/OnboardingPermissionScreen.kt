package com.aiagent.assistant.presentation.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.presentation.theme.agentAccentBrush
import com.aiagent.assistant.presentation.theme.agentBackgroundBrush

/** প্রথমবার অ্যাপ খুললে এই screen দেখায়। Skip করলেও পরে Settings → Permission Center থেকে চালু করা যাবে। */
@Composable
fun OnboardingPermissionScreen(
    onFinished: () -> Unit,
    viewModel: PermissionViewModel = hiltViewModel()
) {
    PermissionEffectsHandler(viewModel)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val special = state.items.filter { it.def.highlighted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(agentBackgroundBrush()),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(agentAccentBrush())
                )
                Text(
                    text = "Welcome · স্বাগতম",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "To work like a real assistant, the app needs a few permissions. " +
                        "Each one is explained below, and you stay in control.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "সত্যিকারের assistant-এর মতো কাজ করতে অ্যাপের কয়েকটা permission লাগবে। " +
                        "প্রতিটার কারণ নিচে লেখা আছে, নিয়ন্ত্রণ সবসময় আপনার হাতে।",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        item { SafetyCard() }
        item { ProgressCard(state) }
        item {
            GuidedButtons(
                state = state,
                onStartRequired = { viewModel.onEvent(PermissionEvent.StartGuided(false)) },
                onStartEverything = {},
                onStop = { viewModel.onEvent(PermissionEvent.StopGuided) },
                showEverythingButton = false
            )
        }
        item { RestrictedSettingsCard(onOpenAppInfo = { viewModel.onEvent(PermissionEvent.OpenAppDetails) }) }

        item {
            SectionTitle(
                en = "The four key permissions",
                bn = "চারটি মূল permission"
            )
        }
        items(special, key = { it.def.id.name }) { item ->
            PermissionCard(
                item = item,
                onEnable = { viewModel.onEvent(PermissionEvent.Enable(item.def.id)) },
                onOpenSettings = { viewModel.onEvent(PermissionEvent.OpenSettings(item.def.id)) }
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.allRequiredGranted) {
                    Button(
                        onClick = onFinished,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            text = "Continue · চালিয়ে যান",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onFinished,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            text = "Skip for now · পরে করব",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Text(
                        text = "পরে Settings → Permission Center থেকে চালু করতে পারবেন · " +
                            "You can enable them later from Settings → Permission Center",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

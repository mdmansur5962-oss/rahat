package com.aiagent.assistant.presentation.voice

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.domain.settings.VoiceLanguage
import com.aiagent.assistant.presentation.components.AgentChip
import com.aiagent.assistant.presentation.components.AiOrb
import com.aiagent.assistant.presentation.components.GlassButton
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.GradientBackground
import com.aiagent.assistant.presentation.components.GradientButton
import com.aiagent.assistant.presentation.components.OrbState
import com.aiagent.assistant.presentation.components.VoiceWaveform
import com.aiagent.assistant.presentation.components.rememberAgentHaptics
import com.aiagent.assistant.presentation.theme.AgentDesign

/** Live Voice: orb, waveform, বলা কথা ও উত্তর, আর মাইক বোতাম */
@Composable
fun VoiceScreen(
    onClose: () -> Unit,
    onOpenChat: (Long) -> Unit,
    viewModel: VoiceViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptics = rememberAgentHaptics()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onEvent(VoiceEvent.PermissionResult(granted))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                VoiceEffect.RequestMicPermission -> permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                VoiceEffect.WakeDetected -> haptics.confirm()
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.onEvent(VoiceEvent.ScreenOpened)
    }
    // স্ক্রিন থেকে সরে গেলে মাইক ও কথা বন্ধ
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        viewModel.onEvent(VoiceEvent.ScreenPaused)
    }

    val orbState = when (state.phase) {
        VoicePhase.IDLE -> OrbState.IDLE
        VoicePhase.LISTENING -> OrbState.LISTENING
        VoicePhase.THINKING -> OrbState.THINKING
        VoicePhase.SPEAKING -> OrbState.SPEAKING
    }
    val phaseLabel = when (state.phase) {
        VoicePhase.IDLE -> "Tap the mic to talk · কথা বলতে মাইকে ট্যাপ করুন"
        VoicePhase.LISTENING ->
            if (state.waitingForWakeWord) {
                "Say \"${state.wakeWord}\" · \"${state.wakeWord}\" বলুন"
            } else {
                "Listening… · শুনছি"
            }
        VoicePhase.THINKING -> "Thinking… · ভাবছি"
        VoicePhase.SPEAKING -> "Speaking… · বলছি"
    }
    val listening = state.phase == VoicePhase.LISTENING

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ───── top bar ─────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = cs.onBackground)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Live Voice · লাইভ ভয়েস",
                        style = MaterialTheme.typography.titleMedium,
                        color = cs.onBackground
                    )
                    Text(
                        text = state.assistantName,
                        style = MaterialTheme.typography.labelMedium,
                        color = cs.onSurfaceVariant
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AgentChip(
                    text = "Auto · স্বয়ংক্রিয়",
                    selected = state.language == VoiceLanguage.AUTO,
                    onClick = { viewModel.onEvent(VoiceEvent.SetLanguage(VoiceLanguage.AUTO)) }
                )
                AgentChip(
                    text = "English",
                    selected = state.language == VoiceLanguage.ENGLISH,
                    onClick = { viewModel.onEvent(VoiceEvent.SetLanguage(VoiceLanguage.ENGLISH)) }
                )
                AgentChip(
                    text = "বাংলা",
                    selected = state.language == VoiceLanguage.BANGLA,
                    onClick = { viewModel.onEvent(VoiceEvent.SetLanguage(VoiceLanguage.BANGLA)) }
                )
            }

            // ───── middle ─────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AiOrb(
                    orbSize = 220.dp,
                    state = orbState,
                    level = if (listening) state.level else 0f,
                    onClick = { viewModel.onEvent(VoiceEvent.MicTapped) }
                )
                Text(
                    text = phaseLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = cs.onBackground,
                    textAlign = TextAlign.Center
                )
                VoiceWaveform(
                    levels = state.levels,
                    active = listening,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )

                val spoken = if (state.partialText.isNotEmpty()) state.partialText else state.lastUserText
                if (spoken.isNotEmpty()) {
                    Text(
                        text = "“$spoken”",
                        style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                        color = if (state.partialText.isNotEmpty()) cs.secondary else cs.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
                if (state.replyText.isNotEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                        Text(
                            text = state.replyText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = cs.onSurface
                        )
                    }
                }

                val message = state.message
                if (message != null) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable { viewModel.onEvent(VoiceEvent.DismissMessage) }
                            .padding(8.dp)
                    )
                }

                if (!state.micAvailable) {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                        Text(
                            text = "Speech recognition is not available on this phone · আপনার ফোনে speech service নেই। " +
                                "Google অ্যাপ বা Speech Services ইনস্টল করুন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = cs.onSurface
                        )
                    }
                }
                if (state.needsPermission) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        highlighted = true
                    ) {
                        Text(
                            text = "Microphone permission needed · মাইক্রোফোনের অনুমতি লাগবে",
                            style = MaterialTheme.typography.titleMedium,
                            color = cs.onSurface
                        )
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GradientButton(
                                text = "Allow microphone · অনুমতি দিন",
                                onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            GlassButton(
                                text = "Open app settings · সেটিংস খুলুন",
                                onClick = {
                                    try {
                                        context.startActivity(
                                            Intent(
                                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.parse("package:${context.packageName}")
                                            )
                                        )
                                    } catch (e: Exception) {
                                        // কিছু করার নেই
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // ───── controls ─────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AgentChip(
                    text = "Auto-listen · টানা শোনা",
                    selected = state.continuous,
                    onClick = { viewModel.onEvent(VoiceEvent.ToggleContinuous) }
                )
                AgentChip(
                    text = "Speak replies · জোরে পড়া",
                    selected = state.speakReplies,
                    onClick = { viewModel.onEvent(VoiceEvent.ToggleSpeak) }
                )
                AgentChip(
                    text = "Wake word · জাগানোর শব্দ",
                    selected = state.wakeWordEnabled,
                    onClick = { viewModel.onEvent(VoiceEvent.ToggleWakeWord) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                val chatId = state.chatId
                if (chatId != null) {
                    GlassButton(
                        text = "Open chat · চ্যাট খুলুন",
                        onClick = { onOpenChat(chatId) }
                    )
                }
                val micModifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                val micFill = if (listening) micModifier.background(cs.error) else micModifier.background(AgentDesign.colors.brand)
                Box(
                    modifier = micFill.clickable(role = Role.Button) {
                        haptics.confirm()
                        viewModel.onEvent(VoiceEvent.MicTapped)
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (listening) Icons.Rounded.Stop else Icons.Rounded.Mic,
                        contentDescription = if (listening) "Stop" else "Mic",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

package com.aiagent.assistant.presentation.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.R
import com.aiagent.assistant.presentation.chat.formatChatTime
import com.aiagent.assistant.presentation.components.AgentChip
import com.aiagent.assistant.presentation.components.AgentDialog
import com.aiagent.assistant.presentation.components.AgentIconButton
import com.aiagent.assistant.presentation.components.AgentTextField
import com.aiagent.assistant.presentation.components.AiOrb
import com.aiagent.assistant.presentation.components.AskBar
import com.aiagent.assistant.presentation.components.BrandLogo
import com.aiagent.assistant.presentation.components.brandLetter
import com.aiagent.assistant.presentation.components.FeatureTile
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.GradientBackground
import com.aiagent.assistant.presentation.components.GradientButton
import com.aiagent.assistant.presentation.components.PromoBanner
import com.aiagent.assistant.presentation.components.RecentChatRow
import com.aiagent.assistant.presentation.components.RobotMascot
import com.aiagent.assistant.presentation.navigation.Routes
import com.aiagent.assistant.presentation.navigation.Screen
import com.aiagent.assistant.presentation.theme.IconTone

@Composable
fun HomeScreen(
    onOpenDrawer: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val cs = MaterialTheme.colorScheme

    var showNameDialog by remember { mutableStateOf(false) }
    var nameDraft by remember { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.ShowMessage ->
                    Toast.makeText(context, context.getString(effect.messageRes), Toast.LENGTH_SHORT).show()
            }
        }
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ───── Top bar ─────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AgentIconButton(
                    icon = Icons.Rounded.Menu,
                    contentDescription = "Menu",
                    onClick = onOpenDrawer
                )
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BrandLogo(size = 40.dp, letter = state.appName.brandLetter())
                        Column {
                            Text(
                                text = state.appName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = cs.onBackground
                            )
                            Text(
                                text = stringResource(R.string.drawer_tagline),
                                style = MaterialTheme.typography.labelMedium,
                                color = cs.onSurfaceVariant
                            )
                        }
                    }
                }
                AgentIconButton(
                    icon = Icons.Rounded.Notifications,
                    contentDescription = "Notifications",
                    onClick = { viewModel.onEvent(HomeEvent.BellClicked) }
                )
            }

            // ───── Greeting ─────
            val hello = stringResource(R.string.home_greeting_hello)
            val nameColor = cs.onPrimaryContainer
            val greeting = buildAnnotatedString {
                append(hello)
                if (state.userName.isNotBlank()) {
                    append(", ")
                    withStyle(SpanStyle(color = nameColor)) {
                        append(state.userName)
                    }
                }
                append(" 👋")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineMedium,
                    color = cs.onBackground,
                    modifier = Modifier.clickable {
                        nameDraft = state.userName
                        showNameDialog = true
                    }
                )
                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurfaceVariant
                )
                if (state.userName.isBlank()) {
                    AgentChip(
                        text = stringResource(R.string.home_set_name),
                        icon = Icons.Rounded.Edit,
                        onClick = {
                            nameDraft = ""
                            showNameDialog = true
                        }
                    )
                }
            }

            // ───── Orb + live voice ─────
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AiOrb(
                    orbSize = 176.dp,
                    state = state.orbState,
                    onClick = { onNavigate(Routes.voice()) }
                )
                Text(
                    text = stringResource(R.string.home_orb_hint),
                    style = MaterialTheme.typography.labelMedium,
                    color = cs.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                GradientButton(
                    text = stringResource(R.string.home_talk, state.assistantName),
                    subtitle = stringResource(R.string.home_talk_sub),
                    icon = Icons.Rounded.Mic,
                    onClick = { onNavigate(Routes.voice()) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ───── Ask anything ─────
            AskBar(
                placeholder = stringResource(R.string.home_ask_placeholder),
                onClick = { onNavigate(Screen.Chat.route) },
                onAttachClick = { onNavigate(Routes.FILES) },
                onMicClick = { onNavigate(Routes.voice()) }
            )

            // ───── Quick actions ─────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureTile(
                    icon = Icons.Rounded.ChatBubble,
                    title = stringResource(R.string.tile_chat_title),
                    subtitle = stringResource(R.string.tile_chat_sub),
                    tone = IconTone.PURPLE,
                    onClick = { onNavigate(Screen.Chat.route) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                FeatureTile(
                    icon = Icons.Rounded.Image,
                    title = stringResource(R.string.tile_image_title),
                    subtitle = stringResource(R.string.tile_image_sub),
                    tone = IconTone.TEAL,
                    onClick = { onNavigate(Screen.ImageGen.route) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                FeatureTile(
                    icon = Icons.Rounded.SmartToy,
                    title = stringResource(R.string.tile_agents_title),
                    subtitle = stringResource(R.string.tile_agents_sub),
                    tone = IconTone.GREEN,
                    onClick = { onNavigate(Screen.Agents.route) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureTile(
                    icon = Icons.Rounded.UploadFile,
                    title = stringResource(R.string.tile_files_title),
                    subtitle = stringResource(R.string.tile_files_sub),
                    tone = IconTone.SKY,
                    onClick = { onNavigate(Routes.FILES) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                FeatureTile(
                    icon = Icons.Rounded.History,
                    title = stringResource(R.string.tile_history_title),
                    subtitle = stringResource(R.string.tile_history_sub),
                    tone = IconTone.PURPLE,
                    onClick = { onNavigate(Routes.HISTORY) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                FeatureTile(
                    icon = Icons.Rounded.GridView,
                    title = stringResource(R.string.tile_more_title),
                    subtitle = stringResource(R.string.tile_more_sub),
                    tone = IconTone.SLATE,
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            // ───── Promo ─────
            PromoBanner(
                title = stringResource(R.string.promo_title),
                subtitle = stringResource(R.string.promo_sub),
                onClick = { onNavigate(Screen.ImageGen.route) }
            )

            // ───── Recent chats ─────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.recent_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = cs.onBackground,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { onNavigate(Routes.HISTORY) }) {
                    Text(
                        text = stringResource(R.string.recent_view_all),
                        color = cs.secondary
                    )
                }
            }
            if (state.recentChats.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        RobotMascot(Modifier.size(64.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = stringResource(R.string.recent_empty_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = cs.onSurface
                            )
                            Text(
                                text = stringResource(R.string.recent_empty_sub, state.assistantName),
                                style = MaterialTheme.typography.bodySmall,
                                color = cs.onSurfaceVariant
                            )
                            AgentChip(
                                text = stringResource(R.string.recent_say_hello),
                                onClick = { onNavigate(Screen.Chat.route) }
                            )
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.recentChats.take(3).forEach { chat ->
                        RecentChatRow(
                            title = chat.title,
                            preview = chat.preview,
                            time = formatChatTime(context, chat.updatedAt),
                            onClick = { onNavigate(Routes.chat(chat.id)) }
                        )
                    }
                }
            }
        }
    }

    if (showNameDialog) {
        AgentDialog(
            title = stringResource(R.string.home_name_dialog_title),
            onDismiss = { showNameDialog = false },
            icon = Icons.Rounded.Person,
            confirmText = stringResource(R.string.common_save),
            onConfirm = {
                viewModel.onEvent(HomeEvent.SetUserName(nameDraft))
                showNameDialog = false
            },
            dismissText = stringResource(R.string.common_cancel)
        ) {
            AgentTextField(
                value = nameDraft,
                onValueChange = { nameDraft = it.take(30) },
                label = stringResource(R.string.home_name_label),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

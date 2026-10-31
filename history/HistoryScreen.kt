package com.aiagent.assistant.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DriveFileRenameOutline
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aiagent.assistant.domain.chat.ChatFolder
import com.aiagent.assistant.domain.chat.ChatSummary
import com.aiagent.assistant.domain.chat.toPreview
import com.aiagent.assistant.presentation.chat.formatChatTime
import com.aiagent.assistant.presentation.components.AgentChip
import com.aiagent.assistant.presentation.components.AgentDialog
import com.aiagent.assistant.presentation.components.AgentIconButton
import com.aiagent.assistant.presentation.components.AgentTextField
import com.aiagent.assistant.presentation.components.ConfirmActionDialog
import com.aiagent.assistant.presentation.components.GlassButton
import com.aiagent.assistant.presentation.components.GlassCard
import com.aiagent.assistant.presentation.components.GradientBackground
import com.aiagent.assistant.presentation.components.IconTile
import com.aiagent.assistant.presentation.components.RobotMascot
import com.aiagent.assistant.presentation.components.ScreenHeader
import com.aiagent.assistant.presentation.theme.IconTone

private sealed interface HistoryDialog {
    data class Rename(val chat: ChatSummary) : HistoryDialog
    data class Move(val chat: ChatSummary) : HistoryDialog
    data class Delete(val chat: ChatSummary) : HistoryDialog
    data object NewFolder : HistoryDialog
    data class RenameFolder(val folder: ChatFolder) : HistoryDialog
    data class DeleteFolder(val folder: ChatFolder) : HistoryDialog
}

@Composable
fun HistoryScreen(
    onOpenChat: (Long) -> Unit,
    onNewChat: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val cs = MaterialTheme.colorScheme
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var dialog by remember { mutableStateOf<HistoryDialog?>(null) }
    var draft by remember { mutableStateOf("") }

    val selectedFolder = state.folders.firstOrNull { it.id == state.folderFilter }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ScreenHeader(
                title = "History · ইতিহাস",
                subtitle = "${state.totalCount} chats",
                modifier = Modifier.padding(top = 8.dp),
                trailing = {
                    AgentIconButton(
                        icon = Icons.Rounded.Add,
                        contentDescription = "New chat",
                        onClick = onNewChat
                    )
                }
            )

            AgentTextField(
                value = state.query,
                onValueChange = { viewModel.setQuery(it) },
                label = "Search chats · চ্যাট খুঁজুন",
                leadingIcon = Icons.Rounded.Search,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            // ───── folders ─────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AgentChip(
                    text = "All · সব",
                    selected = state.folderFilter == null,
                    onClick = { viewModel.selectFolder(null) }
                )
                state.folders.forEach { folder ->
                    AgentChip(
                        text = folder.name,
                        icon = Icons.Rounded.FolderOpen,
                        selected = state.folderFilter == folder.id,
                        onClick = { viewModel.selectFolder(folder.id) }
                    )
                }
                AgentChip(
                    text = "New folder · নতুন ফোল্ডার",
                    icon = Icons.Rounded.CreateNewFolder,
                    onClick = {
                        draft = ""
                        dialog = HistoryDialog.NewFolder
                    }
                )
            }

            if (selectedFolder != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = {
                        draft = selectedFolder.name
                        dialog = HistoryDialog.RenameFolder(selectedFolder)
                    }) {
                        Text("Rename folder · নাম বদলান", color = cs.secondary)
                    }
                    TextButton(onClick = { dialog = HistoryDialog.DeleteFolder(selectedFolder) }) {
                        Text("Delete folder · মুছুন", color = cs.error)
                    }
                }
            }

            // ───── list ─────
            if (state.chats.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                ) {
                    RobotMascot(Modifier.size(88.dp))
                    Text(
                        text = if (state.totalCount == 0) {
                            "No conversations yet · এখনো কোনো কথোপকথন নেই"
                        } else {
                            "Nothing found · কিছু পাওয়া যায়নি"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = cs.onBackground
                    )
                    if (state.totalCount == 0) {
                        GlassButton(text = "Start a chat · চ্যাট শুরু করুন", onClick = onNewChat)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.chats, key = { it.id }) { chat ->
                        ChatRow(
                            chat = chat,
                            folderName = state.folders.firstOrNull { it.id == chat.folderId }?.name,
                            timeLabel = formatChatTime(context, chat.updatedAt),
                            onClick = { onOpenChat(chat.id) },
                            onRename = {
                                draft = chat.title
                                dialog = HistoryDialog.Rename(chat)
                            },
                            onPin = { viewModel.togglePin(chat) },
                            onMove = { dialog = HistoryDialog.Move(chat) },
                            onDelete = { dialog = HistoryDialog.Delete(chat) }
                        )
                    }
                }
            }
        }
    }

    // ───── dialogs ─────
    when (val d = dialog) {
        is HistoryDialog.Rename -> AgentDialog(
            title = "Rename chat · নাম বদলান",
            onDismiss = { dialog = null },
            icon = Icons.Rounded.DriveFileRenameOutline,
            confirmText = "Save · সংরক্ষণ",
            onConfirm = {
                viewModel.rename(d.chat.id, draft)
                dialog = null
            },
            dismissText = "Cancel · বাতিল"
        ) {
            AgentTextField(
                value = draft,
                onValueChange = { draft = it.take(60) },
                label = "Title · শিরোনাম",
                modifier = Modifier.fillMaxWidth()
            )
        }
        is HistoryDialog.Move -> AgentDialog(
            title = "Move to folder · ফোল্ডারে সরান",
            onDismiss = { dialog = null },
            icon = Icons.Rounded.FolderOpen,
            dismissText = "Close · বন্ধ"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassButton(
                    text = "No folder · কোনো ফোল্ডার নয়",
                    highlighted = d.chat.folderId == null,
                    onClick = {
                        viewModel.moveToFolder(d.chat.id, null)
                        dialog = null
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                state.folders.forEach { folder ->
                    GlassButton(
                        text = folder.name,
                        highlighted = d.chat.folderId == folder.id,
                        onClick = {
                            viewModel.moveToFolder(d.chat.id, folder.id)
                            dialog = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        is HistoryDialog.Delete -> ConfirmActionDialog(
            title = "Delete this chat? · চ্যাট মুছবেন?",
            message = "এই চ্যাটের সব বার্তা মুছে যাবে, ফিরে পাওয়া যাবে না।",
            confirmText = "Delete · মুছুন",
            destructive = true,
            onConfirm = {
                viewModel.delete(d.chat.id)
                dialog = null
            },
            onDismiss = { dialog = null }
        )
        HistoryDialog.NewFolder -> AgentDialog(
            title = "New folder · নতুন ফোল্ডার",
            onDismiss = { dialog = null },
            icon = Icons.Rounded.CreateNewFolder,
            confirmText = "Create · তৈরি করুন",
            onConfirm = {
                viewModel.createFolder(draft)
                dialog = null
            },
            dismissText = "Cancel · বাতিল"
        ) {
            AgentTextField(
                value = draft,
                onValueChange = { draft = it.take(30) },
                label = "Folder name · ফোল্ডারের নাম",
                modifier = Modifier.fillMaxWidth()
            )
        }
        is HistoryDialog.RenameFolder -> AgentDialog(
            title = "Rename folder · ফোল্ডারের নাম",
            onDismiss = { dialog = null },
            icon = Icons.Rounded.DriveFileRenameOutline,
            confirmText = "Save · সংরক্ষণ",
            onConfirm = {
                viewModel.renameFolder(d.folder.id, draft)
                dialog = null
            },
            dismissText = "Cancel · বাতিল"
        ) {
            AgentTextField(
                value = draft,
                onValueChange = { draft = it.take(30) },
                label = "Folder name · ফোল্ডারের নাম",
                modifier = Modifier.fillMaxWidth()
            )
        }
        is HistoryDialog.DeleteFolder -> ConfirmActionDialog(
            title = "Delete folder? · ফোল্ডার মুছবেন?",
            message = "ফোল্ডারটা মুছবে, কিন্তু ভেতরের চ্যাটগুলো মুছবে না।",
            confirmText = "Delete · মুছুন",
            destructive = true,
            onConfirm = {
                viewModel.deleteFolder(d.folder.id)
                dialog = null
            },
            onDismiss = { dialog = null }
        )
        null -> Unit
    }
}

@Composable
private fun ChatRow(
    chat: ChatSummary,
    folderName: String?,
    timeLabel: String,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onPin: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    var menuOpen by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
        highlighted = chat.pinned,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTile(
                icon = if (chat.pinned) Icons.Rounded.PushPin else Icons.Rounded.ChatBubble,
                tone = if (chat.pinned) IconTone.ORANGE else IconTone.PURPLE,
                size = 44.dp,
                iconSize = 22.dp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.title.ifBlank { "Chat" },
                    style = MaterialTheme.typography.titleSmall,
                    color = cs.onSurface,
                    maxLines = 1
                )
                Text(
                    text = chat.lastMessage.toPreview(),
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant,
                    maxLines = 1
                )
                if (folderName != null) {
                    Text(
                        text = "📁 $folderName",
                        style = MaterialTheme.typography.labelSmall,
                        color = cs.secondary,
                        maxLines = 1
                    )
                }
            }
            Text(
                text = timeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = cs.onSurfaceVariant
            )
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Rounded.MoreVert, contentDescription = "More", tint = cs.onSurfaceVariant)
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("Rename · নাম বদলান") },
                        leadingIcon = { Icon(Icons.Rounded.DriveFileRenameOutline, contentDescription = null) },
                        onClick = {
                            menuOpen = false
                            onRename()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (chat.pinned) "Unpin · পিন সরান" else "Pin · পিন করুন") },
                        leadingIcon = { Icon(Icons.Rounded.PushPin, contentDescription = null) },
                        onClick = {
                            menuOpen = false
                            onPin()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Move to folder · ফোল্ডারে সরান") },
                        leadingIcon = { Icon(Icons.Rounded.FolderOpen, contentDescription = null) },
                        onClick = {
                            menuOpen = false
                            onMove()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete · মুছুন") },
                        leadingIcon = { Icon(Icons.Rounded.Delete, contentDescription = null) },
                        onClick = {
                            menuOpen = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

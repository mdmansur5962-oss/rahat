package com.aiagent.assistant.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiagent.assistant.domain.chat.ChatFolder
import com.aiagent.assistant.domain.chat.ChatRepository
import com.aiagent.assistant.domain.chat.ChatSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryUiState(
    val chats: List<ChatSummary> = emptyList(),
    val folders: List<ChatFolder> = emptyList(),
    val query: String = "",
    /** null = সব চ্যাট */
    val folderFilter: Long? = null,
    val totalCount: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val folderFilter = MutableStateFlow<Long?>(null)

    /** বার্তার ভেতরে খুঁজে পাওয়া চ্যাটের id */
    private val matchedIds = query.mapLatest { q ->
        if (q.isBlank()) emptySet<Long>() else repository.searchChatIds(q.trim()).toSet()
    }

    val state: StateFlow<HistoryUiState> = combine(
        repository.observeSummaries(),
        repository.observeFolders(),
        query,
        folderFilter,
        matchedIds
    ) { summaries, folders, q, folder, matched ->
        val needle = q.trim()
        val filtered = summaries.filter { s ->
            (folder == null || s.folderId == folder) &&
                (
                    needle.isEmpty() ||
                        s.title.contains(needle, ignoreCase = true) ||
                        s.lastMessage.contains(needle, ignoreCase = true) ||
                        s.id in matched
                    )
        }
        HistoryUiState(
            chats = filtered,
            folders = folders,
            query = q,
            folderFilter = folder,
            totalCount = summaries.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())

    fun setQuery(text: String) {
        query.value = text
    }

    fun selectFolder(id: Long?) {
        folderFilter.value = id
    }

    fun rename(chatId: Long, title: String) {
        val clean = title.trim().take(60)
        if (clean.isEmpty()) return
        viewModelScope.launch { repository.renameChat(chatId, clean) }
    }

    fun togglePin(chat: ChatSummary) {
        viewModelScope.launch { repository.setPinned(chat.id, !chat.pinned) }
    }

    fun moveToFolder(chatId: Long, folderId: Long?) {
        viewModelScope.launch { repository.setFolder(chatId, folderId) }
    }

    fun delete(chatId: Long) {
        viewModelScope.launch { repository.deleteChat(chatId) }
    }

    fun createFolder(name: String) {
        val clean = name.trim().take(30)
        if (clean.isEmpty()) return
        viewModelScope.launch { repository.createFolder(clean) }
    }

    fun renameFolder(id: Long, name: String) {
        val clean = name.trim().take(30)
        if (clean.isEmpty()) return
        viewModelScope.launch { repository.renameFolder(id, clean) }
    }

    fun deleteFolder(id: Long) {
        if (folderFilter.value == id) folderFilter.value = null
        viewModelScope.launch { repository.deleteFolder(id) }
    }
}

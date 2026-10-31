package com.aiagent.assistant.presentation.home

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.aiagent.assistant.R
import com.aiagent.assistant.core.mvi.BaseViewModel
import com.aiagent.assistant.core.mvi.UiEffect
import com.aiagent.assistant.core.mvi.UiEvent
import com.aiagent.assistant.core.mvi.UiState
import com.aiagent.assistant.data.local.AppPreferences
import com.aiagent.assistant.data.local.SettingsRepository
import com.aiagent.assistant.domain.chat.ChatRepository
import com.aiagent.assistant.domain.chat.RecentChat
import com.aiagent.assistant.domain.chat.toPreview
import com.aiagent.assistant.presentation.components.OrbState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "",
    val appName: String = "MYRA",
    val assistantName: String = "MYRA",
    val orbState: OrbState = OrbState.IDLE,
    /** Step 6-এ Room থেকে আসবে; এখন খালি থাকে */
    val recentChats: List<RecentChat> = emptyList()
) : UiState

sealed interface HomeEvent : UiEvent {
    data object OrbTapped : HomeEvent
    data object TalkClicked : HomeEvent
    data object BellClicked : HomeEvent
    data class SetUserName(val name: String) : HomeEvent
}

sealed interface HomeEffect : UiEffect {
    data class ShowMessage(@StringRes val messageRes: Int) : HomeEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefs: AppPreferences,
    private val settingsRepository: SettingsRepository,
    private val chatRepository: ChatRepository
) : BaseViewModel<HomeUiState, HomeEvent, HomeEffect>(HomeUiState()) {

    private var orbJob: Job? = null

    init {
        viewModelScope.launch {
            prefs.userName.collect { name ->
                setState { copy(userName = name) }
            }
        }
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                setState { copy(appName = settings.appName, assistantName = settings.assistantName) }
            }
        }
        viewModelScope.launch {
            chatRepository.observeSummaries().collect { summaries ->
                val recent = summaries.take(3).map { s ->
                    RecentChat(
                        id = s.id,
                        title = s.title.ifBlank { "Chat" },
                        preview = s.lastMessage.toPreview(),
                        updatedAt = s.updatedAt
                    )
                }
                setState { copy(recentChats = recent) }
            }
        }
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.OrbTapped, HomeEvent.TalkClicked -> showListeningDemo()
            HomeEvent.BellClicked -> sendEffect(HomeEffect.ShowMessage(R.string.msg_notifications_soon))
            is HomeEvent.SetUserName -> {
                val clean = event.name.trim().take(30)
                viewModelScope.launch { prefs.setUserName(clean) }
            }
        }
    }

    /** Step 9-এ আসল voice আসবে; এখন শুধু orb-এর "শুনছে" চেহারা কয়েক সেকেন্ড দেখায় */
    private fun showListeningDemo() {
        orbJob?.cancel()
        setState { copy(orbState = OrbState.LISTENING) }
        sendEffect(HomeEffect.ShowMessage(R.string.msg_voice_soon))
        orbJob = viewModelScope.launch {
            delay(2500)
            setState { copy(orbState = OrbState.IDLE) }
        }
    }
}
